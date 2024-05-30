package com.restfulBooker.utilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class AuthUtil {

    public static String generateToken() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "admin");
        credentials.put("password", "password123");

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(credentials)
                .post("/auth")
                .then()
                .statusCode(200)
                .extract().response();

        return response.path("token");
    }
}
