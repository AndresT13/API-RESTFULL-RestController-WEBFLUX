package com.webflux.restfull.documents;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Document(collection="productos")
@Getter
@Setter
public class Producto {

    @Id
    private String id;
    @NotEmpty
    private String nombre;
    @NotNull
    private Double precio;

    private String foto;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createAt;

    @Valid
    @NotNull
    private Categoria categoria;

    public Producto(){}

    public Producto(String nombre, Double precio) {
        this.nombre  = nombre;
        this.precio = precio;
    }

    public Producto(String nombre, Double precio, Categoria categoria) {
      this(nombre, precio);
        this.categoria = categoria;
    }


}
