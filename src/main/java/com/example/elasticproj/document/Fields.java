package com.example.elasticproj.document;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Fields {
    ID("id"),
    NAME("name"),
    SUMMARY("summary"),
    CONTENT("content"),
    PUBLISH_YEAR("publishYear");

    private String nameValue;
}
