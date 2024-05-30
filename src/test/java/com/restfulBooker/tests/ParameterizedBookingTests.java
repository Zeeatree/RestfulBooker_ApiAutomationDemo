package com.restfulBooker.tests;

import com.restfulBooker.config.TestConfig;
import com.restfulBooker.models.Booking;
import com.restfulBooker.models.BookingDates;
import com.restfulBooker.utilities.BookingTestContext;
import com.restfulBooker.utilities.JsonUtil;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ParameterizedBookingTests {

    private static final Logger logger = LogManager.getLogger(ParameterizedBookingTests.class);

    @BeforeAll
    public static void setup() {
        RestAssured.requestSpecification = TestConfig.requestSpecification;
        logger.info("Test setup completed.");
    }

    @ParameterizedTest
    @CsvSource({
            "John, Doe, 123, true, 2021-01-01, 2021-01-10, Breakfast",
            "Jane, Smith, 456, false, 2021-02-01, 2021-02-15, Dinner",
            "Alice, Johnson, 789, true, 2021-03-01, 2021-03-20, Lunch",
            "Bob, Brown, 100, false, 2021-04-01, 2021-04-30, Breakfast",
            "Charlie, White, 200, true, 2021-05-01, 2021-05-31, Dinner",
            "David, Black, 300, false, 2021-06-01, 2021-06-30, Lunch",
            "Eve, Green, 400, true, 2021-07-01, 2021-07-31, Breakfast",
            "Frank, Blue, 500, false, 2021-08-01, 2021-08-31, Dinner",
            "Grace, Red, 600, true, 2021-09-01, 2021-09-30, Lunch"
    })
    @Order(1)
    public void testCreateBookingParameterized(String firstname, String lastname, int totalprice, boolean depositpaid, String checkin, String checkout, String additionalneeds) {
        BookingDates bookingDates = new BookingDates(checkin, checkout);
        Booking newBooking = new Booking(firstname, lastname, totalprice, depositpaid, bookingDates, additionalneeds);

        BookingTestContext.setExpectedFirstname(firstname);
        BookingTestContext.setExpectedLastname(lastname);
        BookingTestContext.setExpectedTotalPrice(totalprice);
        BookingTestContext.setExpectedDepositPaid(depositpaid);
        BookingTestContext.setExpectedBookingDates(bookingDates);
        BookingTestContext.setExpectedAdditionalNeeds(additionalneeds);

        logger.info("Creating a new booking with firstname: {}, lastname: {}", firstname, lastname);

        Response response = null;
        try {
            response = given()
                    .body(JsonUtil.toJson(newBooking))
                    .when()
                    .post("/booking")
                    .then()
                    .statusCode(200)
                    .extract().response();
        } catch (Exception e) {
            logger.error("Error creating booking: {}", e.getMessage(), e);
            fail("Booking creation failed due to an exception");
        }

        JsonPath jsonPath = response.jsonPath();
        BookingTestContext.setBookingId(jsonPath.getInt("bookingid"));

        logger.info("Booking created successfully with bookingId: {}", BookingTestContext.getBookingId());

        assertAll("Verify booking details",
                () -> assertEquals(firstname, jsonPath.getString("booking.firstname")),
                () -> assertEquals(lastname, jsonPath.getString("booking.lastname")),
                () -> assertEquals(totalprice, jsonPath.getInt("booking.totalprice")),
                () -> assertEquals(depositpaid, jsonPath.getBoolean("booking.depositpaid")),
                () -> assertEquals(checkin, jsonPath.getString("booking.bookingdates.checkin")),
                () -> assertEquals(checkout, jsonPath.getString("booking.bookingdates.checkout")),
                () -> assertEquals(additionalneeds, jsonPath.getString("booking.additionalneeds"))
        );
    }

    @Test
    @Order(2)
    public void testGetBooking() {
        Integer bookingId = BookingTestContext.getBookingId();
        logger.info("Retrieving booking with bookingId: {}", bookingId);

        Response response = null;
        try {
            response = given()
                    .when()
                    .get("/booking/" + bookingId)
                    .then()
                    .statusCode(200)
                    .extract().response();
        } catch (Exception e) {
            logger.error("Error retrieving booking: {}", e.getMessage(), e);
            fail("Booking retrieval failed due to an exception");
        }

        JsonPath jsonPath = response.jsonPath();

        assertAll("Verify retrieved booking details",
                () -> assertEquals(BookingTestContext.getExpectedFirstname(), jsonPath.getString("firstname")),
                () -> assertEquals(BookingTestContext.getExpectedLastname(), jsonPath.getString("lastname")),
                () -> assertEquals(BookingTestContext.getExpectedTotalPrice(), jsonPath.getInt("totalprice")),
                () -> assertEquals(BookingTestContext.getExpectedDepositPaid(), jsonPath.getBoolean("depositpaid")),
                () -> assertEquals(BookingTestContext.getExpectedBookingDates().getCheckin(), jsonPath.getString("bookingdates.checkin")),
                () -> assertEquals(BookingTestContext.getExpectedBookingDates().getCheckout(), jsonPath.getString("bookingdates.checkout")),
                () -> assertEquals(BookingTestContext.getExpectedAdditionalNeeds(), jsonPath.getString("additionalneeds"))
        );

        logger.info("Booking retrieved successfully with firstname: {}, lastname: {}", jsonPath.getString("firstname"), jsonPath.getString("lastname"));
    }

    @Test
    @Order(3)
    public void testUpdateBooking() {
        Integer bookingId = BookingTestContext.getBookingId();
        logger.info("Updating booking with bookingId: {}", bookingId);

        BookingDates bookingDates = new BookingDates("2021-02-01", "2021-02-10");
        Booking updatedBooking = new Booking("Jane", "Doe", 150, false, bookingDates, "Lunch");

        Response response = null;
        try {
            response = given()
                    .header("Cookie", "token=" + TestConfig.token)
                    .body(JsonUtil.toJson(updatedBooking))
                    .when()
                    .put("/booking/" + bookingId)
                    .then()
                    .statusCode(200)
                    .extract().response();
        } catch (Exception e) {
            logger.error("Error updating booking: {}", e.getMessage(), e);
            fail("Booking update failed due to an exception");
        }

        JsonPath jsonPath = response.jsonPath();

        assertAll("Verify updated booking details",
                () -> assertEquals("Jane", jsonPath.getString("firstname")),
                () -> assertEquals("Doe", jsonPath.getString("lastname")),
                () -> assertEquals(150, jsonPath.getInt("totalprice")),
                () -> assertFalse(jsonPath.getBoolean("depositpaid")),
                () -> assertEquals("2021-02-01", jsonPath.getString("bookingdates.checkin")),
                () -> assertEquals("2021-02-10", jsonPath.getString("bookingdates.checkout")),
                () -> assertEquals("Lunch", jsonPath.getString("additionalneeds"))
        );

        logger.info("Booking updated successfully with new dates: checkin={}, checkout={}", jsonPath.getString("bookingdates.checkin"), jsonPath.getString("bookingdates.checkout"));
    }

    @Test
    @Order(4)
    public void testDeleteBooking() {
        Integer bookingId = BookingTestContext.getBookingId();
        logger.info("Deleting booking with bookingId: {}", bookingId);

        try {
            given()
                    .header("Cookie", "token=" + TestConfig.token)
                    .when()
                    .delete("/booking/" + bookingId)
                    .then()
                    .statusCode(201);
        } catch (Exception e) {
            logger.error("Error deleting booking: {}", e.getMessage(), e);
            fail("Booking deletion failed due to an exception");
        }

        logger.info("Booking deleted successfully with bookingId: {}", bookingId);

        // Verify Deletion
        try {
            given()
                    .when()
                    .get("/booking/" + bookingId)
                    .then()
                    .statusCode(404);
        } catch (Exception e) {
            logger.error("Error verifying booking deletion: {}", e.getMessage(), e);
            fail("Booking deletion verification failed due to an exception");
        }

        // Clear context after deletion
        BookingTestContext.clear();
    }


    @Test
    @Order(5)
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
    @Order(6)
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