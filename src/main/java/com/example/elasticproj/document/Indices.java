package com.example.elasticproj.document;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Indices {
    TESTING("testing"),
    NEWSPAPER("newspaper"),
    ARTICLES("articles");

    private String nameValue;
}
