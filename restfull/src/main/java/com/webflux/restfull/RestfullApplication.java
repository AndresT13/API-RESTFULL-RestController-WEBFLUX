package com.webflux.restfull;

import com.webflux.restfull.documents.Categoria;
import com.webflux.restfull.documents.Producto;
import com.webflux.restfull.model.service.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;

import java.util.Date;

@SpringBootApplication
public class RestfullApplication implements CommandLineRunner  {

	@Autowired
	private ProductoService service;
	@Autowired
	private ReactiveMongoTemplate mongoTemplate;

	private static final Logger log = LoggerFactory.getLogger(RestfullApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(RestfullApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		mongoTemplate.dropCollection("productos").subscribe();
		mongoTemplate.dropCollection("categorias").subscribe();

		Categoria televisores = new Categoria("Televisores");
		Categoria computadoras = new Categoria("Computadoras");
		Categoria celulares = new Categoria("Celulares");
		Categoria accesorios = new Categoria("Accesorios");
		Categoria relojes = new Categoria("Relojes");
		Categoria ipad = new Categoria("Ipad");
		Categoria camaras = new Categoria("Cámaras");
		Categoria routers = new Categoria("Routers");
		Categoria videojuegos = new Categoria("VideoJuegos");
		Categoria sonido = new Categoria("Sonido");
		Categoria video = new Categoria("Video");

		Flux.just(televisores,computadoras,celulares,accesorios,relojes, ipad, camaras, routers, videojuegos, sonido, video )
				.flatMap(c -> service.saveCategoria(c))
				.doOnNext(c -> {
					log.info("Categoria creada: "+ c.getNombre() + c.getId());
				}).thenMany(
						Flux.just(
										new Producto("Televisor Samsung", 200.000,televisores),
										new Producto("Televisor LG", 1800.00,televisores),
										new Producto("Televisor Sony", 2200.0,televisores),
										new Producto("Smartphone Xiaomi", 1500.00,celulares),
										new Producto("Smartphone Samsung", 2500.00,celulares),
										new Producto("Laptop Dell", 6000.00,computadoras),
										new Producto("Laptop HP", 5500.00,computadoras),
										new Producto("Tablet Apple", 4000.00,ipad),
										new Producto("Tablet Samsung", 3500.00, ipad),
										new Producto("Auriculares Bose", 1500.00, accesorios),
										new Producto("Auriculares Sony", 1200.00, accesorios),
										new Producto("Reloj inteligente Garmin", 3000.00, relojes),
										new Producto("Reloj inteligente Apple", 5000.00, relojes),
										new Producto("Cámara Canon", 7000.00, camaras),
										new Producto("Cámara Nikon", 6500.00, camaras),
										new Producto("Proyector Epson", 3000.00, video),
										new Producto("Parlante JBL", 1200.00, sonido),
										new Producto("Router TP-Link", 800.00, routers),
										new Producto("Console PlayStation", 3000.00, videojuegos),
										new Producto("Console Xbox", 3200.00, videojuegos)
								)
								.flatMap( producto -> {
									producto.setCreateAt(new Date());
									return service.save(producto);

								})
				)
				.subscribe( producto -> log.info("Insert: " + producto.getId() + " " + producto.getNombre() ));

	}
}
