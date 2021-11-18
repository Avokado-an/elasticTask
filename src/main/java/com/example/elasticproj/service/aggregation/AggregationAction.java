package com.example.elasticproj.service.aggregation;

import org.elasticsearch.search.aggregations.AggregationBuilder;

public interface AggregationAction {
    AggregationBuilder getAggregation(String aggregationField, String targetField);
}
