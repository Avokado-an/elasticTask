package com.example.elasticproj.service;

import com.example.elasticproj.controller.dto.AdvancedIndexSearchDto;
import com.example.elasticproj.controller.dto.AggregationDto;
import com.example.elasticproj.controller.dto.FieldSearchDto;
import com.example.elasticproj.controller.dto.IndexPhraseSearchDto;
import com.example.elasticproj.document.Article;
import com.example.elasticproj.document.Fields;
import com.example.elasticproj.document.Indices;
import com.example.elasticproj.service.aggregation.AggregationActionProvider;
import com.example.elasticproj.service.util.ElasticPageableUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.Max;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ElasticService {
    private final AggregationActionProvider aggregationActionProvider;
    private final RestHighLevelClient elasticClient;
    private final ObjectMapper objectMapper;
    private final List<String> existingIndices = Arrays.asList(Indices.ARTICLES.getNameValue(),
            Indices.NEWSPAPER.getNameValue(), Indices.TESTING.getNameValue());
    private final List<String> existingFields = Arrays.asList(Fields.PUBLISH_YEAR.getNameValue(),
            Fields.ID.getNameValue(), Fields.NAME.getNameValue(), Fields.CONTENT.getNameValue(),
            Fields.SUMMARY.getNameValue());

    public Article createArticle(String typeName, String id, Article article) throws IOException {
        IndexRequest indexRequest = new IndexRequest(typeName);
        indexRequest.id(id);
        article.setId(id);
        indexRequest.source(objectMapper.writeValueAsString(article), XContentType.JSON);
        elasticClient.index(indexRequest, RequestOptions.DEFAULT);
        return article;
    }

    public Page<Map<String, Object>> searchAllInIndex(String typeName, Pageable pageable) throws IOException {
        return searchAllInType(pageable, createSearchRequest(typeName));
    }

    public Page<Map<String, Object>> searchAllInSeveralIndices(List<String> typeNames, Pageable pageable) throws IOException {
        return searchAllInType(pageable, createSearchRequest(typeNames));
    }

    public Page<Map<String, Object>> searchAllInAllIndices(Pageable pageable) throws IOException {
        return searchAllInType(pageable, createSearchRequest());
    }

    public Page<Map<String, Object>> aggregateForIndices(AggregationDto aggregationDto, Pageable pageable) throws IOException {
        AggregationBuilder aggregationBuilder = aggregationActionProvider.provideAggregationActionExecution(aggregationDto);
        SearchRequest searchRequest = createSearchRequest(aggregationDto.getIndices());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.aggregation(aggregationBuilder);
        searchRequest.source(searchSourceBuilder);
        return searchByAggregatedSearchRequest(searchRequest, pageable);
    }

    public Page<Map<String, Object>> searchByFieldsInIndices(List<String> typeNames, FieldSearchDto fieldSearchDto,
                                                             Pageable pageable) throws IOException {
        SearchRequest searchRequest = createSearchRequest(typeNames);
        return searchByPhraseInFields(fieldSearchDto.getFields(),
                searchRequest,
                fieldSearchDto.getSearchedPhrase(),
                pageable
        );
    }

    public Page<Map<String, Object>> searchByFieldsInIndex(String typeName, FieldSearchDto fieldSearchDto,
                                                           Pageable pageable) throws IOException {
        SearchRequest searchRequest = createSearchRequest(typeName);
        return searchByPhraseInFields(fieldSearchDto.getFields(),
                searchRequest,
                fieldSearchDto.getSearchedPhrase(),
                pageable
        );
    }

    //todo do it properly without listing pages
    //public List<Page<Map<String, Object>>> searchByUniqueForIndexPhrase(AdvancedIndexSearchDto advancedIndexSearchDto,
    //                                                                    Pageable pageable) throws IOException {
    //    List<Page<Map<String, Object>>> articles = new ArrayList<>();
    //    for (IndexPhraseSearchDto indexPhraseSearchDto : advancedIndexSearchDto.getIndexUniqueSearchedPhrases()) {
    //        SearchRequest searchRequest = createSearchRequest(indexPhraseSearchDto.getIndexName());
    //        articles.add(searchByPhraseInFields(
    //                indexPhraseSearchDto.getFields(),
    //                searchRequest,
    //                indexPhraseSearchDto.getPhrase(),
    //                pageable)
    //        );
    //    }
    //    return articles;
    //}

    public Page<Map<String, Object>> searchLatestByFieldsInIndices(List<String> typeNames, FieldSearchDto fieldSearchDto,
                                                                   Pageable pageable) throws IOException {
        String[] typeNamesArray = arrayFromList(typeNames);
        SearchRequest searchRequest = new SearchRequest(typeNamesArray);
        return searchLatestByPhraseInFields(fieldSearchDto.getFields(),
                searchRequest,
                fieldSearchDto.getSearchedPhrase(),
                pageable
        );
    }

    public Page<Map<String, Object>> searchLatestByFieldsInIndex(String typeName, FieldSearchDto fieldSearchDto,
                                                                 Pageable pageable) throws IOException {
        SearchRequest searchRequest = createSearchRequest(typeName);
        return searchLatestByPhraseInFields(fieldSearchDto.getFields(),
                searchRequest,
                fieldSearchDto.getSearchedPhrase(),
                pageable
        );
    }

    private String[] arrayFromList(List<String> typeNames) {
        String[] typeNamesArray = new String[typeNames.size()];
        typeNames.toArray(typeNamesArray);
        return typeNamesArray;
    }

    private Page<Map<String, Object>> searchLatestByPhraseInFields(List<String> fields, SearchRequest searchRequest,
                                                                   String searchedPhrase, Pageable pageable) throws IOException {
        SearchSourceBuilder searchSourceBuilder = ElasticPageableUtils.fromPageable(pageable);
        String[] fieldsArray = arrayFromList(fields);
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery(searchedPhrase, fieldsArray));
        searchSourceBuilder.aggregation(AggregationBuilders.max("maxPublishYear").field("publishYear"));
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = elasticClient.search(searchRequest, RequestOptions.DEFAULT);
        Max max = response.getAggregations().get("maxPublishYear");
        return searchByPhraseInFields(List.of("publishYear"), searchRequest, String.valueOf(max.getValue()), pageable);
    }

    private SearchRequest createSearchRequest(String typeName) throws IOException {
        if (existingIndices.contains(typeName)) {
            return new SearchRequest(typeName);
        } else {
            throw new IOException("Invalid index name");
        }
    }

    private SearchRequest createSearchRequest() {
        String[] typeNamesArray = arrayFromList(existingIndices);
        return new SearchRequest(typeNamesArray);
    }

    private SearchRequest createSearchRequest(List<String> typeNames) throws IOException {
        if (existingIndices.containsAll(typeNames)) {
            String[] typeNamesArray = arrayFromList(typeNames);
            return new SearchRequest(typeNamesArray);
        } else {
            throw new IOException("Invalid index name");
        }
    }

    private Page<Map<String, Object>> searchByPhraseInFields(List<String> fields, SearchRequest searchRequest,
                                                             String searchedPhrase, Pageable pageable) throws IOException {
        SearchSourceBuilder searchSourceBuilder = ElasticPageableUtils.fromPageable(pageable);
        if (checkIfFieldsExist(fields)) {
            String[] fieldsArray = arrayFromList(fields);
            searchSourceBuilder.query(QueryBuilders.multiMatchQuery(searchedPhrase, fieldsArray));
            searchRequest.source(searchSourceBuilder);
            return searchBySearchRequest(searchRequest, pageable);
        } else {
            throw new IOException("invalid field name");
        }
    }

    private boolean checkIfFieldsExist(List<String> fields) {
        return existingFields.containsAll(fields);
    }

    private Page<Map<String, Object>> searchBySearchRequest(SearchRequest searchRequest,
                                                            Pageable pageable) throws IOException {
        SearchResponse response = elasticClient.search(searchRequest, RequestOptions.DEFAULT);
        int total = 10;
        return ElasticPageableUtils.buildPage(response, pageable, total);
    }

    private Page<Map<String, Object>> searchByAggregatedSearchRequest(SearchRequest searchRequest,
                                                                      Pageable pageable) throws IOException {
        SearchResponse response = elasticClient.search(searchRequest, RequestOptions.DEFAULT);
        int total = 10;
        return ElasticPageableUtils.buildAggregatedPage(response, pageable, total);
    }

    private Page<Map<String, Object>> searchAllInType(Pageable pageable, SearchRequest searchRequest) throws IOException {
        SearchSourceBuilder searchSourceBuilder = ElasticPageableUtils.fromPageable(pageable);
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        return searchBySearchRequest(searchRequest, pageable);
    }

    //private Article mapElasticResponseToArticle(SearchHit hit) {
    //    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
    //    return Article.builder()
    //            .content((String) sourceAsMap.get("content"))
    //            .name((String) sourceAsMap.get("name"))
    //            .summary((String) sourceAsMap.get("summary"))
    //            .id((String) sourceAsMap.get("id"))
    //            .publishYear((Integer) sourceAsMap.get("publishYear"))
    //            .build();
    //}
}

