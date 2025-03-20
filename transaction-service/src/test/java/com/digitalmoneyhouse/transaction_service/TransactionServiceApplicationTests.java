package com.digitalmoneyhouse.transaction_service;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest
class WalletServiceApplicationTests {

	private static String validToken;

	@BeforeAll
	public static void setup() {
		RestAssured.baseURI = "http://localhost:8080/api";
		validToken = getValidToken();
	}

	private static String getValidToken() {
		return given()
				.contentType(ContentType.JSON)
				.body("{\"email\": \"juan@mail.com\", \"password\": \"password123\"}")
				.when()
				.post("/auth/login")
				.then()
				.statusCode(200)
				.extract()
				.path("token"); // Extrae el token de la respuesta
	}

	@Test
	public void testGetUserCards() {
		given()
				.header("Authorization", "Bearer " + validToken)
				.when()
				.get("/accounts/1/cards")
				.then()
				.statusCode(200)
				.body("size()", greaterThanOrEqualTo(0)); // Valida que haya una lista de tarjetas (puede estar vacía)
	}

	@Test
	public void testAddCardToAccount() {
		String requestBody = "{" +
				"\"cardNumber\": \"1234567854624321\", " +
				"\"cardHolder\": \"Juan Pérez\", " +
				"\"expirationDate\": \"12/26\", " +
				"\"cardType\": \"DEBIT\" }";

		given()
				.header("Authorization", "Bearer " + validToken)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when()
				.post("/accounts/1/cards")
				.then()
				.statusCode(201);
	}

	@Test
	public void testDeleteCardFromAccount() {
		given()
				.header("Authorization", "Bearer " + validToken)
				.when()
				.delete("/accounts/1/cards/7")
				.then()
				.statusCode(200)
				.contentType("text/plain")
				.body(containsString("Tarjeta eliminada correctamente"));
	}

}
