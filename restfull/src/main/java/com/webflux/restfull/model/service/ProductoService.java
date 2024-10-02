package com.webflux.restfull.model.service;


import com.webflux.restfull.documents.Categoria;
import com.webflux.restfull.documents.Producto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductoService {

    public Flux<Producto> findAll();

    public Mono<Producto> findByNombre(String nombre);

    public Flux<Producto> findAllConNombreUpperCaseRepeat();

    public Mono<Producto> findById(String id);

    public Mono<Producto> save(Producto producto);

   public Mono<Void> delete(Producto producto);

   public Flux<Producto> findAllConNombreUpperCase();

    public Flux<Categoria> findAllCategoria();

    public Mono<Categoria> findByIdCategoria(String id);

    public Mono<Categoria> saveCategoria(Categoria categoria);

    Mono<Void> insertarDatosDePrueba();

    public Mono<Categoria> findByCategoriaByNombre(String nombre);

    public Mono<Void> limpiarDatos();
}
