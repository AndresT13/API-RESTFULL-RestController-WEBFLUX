package com.webflux.restfull.model.dao;


import com.webflux.restfull.documents.Categoria;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CategoriaDao extends ReactiveMongoRepository<Categoria, String> {
}
