package com.example.elasticproj.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AdvancedIndexSearchDto {
    private List<IndexPhraseSearchDto> indexUniqueSearchedPhrases;
}
