package com.example.elasticproj.repository;

import com.example.elasticproj.document.Article;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticRepository extends ElasticsearchRepository<Article, String> {
}
