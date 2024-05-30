package com.restfulBooker.tests;

import com.restfulBooker.config.TestConfig;
import com.restfulBooker.models.Booking;
import com.restfulBooker.utilities.JsonFileUtil;
import com.restfulBooker.utilities.SchemaValidator;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;

@Tag("regression")
@TestMethodOrder(OrderAnnotation.class)
public class SchemaValidationTests {

    private static final Logger logger = LogManager.getLogger(SchemaValidationTests.class);

    @BeforeAll
    public static void setup() {
        RestAssured.requestSpecification = TestConfig.requestSpecification;
        logger.info("Test setup completed.");
    }

    static Stream<Booking> bookingProvider() throws IOException {
        return JsonFileUtil.readBookingsFromJson("src/test/resources/data/bookings.json").stream();
    }

    @ParameterizedTest(name = "Validate Booking Schema - {index}")
    @MethodSource("bookingProvider")
    public void testBookingSchemaValidation(Booking booking) {
        logger.info("Creating a new booking with firstname: {}, lastname: {}", booking.getFirstname(), booking.getLastname());

        Response response = given()
                .body(booking)
                .when()
                .post("/booking")
                .then()
                .statusCode(200)
                .extract().response();

        logger.info("Validating booking schema");
        SchemaValidator.validateBookingSchema(response);
        logger.info("Booking schema validated successfully");
    }
}