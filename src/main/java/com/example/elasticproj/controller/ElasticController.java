package com.example.elasticproj.controller;

import com.example.elasticproj.controller.dto.AdvancedIndexSearchDto;
import com.example.elasticproj.controller.dto.AggregationDto;
import com.example.elasticproj.controller.dto.FieldSearchDto;
import com.example.elasticproj.document.Article;
import com.example.elasticproj.service.ElasticService;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.ElasticsearchException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class ElasticController {
    private final ElasticService elasticService;

    @GetMapping(value = "/indices")
    public Page<Map<String, Object>> findAllArticlesForSeveralIndices(@RequestParam(name = "typeNames") List<String> typeNames,
                                                                      Pageable pageable) throws IOException {
        return elasticService.searchAllInSeveralIndices(typeNames, pageable);
    }

    @GetMapping
    public Page<Map<String, Object>> findAllArticlesFromIndex(Pageable pageable) throws IOException {
        return elasticService.searchAllInAllIndices(pageable);
    }

    @GetMapping(value = "/indices/{typeName}")
    public Page<Map<String, Object>> findAllArticlesFromIndex(@PathVariable String typeName,
                                                              Pageable pageable) throws IOException {
        return elasticService.searchAllInIndex(typeName, pageable);
    }

    @PostMapping(value = "indices/aggregate")
    public Page<Map<String, Object>> aggregateArticles(Pageable pageable,
                                                       @RequestBody AggregationDto aggregationDto) throws IOException {
        return elasticService.aggregateForIndices(aggregationDto, pageable);
    }

    @PostMapping
    public Page<Map<String, Object>> searchArticlesForSeveralIndices(@RequestParam(name = "typeNames") List<String> typeNames,
                                                                     @RequestBody FieldSearchDto searchDto,
                                                                     Pageable pageable) throws IOException {
        return elasticService.searchByFieldsInIndices(typeNames, searchDto, pageable);
    }

    @PostMapping(value = "/{typeName}")
    public Page<Map<String, Object>> searchArticlesFromIndex(@PathVariable String typeName,
                                                             @RequestBody FieldSearchDto searchDto,
                                                             Pageable pageable) throws IOException {
        return elasticService.searchByFieldsInIndex(typeName, searchDto, pageable);
    }

    @PostMapping(value = "/advanced-search")
    public List<Page<Map<String, Object>>> searchArticlesByUniquePhraseForIndex(@RequestBody AdvancedIndexSearchDto advancedIndexSearchDto,
                                                                                Pageable pageable) throws IOException {
        return elasticService.searchByUniqueForIndexPhrase(advancedIndexSearchDto, pageable);
    }

    @PostMapping("/latest")
    public Page<Map<String, Object>> searchLatestArticleForSeveralIndices(@RequestParam(name = "typeNames") List<String> typeNames,
                                                                          @RequestBody FieldSearchDto searchDto,
                                                                          Pageable pageable) throws IOException {
        return elasticService.searchLatestByFieldsInIndices(typeNames, searchDto, pageable);
    }

    @PostMapping(value = "/{typeName}/latest")
    public Page<Map<String, Object>> searchLatestArticleFromIndex(@PathVariable String typeName,
                                                                  @RequestBody FieldSearchDto searchDto,
                                                                  Pageable pageable) throws IOException {
        return elasticService.searchLatestByFieldsInIndex(typeName, searchDto, pageable);
    }

    @PostMapping(value = "/{typeName}/{id}")
    public Article createArticle(@PathVariable String typeName, @PathVariable String id,
                                 @RequestBody Article article) throws IOException {
        return elasticService.createArticle(typeName, id, article);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ResponseEntity<String>> handleIOException(IOException ex) {
        return ResponseEntity.status(403).body(ResponseEntity.status(403).body(ex.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ElasticsearchException.class)
    public ResponseEntity<ResponseEntity<String>> handleElasticsearchException(ElasticsearchException ex) {
        return ResponseEntity.status(403).body(ResponseEntity.status(403).body(ex.getDetailedMessage()));
    }
}
