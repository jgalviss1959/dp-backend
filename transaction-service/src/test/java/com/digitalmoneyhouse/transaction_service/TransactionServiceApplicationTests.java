package com.digitalmoneyhouse.transaction_service;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest
class TransactionServiceApplicationTests {

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
	public void testGetActivityWithValidId() {
		given()
				.header("Authorization", "Bearer " + validToken)
				.when()
				.get("/transactions/accounts/1/activity")
				.then()
				.statusCode(200)
				.body("size()", greaterThanOrEqualTo(0));
	}

	@Test
	public void testGetActivityWithoutToken() {
		given()
				.when()
				.get("/transactions/accounts/1/activity")
				.then()
				.statusCode(401);
	}

	@Test
	public void testGetActivityWithInvalidId() {
		given()
				.header("Authorization", "Bearer " + validToken)
				.when()
				.get("/transactions/accounts/9999/activity/1")
				.then()
				.statusCode(404);
	}

	@Test
	public void testGetActivity() {
		given()
				.header("Authorization", "Bearer " + validToken)
				.when()
				.get("/transactions/accounts/1/activity/1")
				.then()
				.statusCode(200)
				.body("amount", equalTo(100.50f));
	}

	@Test
	public void testGetActivityBadReq() {
		given()
				.header("Authorization", "Bearer " + validToken)
				.when()
				.get("/transactions/accounts/0/activity/1")
				.then()
				.statusCode(400)
				.body(equalTo("ID inválido"));
	}

	@Test
	public void testGetActivityNoToken() {
		given()
				.when()
				.get("/transactions/accounts/1/activity/1")
				.then()
				.statusCode(401);
	}

	@Test
	public void testGetActivityWithInvalidActivityId() {
		given()
				.header("Authorization", "Bearer " + validToken)
				.when()
				.get("/transactions/accounts/1/activity/9999")
				.then()
				.statusCode(404)
				.body(equalTo("ActivityID inexistente"));
	}

	@Test
	public void testDepositToWallet() {
		String requestBody = "{" +
				"\"accountId\": 1, " +
				"\"cardNumber\": \"1234567812345678\", " +
				"\"amount\": 50.00}";

		given()
				.header("Authorization", "Bearer " + validToken)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when()
				.post("/transactions/accounts/1/transferences")
				.then()
				.statusCode(201)
				.body("amount", equalTo(50.00f));
	}

	@Test
	public void testTransferBetweenAccounts() {
		String requestBody = "{" +
				"\"origin\": \"0d188f5116e44680a4d32d\", " +
				"\"destination\": \"5c24bd6d418348f9ad722f\", " +
				"\"amount\": 1}";

		given()
				.header("Authorization", "Bearer " + validToken)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when()
				.post("/transactions/accounts/transfer")
				.then()
				.statusCode(200)
				.body("message", containsString("Transferencia realizada")); // adaptá según tu respuesta real
	}

}
