package com.restfulBooker.config;


import com.restfulBooker.utilities.AuthUtil;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class TestConfig {

    public static RequestSpecification requestSpecification;
    public static String token;

    static {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        token = AuthUtil.generateToken();
        requestSpecification = new RequestSpecBuilder()
                .setBaseUri(RestAssured.baseURI)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
    }

}
