package com.webflux.restfull.documents;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@Setter
@NoArgsConstructor
@Document(collection = "categorias")
public class Categoria {

    @Id
    private String id;
    @NotEmpty
    private String nombre;

    public Categoria(String nombre){
        this.nombre = nombre;
    }
}
