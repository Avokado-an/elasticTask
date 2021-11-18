package com.example.elasticproj.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Article {
    @Id
    private String id;
    private String name;
    private String summary;
    private String content;
    private int publishYear;
}
