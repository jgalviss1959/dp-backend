package com.digitalmoneyhouse.wallet_service;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest
class WalletServiceApplicationTests {

	private static final String BASE_URL = "http://localhost:8080/api";
	private static String validToken;

	@BeforeAll
	public static void setup() {
		RestAssured.baseURI = BASE_URL;
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
	public void testGetAccountBalance() {
		given()
				.header("Authorization", "Bearer " + validToken)
				.when()
				.get("/accounts/1")
				.then()
				.statusCode(200)
				.body("balance", notNullValue());
	}

	@Test
	public void testGetLastTransactions() {
		given()
				.header("Authorization", "Bearer " + validToken)
				.when()
				.get("/transactions/account/1/last")
				.then()
				.statusCode(200);
	}

	@Test
	public void testAccessProtectedEndpointWithoutToken() {
		given()
				.when()
				.get("/accounts/1") // Intentamos acceder sin token
				.then()
				.statusCode(401); // Verificamos que la respuesta sea 403 Forbidden
	}

	@Test
	public void testGetAccountInfoById() {
		given()
				.header("Authorization", "Bearer " + validToken)
				.when()
				.get("/accounts/1")
				.then()
				.statusCode(200)
				.body("id", notNullValue())
				.body("balance", notNullValue());
	}

	@Test
	public void testGetUserCards() {
		given()
				.header("Authorization", "Bearer " + validToken)
				.when()
				.get("/accounts/1/cards")
				.then()
				.statusCode(200)
				.body("size()", greaterThanOrEqualTo(0));
	}

	@Test
	public void testAddCardToAccount() {
		String requestBody = "{" +
				"\"cardNumber\": \"2534567854624321\", " +
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
				.delete("/accounts/1/cards/10")
				.then()
				.statusCode(200)
				.contentType("text/plain")
				.body(containsString("Tarjeta eliminada correctamente"));
	}

	@Test
	public void testGetAccountIncludesAliasAndCvu() {
		given()
				.header("Authorization", "Bearer " + validToken)
				.when()
				.get("/accounts/dashboard/1")
				.then()
				.statusCode(200)
				.body("id", notNullValue())
				.body("balance", notNullValue())
				.body("alias", not(emptyOrNullString()))
				.body("cvu", allOf(not(emptyOrNullString()), hasLength(22)));
	}

}
