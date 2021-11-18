package com.example.elasticproj.controller.dto;

import lombok.Data;

import java.util.List;

@Data
public class AggregationDto {
    private List<String> indices;
    private String aggregationFieldName;
    private String targetField;
    private String aggregationAction;
}
