package com.webflux.restfull;


import com.webflux.restfull.handler.ProductoHandler;
import com.webflux.restfull.model.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.reactive.function.server.*;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class RouterFunctionConfig {

    @Autowired
    private ProductoService service;

    @Bean
    public RouterFunction<ServerResponse> routes(ProductoHandler handler){
        return RouterFunctions.route(GET("/api/v2/productos").or(GET("/api/v3/productos")), request -> handler.listar(request))
                .andRoute(GET("/api/v2/productos/{id}"), request -> handler.ver(request)) // .and(contentType(MediaType.APPLICATION_JSON))
                .andRoute(POST("/api/v2/productos"), request ->  handler.crear(request))
                .andRoute(PUT("/api/v2/productos/{id}"), request ->  handler.editar(request))
                .andRoute(DELETE("/api/v2/productos/{id}"), request ->  handler.eliminar(request))
                .andRoute(POST("/api/v2/productos/upload/{id}"), request ->  handler.upload(request))
           .andRoute(POST("/api/v2/productos/crear"), request ->  handler.crearConFoto(request));

    }
}
