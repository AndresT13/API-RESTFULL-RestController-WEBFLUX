package com.webflux.restfull.model.dao;


import com.webflux.restfull.documents.Producto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoDao extends ReactiveMongoRepository<Producto, String> {


}
