package com.webflux.restfull.controller;

import com.webflux.restfull.documents.Producto;
import com.webflux.restfull.model.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/productos")
public class ProductosController {

    @Autowired
    private ProductoService service;

    @Value("${config.uploads.path}")
    private String path;


    /*
    @GetMapping
    public Flux<Producto> listar(){
        return service.findAll();
    }

     */

    @PostMapping("/v2")
    public Mono<ResponseEntity<Producto>> crearconFoto( Producto producto,  @RequestPart FilePart file){

        if (producto.getCreateAt() == null){
            producto.setCreateAt(new Date());
        }

        producto.setFoto(UUID.randomUUID().toString()+ "-" + file.filename()
                .replace(" ", "")
                .replace(":", "")
                .replace("\\", ""));

        return file.transferTo(new File(path + producto.getFoto()))
                .then(service.save(producto))
                .map(p -> ResponseEntity.created(URI.create("/api/productos/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p)
                );

    }


    @PostMapping("/upload/{id}")
    public Mono<ResponseEntity<Producto>> upload(@PathVariable String id, @RequestPart FilePart file){
        return service.findById(id).flatMap( p -> {
            p.setFoto(UUID.randomUUID().toString()+ "-" + file.filename()
                    .replace(" ", "")
                    .replace(":", "")
                    .replace("\\", ""));

            return file.transferTo(new File(path + p.getFoto()))
                    .then(service.save(p));
        }).map(p -> ResponseEntity.ok(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<Producto>>> lista(){
        return Mono.just(
                ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll())
                );
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Producto>> ver(@PathVariable String id){

        return service.findById(id).map(p -> ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<  Map<String, Object>>> crear(@Valid @RequestBody Mono<Producto> monoProducto){
        Map<String, Object> respuesta = new HashMap<>();
        return monoProducto.flatMap(producto -> {
            if (producto.getCreateAt() == null){
                producto.setCreateAt(new Date());
            }

            return service.save(producto).map(p -> {
                respuesta.put("producto", p);
                respuesta.put("mensaje", "Producto creado con éxito");
                respuesta.put("timestamp",new Date());
                     return  ResponseEntity
                             .created(URI.create("/api/productos/".concat(p.getId())))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(respuesta);
                            });

        }).onErrorResume( t ->{
            return  Mono.just(t).cast(WebExchangeBindException.class)
                    .flatMap(e ->  Mono.just(e.getFieldErrors()))
                    .flatMapMany(errors -> Flux.fromIterable(errors))
                    .map(fieldError -> "El campo"+ fieldError.getField() + " " + fieldError.getDefaultMessage()) // convertir cada fieldErrors en tipo String
                    .collectList()
                    .flatMap(list -> {
                        respuesta.put("errors", list);
                        respuesta.put("timestamp",new Date());
                        respuesta.put("status",HttpStatus.BAD_REQUEST.value());
                        return Mono.just(ResponseEntity.badRequest().body(respuesta));

                    });
        });

    }

@PutMapping("/{id}")
    public Mono<ResponseEntity<Producto>> editar(@RequestBody Producto producto, @PathVariable String id){
        return service.findById(id).flatMap(
                p -> {
                    p.setNombre(producto.getNombre());
                    p.setPrecio(producto.getPrecio());
                    p.setCategoria(producto.getCategoria());
                    return service.save(p);

                }).map( p -> ResponseEntity.created(URI.create("/api/productos/".concat(p.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> eliminar(@PathVariable String id){
        return service.findById(id).flatMap( p-> {
            return service.delete(p).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
        })  .defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
    }
}
