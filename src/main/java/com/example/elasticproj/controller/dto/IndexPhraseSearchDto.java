package com.example.elasticproj.controller.dto;

import lombok.Data;

@Data
public class IndexPhraseSearchDto {
    private String indexName;
    private String[] fields;
    private String phrase;
}
