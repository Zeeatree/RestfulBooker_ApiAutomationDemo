package com.restfulBooker.tests;

import com.restfulBooker.config.TestConfig;
import com.restfulBooker.models.Booking;
import com.restfulBooker.models.BookingDates;
import com.restfulBooker.utilities.JsonUtil;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.Assert;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookingTests {

    private static final Logger logger = LogManager.getLogger(BookingTests.class);
    private static int bookingId;

    @BeforeAll
    public static void setup() {
        RestAssured.requestSpecification = TestConfig.requestSpecification;
        logger.info("Test setup completed.");
    }

    @Test
    @Order(1)
    public void testCreateBooking() throws Exception {
        BookingDates bookingDates = new BookingDates("2021-01-01", "2021-01-10");

        Booking newBooking = new Booking("John", "Doe", 123, true, bookingDates, "Breakfast");

        logger.info("Creating a new booking with firstname: {}, lastname: {}", newBooking.getFirstname(), newBooking.getLastname());

        Response response = given()
                .body(JsonUtil.toJson(newBooking))
                .when()
                .post("/booking")
                .then()
                .statusCode(200)
                .extract().response();

        JsonPath jsonPath = response.jsonPath();
        bookingId = jsonPath.getInt("bookingid");

        logger.info("Booking created successfully with bookingId: {}", bookingId);

        assertEquals("John", jsonPath.getString("booking.firstname"));
        assertEquals("Doe", jsonPath.getString("booking.lastname"));
    }

    @Test
    @Order(2)
    public void testGetBooking() {
        logger.info("Retrieving booking with bookingId: {}", bookingId);

        Response response = given()
                .when()
                .get("/booking/" + bookingId)
                .then()
                .statusCode(200)
                .extract().response();

        JsonPath jsonPath = response.jsonPath();
        assertEquals("John", jsonPath.getString("firstname"));
        assertEquals("Doe", jsonPath.getString("lastname"));

        logger.info("Booking retrieved successfully with firstname: {}, lastname: {}", jsonPath.getString("firstname"), jsonPath.getString("lastname"));
    }

    @Test
    @Order(3)
    public void testUpdateBooking() throws Exception {
        BookingDates bookingDates = new BookingDates("2021-02-01", "2021-02-10");

        Booking updatedBooking = new Booking("Jane", "Doe", 150, false, bookingDates, "Lunch");

        logger.info("Updating booking with bookingId: {}", bookingId);

        Response response = given()
                .header("Cookie", "token=" + TestConfig.token)
                .body(JsonUtil.toJson(updatedBooking))
                .when()
                .put("/booking/" + bookingId)
                .then()
                .statusCode(200)
                .extract().response();

        JsonPath jsonPath = response.jsonPath();
        assertEquals("Jane", jsonPath.getString("firstname"));
        assertEquals("Doe", jsonPath.getString("lastname"));
        assertEquals("2021-02-01", jsonPath.getString("bookingdates.checkin"));
        assertEquals("2021-02-10", jsonPath.getString("bookingdates.checkout"));

        logger.info("Booking updated successfully with new dates: checkin={}, checkout={}", jsonPath.getString("bookingdates.checkin"), jsonPath.getString("bookingdates.checkout"));
    }

    @Test
    @Order(4)
    public void testPartialUpdateBooking() throws Exception {
        Booking partialUpdate = new Booking("Jim", "Beam", 200, true, null, "Dinner");

        logger.info("Partially updating booking with bookingId: {}", bookingId);

        Response response = given()
                .header("Cookie", "token=" + TestConfig.token)
                .body(JsonUtil.toJson(partialUpdate))
                .when()
                .patch("/booking/" + bookingId)
                .then()
                .statusCode(200)
                .extract().response();

        JsonPath jsonPath = response.jsonPath();
        assertEquals("Jim", jsonPath.getString("firstname"));
        assertEquals("Beam", jsonPath.getString("lastname"));

        logger.info("Booking partially updated successfully with new firstname: {}, lastname: {}", jsonPath.getString("firstname"), jsonPath.getString("lastname"));
    }

    @Test
    @Order(5)
    public void testDeleteBooking() {
        logger.info("Deleting booking with bookingId: {}", bookingId);

        given()
                .header("Cookie", "token=" + TestConfig.token)
                .when()
                .delete("/booking/" + bookingId)
                .then()
                .statusCode(201);

        logger.info("Booking deleted successfully with bookingId: {}", bookingId);

        // Reset bookingId after deletion
        bookingId = 0;
    }

    @Test
    @Order(6)
    public void testGetBookingIds() {
        logger.info("Retrieving all booking IDs");

        given()
                .when()
                .get("/booking")
                .then()
                .statusCode(200);

        logger.info("Booking IDs retrieved successfully");
    }

    @Test
    @Order(7)
    public void testHealthCheck() {
        logger.info("Performing health check");

        given()
                .when()
                .get("/ping")
                .then()
                .statusCode(201);

        logger.info("Health check passed");
    }
}
