package com.example.elasticproj.controller;

import com.example.elasticproj.controller.dto.AdvancedIndexSearchDto;
import com.example.elasticproj.controller.dto.FieldSearchDto;
import com.example.elasticproj.document.Article;
import com.example.elasticproj.service.ElasticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class ElasticController {
    private final ElasticService elasticService;

    @GetMapping
    public List<Article> findAllArticlesForSeveralIndices(@RequestParam(name = "typeNames") String[] typeNames) throws IOException {
        return elasticService.searchAllInSeveralIndices(typeNames);
    }

    @GetMapping(value = "/{typeName}")
    public List<Article> findAllArticlesFromIndex(@PathVariable String typeName) throws IOException {
        return elasticService.searchAllInIndex(typeName);
    }

    @PostMapping
    public List<Article> searchArticlesForSeveralIndices(@RequestParam(name = "typeNames") String[] typeNames,
                                                         @RequestBody FieldSearchDto searchDto) throws IOException {
        return elasticService.searchByFieldsInIndices(typeNames, searchDto);
    }

    @PostMapping(value = "/{typeName}")
    public List<Article> searchArticlesFromIndex(@PathVariable String typeName,
                                                 @RequestBody FieldSearchDto searchDto) throws IOException {
        return elasticService.searchByFieldsInIndex(typeName, searchDto);
    }

    @PostMapping(value = "/advanced-search")
    public List<Article> searchArticlesByUniquePhraseForIndex(@RequestBody AdvancedIndexSearchDto advancedIndexSearchDto) throws IOException {
        return elasticService.searchByUniqueForIndexPhrase(advancedIndexSearchDto);
    }

    @PostMapping("/latest")
    public List<Article> searchLatestArticleForSeveralIndices(@RequestParam(name = "typeNames") String[] typeNames,
                                                         @RequestBody FieldSearchDto searchDto) throws IOException {
        return elasticService.searchLatestByFieldsInIndices(typeNames, searchDto);
    }

    @PostMapping(value = "/{typeName}/latest")
    public List<Article> searchLatestArticleFromIndex(@PathVariable String typeName,
                                                 @RequestBody FieldSearchDto searchDto) throws IOException {
        return elasticService.searchLatestByFieldsInIndex(typeName, searchDto);
    }

    @PostMapping(value = "/{typeName}/{id}")
    public Article createArticle(@PathVariable String typeName, @PathVariable String id,
                                 @RequestBody Article article) throws IOException {
        return elasticService.createArticle(typeName, id, article);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ResponseEntity<String>> handleTakenUsernameExceptions(IOException ex) {
        return ResponseEntity.status(403).body(ResponseEntity.status(403).body(ex.getMessage()));
    }
}
