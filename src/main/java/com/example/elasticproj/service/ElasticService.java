package com.example.elasticproj.service;

import com.example.elasticproj.controller.dto.AdvancedIndexSearchDto;
import com.example.elasticproj.controller.dto.FieldSearchDto;
import com.example.elasticproj.controller.dto.IndexPhraseSearchDto;
import com.example.elasticproj.document.Article;
import com.example.elasticproj.document.Fields;
import com.example.elasticproj.document.Indices;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.Max;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElasticService {
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

    public List<Article> searchAllInIndex(String typeName) throws IOException {
        SearchRequest searchRequest = createSearchRequest(typeName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        return searchArticlesBySearchRequest(searchRequest);
    }

    public List<Article> searchAllInSeveralIndices(String[] typeNames) throws IOException {
        SearchRequest searchRequest = createSearchRequest(typeNames);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        return searchArticlesBySearchRequest(searchRequest);
    }

    public List<Article> searchByFieldsInIndices(String[] typeNames, FieldSearchDto fieldSearchDto) throws IOException {
        SearchRequest searchRequest = createSearchRequest(typeNames);
        return searchByPhraseInFields(fieldSearchDto.getFields(),
                searchRequest,
                fieldSearchDto.getSearchedPhrase(),
                fieldSearchDto.getSortByYear()
        );
    }

    public List<Article> searchByFieldsInIndex(String typeName, FieldSearchDto fieldSearchDto) throws IOException {
        SearchRequest searchRequest = createSearchRequest(typeName);
        return searchByPhraseInFields(fieldSearchDto.getFields(),
                searchRequest,
                fieldSearchDto.getSearchedPhrase(),
                fieldSearchDto.getSortByYear()
        );
    }

    public List<Article> searchByUniqueForIndexPhrase(AdvancedIndexSearchDto advancedIndexSearchDto) throws IOException {
        List<Article> articles = new ArrayList<>();
        for (IndexPhraseSearchDto indexPhraseSearchDto : advancedIndexSearchDto.getIndexUniqueSearchedPhrases()) {
            SearchRequest searchRequest = createSearchRequest(indexPhraseSearchDto.getIndexName());
            articles.addAll(searchByPhraseInFields(
                    indexPhraseSearchDto.getFields(),
                    searchRequest,
                    indexPhraseSearchDto.getPhrase(),
                    false)
            );
        }
        return articles;
    }

    public List<Article> searchLatestByFieldsInIndices(String[] typeNames, FieldSearchDto fieldSearchDto) throws IOException {
        SearchRequest searchRequest = new SearchRequest(typeNames);
        return searchLatestByPhraseInFields(fieldSearchDto.getFields(),
                searchRequest,
                fieldSearchDto.getSearchedPhrase(),
                fieldSearchDto.getSortByYear()
        );
    }

    public List<Article> searchLatestByFieldsInIndex(String typeName, FieldSearchDto fieldSearchDto) throws IOException {
        SearchRequest searchRequest = createSearchRequest(typeName);
        return searchLatestByPhraseInFields(fieldSearchDto.getFields(),
                searchRequest,
                fieldSearchDto.getSearchedPhrase(),
                fieldSearchDto.getSortByYear()
        );
    }

    private List<Article> searchLatestByPhraseInFields(String[] fields, SearchRequest searchRequest,
                                                       String searchedPhrase, Boolean sortByYear) throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery(searchedPhrase, fields));
        searchSourceBuilder.aggregation(AggregationBuilders.max("maxPublishYear").field("publishYear"));
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = elasticClient.search(searchRequest, RequestOptions.DEFAULT);
        Max max = response.getAggregations().get("maxPublishYear");
        String[] field = {"publishYear"};
        return searchByPhraseInFields(field, searchRequest, String.valueOf(max.getValue()), sortByYear);
    }

    private SearchRequest createSearchRequest(String typeName) throws IOException {
        if(existingIndices.contains(typeName)) {
            return new SearchRequest(typeName);
        } else {
            throw new IOException("Invalid index name");
        }
    }

    private SearchRequest createSearchRequest(String[] typeNames) throws IOException {
        if(existingIndices.containsAll(Arrays.stream(typeNames).collect(Collectors.toList()))) {
            return new SearchRequest(typeNames);
        } else {
            throw new IOException("Invalid index name");
        }
    }

    private List<Article> searchByPhraseInFields(String[] fields, SearchRequest searchRequest,
                                                 String searchedPhrase, Boolean sortByYear) throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        if(checkIfFieldsExist(fields)) {
            searchSourceBuilder.query(QueryBuilders.multiMatchQuery(searchedPhrase, fields));
            if (sortByYear) {
                searchSourceBuilder.sort(SortBuilders.fieldSort("publishYear").order(SortOrder.DESC));
            }
            searchRequest.source(searchSourceBuilder);
            return searchArticlesBySearchRequest(searchRequest);
        } else {
            throw new IOException("invalid field name");
        }
    }

    private boolean checkIfFieldsExist(String[] fields) {
        return existingFields.containsAll(Arrays.stream(fields).collect(Collectors.toList()));
    }

    private List<Article> searchArticlesBySearchRequest(SearchRequest searchRequest) throws IOException {
        SearchResponse response = elasticClient.search(searchRequest, RequestOptions.DEFAULT);
        List<Article> articles = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            articles.add(mapElasticResponseToArticle(hit));
        }
        return articles;
    }

    private Article mapElasticResponseToArticle(SearchHit hit) {
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
        return Article.builder()
                .content((String) sourceAsMap.get("content"))
                .name((String) sourceAsMap.get("name"))
                .summary((String) sourceAsMap.get("summary"))
                .id((String) sourceAsMap.get("id"))
                .publishYear((Integer) sourceAsMap.get("publishYear"))
                .build();
    }
}

