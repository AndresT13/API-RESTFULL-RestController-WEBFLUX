package com.webflux.restfull;

import com.webflux.restfull.documents.Categoria;
import com.webflux.restfull.documents.Producto;
import com.webflux.restfull.model.service.ProductoService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;


import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureWebTestClient
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class RestfullApplicationTests {




	@Autowired
	private WebTestClient client;

	@Autowired
	private ProductoService service;

	@Value("${config.base.endpoint}")
	private String url;

	@BeforeEach
	public void setUp() {
		// Limpiar la base de datos o reiniciar el estado
		service.limpiarDatos(); // Implementa este método según sea necesario
		service.insertarDatosDePrueba().block(); // Inserta los datos de prueba
	}


	@Test
	public void listarTest() {

		client.get()
				.uri(url)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBodyList(Producto.class)
				.consumeWith( response -> {
					List<Producto> productos = response.getResponseBody();

					// Agrega registros para depurar
					System.out.println("Cantidad de productos devueltos: " + productos.size());
					productos.forEach(p -> System.out.println("Producto: " + p.getNombre()));

					/*
					productos.forEach( p -> {
						System.out.println(p.getNombre());
					});

					 */
					assertEquals(31, productos.size());
				});
	}

	@Test
	public void verTest() {
		// Asegúrate de insertar datos de prueba antes de ejecutar la prueba
		service.insertarDatosDePrueba().block(); // Inserta los datos de prueba

		// Busca la categoría
		Categoria categoria = service.findByCategoriaByNombre("Celulares").block();
		Assertions.assertNotNull(categoria, "La categoría no debe ser nula"); // Asegúrate de que la categoría exista

		// Ahora busca el producto
		Producto producto = service.findByNombre("Smartphone Xiaomi").block();
		Assertions.assertNotNull(producto, "El producto no debe ser nulo"); // Verifica que el producto no sea nulo

		// Realiza la petición para obtener el producto por ID
		client.get()
				.uri(url +"/{id}", Collections.singletonMap("id", producto.getId()))
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody(Producto.class)
				.consumeWith(response -> {
					Producto p = response.getResponseBody();
					Assertions.assertNotNull(p, "El producto obtenido no debe ser nulo"); // Verifica que la respuesta no sea nula
					Assertions.assertNotNull(p.getId(), "El ID del producto no debe ser nulo");
					Assertions.assertFalse(p.getId().isEmpty(), "El ID del producto no debe estar vacío");
					Assertions.assertNotNull(p.getNombre(), "El nombre del producto no debe ser nulo");
					Assertions.assertEquals("Smartphone Xiaomi", p.getNombre(), "El nombre del producto debe ser 'Smartphone Xiaomi'");
				});

				/*.expectBody()
				.jsonPath("$.id").isNotEmpty()
				.jsonPath("$.nombre").isEqualTo("Televisor LG");
				 */

	}

	@Test
	public void crearTest(){

		Categoria categoria = service.findByCategoriaByNombre("Celulares").block();

		Producto producto = new Producto("Smartphone Samsung", 400000.00, categoria);

		client.post().uri(url)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(producto), Producto.class)
				.exchange()
				.expectStatus().isCreated()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.id").isNotEmpty()
				.jsonPath("$.nombre").isEqualTo("Smartphone Samsung")
				.jsonPath("$.categoria.nombre").isEqualTo("Celulares");


	}

	@Test
	public void crear2Test(){

		Categoria categoria = service.findByCategoriaByNombre("Celulares").block();

		Producto producto = new Producto("Smartphone Samsung", 400000.00, categoria);

		client.post().uri(url)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(producto), Producto.class)
				.exchange()
				.expectStatus().isCreated()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody(Producto.class)
				.consumeWith( response -> {
					Producto p = response.getResponseBody();
					Assertions.assertNotNull(p, "El producto no debe ser nulo");
					Assertions.assertNotNull(p.getId(), "El ID del producto no debe ser nulo");
					Assertions.assertFalse(p.getId().isEmpty(), "El ID del producto no debe estar vacío");
					Assertions.assertEquals("Smartphone Samsung", p.getNombre(), "El nombre del producto debe ser 'Smartphone Samsung'");
					Assertions.assertNotNull(p.getCategoria(), "La categoría del producto no debe ser nula");
					Assertions.assertEquals("Celulares", p.getCategoria().getNombre(), "La categoría del producto debe ser 'Celulares'");
				});

			}



	@Test
	public void editarTest(){

		Producto producto = service.findByNombre("Proyector Epson").block();
		Categoria categoria = service.findByCategoriaByNombre("Video").block();

		Producto productoEditado = new Producto("Home Theater Sony", 400.00, categoria);

		client.put().uri(url + "/{id}", Collections.singletonMap("id", producto.getId()))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(productoEditado), Producto.class)
				.exchange()
				.expectStatus().isCreated()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.id").isNotEmpty()
				.jsonPath("$.nombre").isEqualTo("Home Theater Sony")
				.jsonPath("$.categoria.nombre").isEqualTo("Video");
			}


			@Test
			public void eliminarTest(){
				Producto producto = service.findByNombre("Cámara Canon").block();
				client.delete()
						.uri(url +"/{id}", Collections.singletonMap("id", producto.getId()))
						.exchange()
						.expectStatus().isNoContent()
						.expectBody()
						.isEmpty();

						client.get()
								.uri(url +"/{id}", Collections.singletonMap("id", producto.getId()))
								.exchange()
								.expectStatus().isOk()  // .isNotFound()
								.expectBody()
								.isEmpty();
			}
	}







