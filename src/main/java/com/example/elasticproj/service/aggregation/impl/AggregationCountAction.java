package com.example.elasticproj.service.aggregation.impl;

import com.example.elasticproj.service.aggregation.AggregationAction;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;

public class AggregationCountAction implements AggregationAction {
    @Override
    public AggregationBuilder getAggregation(String aggregationField, String targetField) {
        return AggregationBuilders.count(aggregationField).field(targetField);
    }
}
