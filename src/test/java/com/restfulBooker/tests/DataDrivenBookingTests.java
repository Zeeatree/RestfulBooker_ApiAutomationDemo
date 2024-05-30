package com.restfulBooker.tests;

import com.restfulBooker.config.TestConfig;
import com.restfulBooker.models.Booking;
import com.restfulBooker.models.BookingDates;
import com.restfulBooker.utilities.BookingTestContext;
import com.restfulBooker.utilities.CsvUtil;
import com.restfulBooker.utilities.JsonUtil;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@Tag("regression")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DataDrivenBookingTests {

    private static final Logger logger = LogManager.getLogger(DataDrivenBookingTests.class);

    @BeforeAll
    public static void setup() {
        RestAssured.requestSpecification = TestConfig.requestSpecification;
        logger.info("Test setup completed.");
    }

    static Stream<Booking> bookingProvider() throws IOException {
        return CsvUtil.readBookingsFromCsv("src/test/resources/data/bookings.csv").stream();
    }

    @DisplayName("Create Booking Data Driven with CSV")
    @ParameterizedTest
    @MethodSource("bookingProvider")
    @Order(1)
    @Tag("smoke")
    public void testCreateBookingDataDriven(Booking booking) {
        logger.info("Creating a new booking with firstname: {}, lastname: {}", booking.getFirstname(), booking.getLastname());

        BookingTestContext.setExpectedFirstname(booking.getFirstname());
        BookingTestContext.setExpectedLastname(booking.getLastname());
        BookingTestContext.setExpectedTotalPrice(booking.getTotalprice());
        BookingTestContext.setExpectedDepositPaid(booking.isDepositpaid());
        BookingTestContext.setExpectedBookingDates(booking.getBookingdates());
        BookingTestContext.setExpectedAdditionalNeeds(booking.getAdditionalneeds());

        Response response = null;
        try {
            response = given()
                    .body(JsonUtil.toJson(booking))
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
        Integer bookingId = jsonPath.getInt("bookingid");
        BookingTestContext.setBookingId(bookingId);

        logger.info("Booking created successfully with bookingId: {}", bookingId);

        assertAll("Verify booking details",
                () -> assertEquals(booking.getFirstname(), jsonPath.getString("booking.firstname")),
                () -> assertEquals(booking.getLastname(), jsonPath.getString("booking.lastname")),
                () -> assertEquals(booking.getTotalprice(), jsonPath.getInt("booking.totalprice")),
                () -> assertEquals(booking.isDepositpaid(), jsonPath.getBoolean("booking.depositpaid")),
                () -> assertEquals(booking.getBookingdates().getCheckin(), jsonPath.getString("booking.bookingdates.checkin")),
                () -> assertEquals(booking.getBookingdates().getCheckout(), jsonPath.getString("booking.bookingdates.checkout")),
                () -> assertEquals(booking.getAdditionalneeds(), jsonPath.getString("booking.additionalneeds"))
        );
    }

    @DisplayName("Get Booking Data Driven with CSV")
    @ParameterizedTest
    @MethodSource("bookingProvider")
    @Order(2)
    @Tag("Smoke")
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

    @DisplayName("Update Booking Data Driven with CSV")
    @ParameterizedTest
    @MethodSource("bookingProvider")
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

    @DisplayName("Delete Booking Data Driven with CSV")
    @ParameterizedTest
    @MethodSource("bookingProvider")
    @Order(4)
    public void testDeleteBooking() {
        Integer bookingId = BookingTestContext.getBookingId();
        logger.info("Deleting booking with bookingId: {}", bookingId);

        if (bookingId == null) {
            fail("Booking ID is null. Test cannot proceed.");
        }

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
}
