package com.restfulBooker.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.restfulBooker.config.TestConfig;
import com.restfulBooker.models.Booking;
import com.restfulBooker.models.BookingDates;
import com.restfulBooker.models.ErrorResponse;
import com.restfulBooker.utilities.JsonUtil;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NegativeBookingTests {

    private static final Logger logger = LogManager.getLogger(NegativeBookingTests.class);

    @BeforeAll
    public static void setup() {
        RestAssured.requestSpecification = TestConfig.requestSpecification;
        logger.info("Test setup completed.");
    }

    @Test
    @Order(1)
    public void testCreateBookingWithInvalidData() throws Exception {
        BookingDates bookingDates = new BookingDates("invalid-date", "invalid-date");

        Booking newBooking = new Booking("John", "Wick", -100, true, bookingDates, "Breakfast");

        logger.info("Creating a new booking with invalid data");

        Response response = given()
                .body(JsonUtil.toJson(newBooking))
                .when()
                .post("/booking")
                .then()
                .statusCode(400)
                .extract().response();

        ErrorResponse errorResponse = JsonUtil.fromJson(response.getBody().asString(), ErrorResponse.class);
        String errorMessage = errorResponse.getError();

        logger.info("Received error message: {}", errorMessage);

        assertEquals("Invalid input", errorMessage); // Adjust based on actual error message
    }

    @Test
    @Order(2)
    public void testCreateBookingWithMissingRequiredFields() throws Exception {
        BookingDates bookingDates = new BookingDates("2021-01-01", "2021-01-10");

        Booking newBooking = new Booking("", "", 100, true, bookingDates, "Breakfast");
        newBooking.setBookingdates(bookingDates);
        newBooking.setAdditionalneeds("Breakfast");

        logger.info("Creating a new booking with missing required fields");

        Response response = given()
                .body(JsonUtil.toJson(newBooking))
                .when()
                .post("/booking")
                .then()
                .statusCode(400)
                .extract().response();


        ErrorResponse errorResponse = JsonUtil.fromJson(response.getBody().asString(), ErrorResponse.class);
        String errorMessage = errorResponse.getError();

        logger.info("Received error message: {}", errorMessage);

        assertEquals("Missing required fields", errorMessage); // Adjust based on actual error message
    }

    @Test
    @Order(3)
    public void testUpdateBookingWithInvalidToken() throws Exception {
        BookingDates bookingDates = new BookingDates("2021-02-01", "2021-02-10");

        Booking updatedBooking = new Booking("Jane", "Doe", 150, false, bookingDates, "Lunch");

        logger.info("Updating booking with invalid token");

        Response response = given()
                .header("Cookie", "token=invalidtoken")
                .body(JsonUtil.toJson(updatedBooking))
                .when()
                .put("/booking/1") // Adjust booking ID based on your setup
                .then()
                .statusCode(403)
                .extract().response();

        ErrorResponse errorResponse = JsonUtil.fromJson(response.getBody().asString(), ErrorResponse.class);
        String errorMessage = errorResponse.getError();

        logger.info("Received error message: {}", errorMessage);

        assertEquals("Forbidden", errorMessage); // Adjust based on actual error message
    }

    @Test
    @Order(4)
    public void testDeleteBookingWithInvalidToken() throws JsonProcessingException {
        logger.info("Deleting booking with invalid token");

        Response response = given()
                .header("Cookie", "token=invalidtoken")
                .when()
                .delete("/booking/1") // Adjust booking ID based on your setup
                .then()
                .statusCode(403)
                .extract().response();

        ErrorResponse errorResponse = JsonUtil.fromJson(response.getBody().asString(), ErrorResponse.class);
        String errorMessage = errorResponse.getError();

        logger.info("Received error message: {}", errorMessage);

        assertEquals("Forbidden", errorMessage); // Adjust based on actual error message
    }

    @Test
    @Order(5)
    public void testGetNonexistentBooking() throws JsonProcessingException {
        int nonexistentBookingId = 999999; // Use a booking ID that doesn't exist

        logger.info("Retrieving nonexistent booking with bookingId: {}", nonexistentBookingId);

        Response response = given()
                .when()
                .get("/booking/" + nonexistentBookingId)
                .then()
                .statusCode(404)
                .extract().response();

        ErrorResponse errorResponse = JsonUtil.fromJson(response.getBody().asString(), ErrorResponse.class);
        String errorMessage = errorResponse.getError();

        logger.info("Received error message: {}", errorMessage);

        assertEquals("Not Found", errorMessage); // Adjust based on actual error message
    }
}
