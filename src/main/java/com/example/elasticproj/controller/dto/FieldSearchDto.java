package com.example.elasticproj.controller.dto;

import lombok.Data;

import java.util.List;

@Data
public class FieldSearchDto {
    private List<String> fields;
    private String searchedPhrase;
    private Boolean sortByYear;
}
