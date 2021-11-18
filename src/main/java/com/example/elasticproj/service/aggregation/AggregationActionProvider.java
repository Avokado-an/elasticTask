package com.example.elasticproj.service.aggregation;

import com.example.elasticproj.controller.dto.AggregationDto;
import com.example.elasticproj.document.AggregationActions;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.springframework.stereotype.Component;

@Component
public class AggregationActionProvider {
    public AggregationBuilder provideAggregationActionExecution(AggregationDto aggregationDto) {
        AggregationAction action;
        try {
            action = AggregationActions.valueOf(aggregationDto.getAggregationAction().toUpperCase()).getAggregationAction();
        } catch (IllegalArgumentException e) {
            throw new ElasticsearchException("invalid aggregation parameters: Invalid action name");
        }
        return action.getAggregation(aggregationDto.getAggregationFieldName(), aggregationDto.getTargetField());
    }
}
