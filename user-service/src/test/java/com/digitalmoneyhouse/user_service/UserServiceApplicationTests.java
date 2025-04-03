package com.digitalmoneyhouse.user_service;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
class UserServiceApplicationTests {

    private static String token;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:8080/api";
    }

    @Test
    public void testRegisterUser() {
        String requestBody = "{" +
                "\"firstName\": \"Test\", " +
                "\"lastName\": \"Perez\", " +
                "\"dni\": \"1"+ System.currentTimeMillis() +"\", " +
                "\"email\": \"test"+ System.currentTimeMillis() +"@mail.com\", " +
                "\"phone\": \"099123456\", " +
                "\"password\": \"password123\"}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/users/register")
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    public void testLoginValidUser() {
        String requestBody = "{" +
                "\"email\": \"juan@mail.com\", " +
                "\"password\": \"password123\"}";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/auth/login");

        // Guarda el token obtenido en la variable estática
        token = response.jsonPath().getString("token");

        response.then()
                .statusCode(200)
                .body("token", notNullValue());
    }

    @Test
    void logoutUserWithValidToken_shouldInvalidateToken() {
        testLoginValidUser(); // Asegura que el usuario está autenticado antes del logout

        given()
                .header("Authorization", "Bearer " + token) // Enviar token válido en el header
                .when()
                .post("/auth/logout") // Llamar al endpoint de logout
                .then()
                .statusCode(200) // Esperamos 200 OK
                .contentType(ContentType.TEXT) // Aseguramos que la respuesta es texto plano
                .body(equalTo("Logout exitoso. Token invalidado.")); // Verificamos el mensaje exacto
    }

    @Test
    public void testGetUserInfoById() {
        testLoginValidUser();
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/users/id/1")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("firstName", notNullValue())
                .body("lastName", notNullValue())
                .body("email", notNullValue());
    }

    @Test
    public void testUpdateUserProfile() {

        testLoginValidUser();

        String requestBody = "{\"email\": \"fio@mail.com\", \"phone\": \"099123456\"}";

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .patch("/users/1")
                .then()
                .statusCode(200)
                .body(equalTo("Perfil actualizado con éxito"));
    }

}
