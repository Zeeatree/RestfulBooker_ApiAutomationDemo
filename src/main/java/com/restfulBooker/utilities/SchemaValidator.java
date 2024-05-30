package com.restfulBooker.utilities;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;

public class SchemaValidator {

    public static void validateBookingSchema(Response response) {
        response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/booking-schema.json"));
    }
}
