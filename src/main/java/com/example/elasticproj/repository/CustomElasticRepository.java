package com.example.elasticproj.repository;

public interface CustomElasticRepository<T> {
    <S extends T> S save(S entity);
    <S extends T> Iterable<S> saveAll(Iterable<S> entities);
}
