package com.example.elasticproj.controller.dto;

import lombok.Data;

@Data
public class FieldSearchDto {
    private String[] fields;
    private String searchedPhrase;
    private Boolean sortByYear;
}
