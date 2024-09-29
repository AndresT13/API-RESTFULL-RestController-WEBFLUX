package com.webflux.restfull.model.service;


import com.webflux.restfull.documents.Categoria;
import com.webflux.restfull.documents.Producto;
import com.webflux.restfull.model.dao.CategoriaDao;
import com.webflux.restfull.model.dao.ProductoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
}
