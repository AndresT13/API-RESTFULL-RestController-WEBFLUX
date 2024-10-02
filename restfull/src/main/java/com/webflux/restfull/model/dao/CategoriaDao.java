package com.webflux.restfull.model.dao;


import com.webflux.restfull.documents.Categoria;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface CategoriaDao extends ReactiveMongoRepository<Categoria, String> {

   public Mono<Categoria> findByNombre(String nombre);

}
