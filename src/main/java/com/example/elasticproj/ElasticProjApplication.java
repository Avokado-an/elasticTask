package com.example.elasticproj;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.client.ElasticsearchClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

@SpringBootApplication
public class ElasticProjApplication {
    @Value("${elastic.host}")
    private String elasticsearchHost;

    public static void main(String[] args) {
        SpringApplication.run(ElasticProjApplication.class, args);
    }

    @Bean
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration clientConfiguration =
                ClientConfiguration.builder().connectedTo(elasticsearchHost).build();
        return RestClients.create(clientConfiguration).rest();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
