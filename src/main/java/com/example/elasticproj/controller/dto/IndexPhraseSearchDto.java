package com.example.elasticproj.controller.dto;

import lombok.Data;

import java.util.List;

@Data
public class IndexPhraseSearchDto {
    private String indexName;
    private List<String> fields;
    private String phrase;
}
