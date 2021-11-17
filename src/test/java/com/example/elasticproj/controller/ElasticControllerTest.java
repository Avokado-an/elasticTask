package com.example.elasticproj.controller;

import com.example.elasticproj.service.ElasticService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ElasticControllerTest {
    private final ElasticService elasticController;

    public ElasticControllerTest(ElasticService elasticService) {
        this.elasticController = elasticService;
    }

    @Test
    public void findAllArticlesForSeveralIndicesValidTest() {
    }

    @Test
    public void findAllArticlesFromIndexValidTest() {
    }

    @Test
    public void searchArticlesForSeveralIndicesValidTest() {
    }

    @Test
    public void searchArticlesFromIndexValidTest() {
    }

    @Test
    public void searchArticlesByUniquePhraseForIndexValidTest() {
    }

    @Test
    public void searchLatestArticleForSeveralIndicesValidTest() {
    }

    @Test
    public void searchLatestArticleFromIndexValidTest() {
    }

    @Test
    public void createArticleValidTest() {
    }

    @Test
    public void findAllArticlesForSeveralIndicesInvalidTest() {
    }

    @Test
    public void findAllArticlesFromIndexInvalidTest() {
    }

    @Test
    public void searchArticlesForSeveralIndicesInvalidTest() {
    }

    @Test
    public void searchArticlesFromIndexInvalidTest() {
    }

    @Test
    public void searchArticlesByUniquePhraseForIndexInvalidTest() {
    }

    @Test
    public void searchLatestArticleForSeveralIndicesInvalidTest() {
    }

    @Test
    public void searchLatestArticleFromIndexInvalidTest() {
    }

    @Test
    public void createArticleInvalidTest() {
    }
}
