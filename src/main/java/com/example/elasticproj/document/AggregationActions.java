package com.example.elasticproj.document;

import com.example.elasticproj.service.aggregation.AggregationAction;
import com.example.elasticproj.service.aggregation.impl.AggregationAvgAction;
import com.example.elasticproj.service.aggregation.impl.AggregationCountAction;
import com.example.elasticproj.service.aggregation.impl.AggregationMaxAction;
import com.example.elasticproj.service.aggregation.impl.AggregationMinAction;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AggregationActions { //todo there lots of them so need to have them listed or something
    AVG(new AggregationAvgAction()),
    MAX(new AggregationMaxAction()),
    MIN(new AggregationMinAction()),
    COUNT(new AggregationCountAction());

    private AggregationAction aggregationAction;
}
