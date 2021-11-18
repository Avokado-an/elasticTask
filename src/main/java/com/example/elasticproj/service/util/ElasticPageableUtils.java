package com.example.elasticproj.service.util;

import lombok.experimental.UtilityClass;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author s.vareyko
 * @since 17.11.2021
 */
@UtilityClass
public class ElasticPageableUtils {

    /**
     * The helper method takes care of the conversion from {@link Pageable} to {@link SearchSourceBuilder} with pagination details.
     *
     * @param pageable to be converted
     * @return builder with pagination details
     */
    public SearchSourceBuilder fromPageable(final Pageable pageable) throws IOException {
        final SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource()
                .size(pageable.getPageSize())
                .from(Math.toIntExact(pageable.getOffset()));
        for (Sort.Order order : pageable.getSort()) {
            sourceBuilder.sort(order.getProperty(), SortOrder.valueOf(order.getDirection().toString()));
        }
        return sourceBuilder;
    }

    public Page<Map<String, Object>> buildPage(final SearchResponse response, final Pageable pageable, final long total) {
        final List<Map<String, Object>> contents = retrieveResponseHitsContent(response);
        return new PageImpl<>(contents, pageable, total);
    }

    public Page<Map<String, Object>> buildAggregatedPage(final SearchResponse response, final Pageable pageable, final long total) {
        final List<Map<String, Object>> contents = retrieveResponseHitsContent(response);
        final Map<String, Object> aggregations = Collections.unmodifiableMap(response.getAggregations().asMap());
        contents.add(aggregations);
        return new PageImpl<>(contents, pageable, total);
    }

    private static List<Map<String, Object>> retrieveResponseHitsContent(SearchResponse response) {
        final List<Map<String, Object>> contents = new LinkedList<>();
        for (SearchHit hit : response.getHits()) {
            contents.add(hit.getSourceAsMap());
        }
        return contents;
    }
}
