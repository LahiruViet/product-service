package com.example.product;

import com.example.product.dto.ProductRequest;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductApplicationTests {

	@ServiceConnection
	private final static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.7");

	@LocalServerPort
	private Integer port;

	static {
		mongoDBContainer.start();
	}

	@BeforeEach
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	@Test
	public void shouldCreateProduct() throws Exception {

		ProductRequest productRequest = getProductRequest();

		RestAssured.given()
				.contentType("application/json")
				.body(productRequest)
				.when()
				.post("/api/v1/product")
				.then()
				.log().all()
				.statusCode(201)
				.body("id", Matchers.notNullValue())
				.body("name", Matchers.equalTo(productRequest.name()))
				.body("description", Matchers.equalTo(productRequest.description()))
				.body("price", Matchers.is(productRequest.price().intValueExact()));
	}

	private ProductRequest getProductRequest() {
		return new ProductRequest("iPhone 13", "iPhone 13", BigDecimal.valueOf(1200));
	}
}
