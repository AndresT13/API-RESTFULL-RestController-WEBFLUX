package com.webflux.restfull.model.service;


import com.webflux.restfull.documents.Categoria;
import com.webflux.restfull.documents.Producto;
import com.webflux.restfull.model.dao.CategoriaDao;
import com.webflux.restfull.model.dao.ProductoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoDao dao;

    @Autowired
    private CategoriaDao categoriaDAO;

    @Override
    public Flux<Producto> findAll() {
        return dao.findAll();
    }

    @Override
    public Mono<Producto> findByNombre(String nombre) {
        return dao.findByNombre(nombre);
    }

    @Override
    public Flux<Producto> findAllConNombreUpperCaseRepeat() {
        return findAllConNombreUpperCase().repeat(5000);
    }

    @Override
    public Mono<Producto> findById(String id) {
        return dao.findById(id);
    }

    @Override
    public Mono<Producto> save(Producto producto) {
        return dao.save(producto);
    }

    @Override
    public Mono<Void>  delete(Producto producto) {
        return dao.delete(producto);
    }

    @Override
    public Flux<Producto> findAllConNombreUpperCase() {
        return dao.findAll().map( producto -> {
            producto.setNombre(producto.getNombre().toUpperCase());
            return producto;
        });
    }

    @Override
    public Flux<Categoria> findAllCategoria() {
        return categoriaDAO.findAll();
    }

    @Override
    public Mono<Categoria> findByIdCategoria(String id) {
        return categoriaDAO.findById(id);
    }

    @Override
    public Mono<Categoria> saveCategoria(Categoria categoria) {
        return categoriaDAO.save(categoria);
    }

    public Mono<Void> insertarDatosDePrueba() {
        Categoria categoriaTelevisores = new Categoria("Televisores");
        List<Producto> productos = Arrays.asList(
                new Producto("Televisor LG", 1000.0, categoriaTelevisores),
                new Producto("TV Panasonic Pantalla LCD", 800.0, categoriaTelevisores)
                // Más productos si es necesario
        );

        return Flux.fromIterable(productos)
                .flatMap(producto -> dao.save(producto))
                .then();
    }

    @Override
    public Mono<Categoria> findByCategoriaByNombre(String nombre) {
        return categoriaDAO.findByNombre(nombre);
    }


    // Método para eliminar todos los productos
    public Mono<Void> eliminarTodosLosProductos() {
        return dao.deleteAll();
    }

    // Método para eliminar todas las categorías
    public Mono<Void> eliminarTodasLasCategorias() {
        return categoriaDAO.deleteAll();
    }

    // Método para limpiar datos
    public Mono<Void> limpiarDatos() {
        return eliminarTodosLosProductos()
                .then(eliminarTodasLasCategorias());
    }

}
