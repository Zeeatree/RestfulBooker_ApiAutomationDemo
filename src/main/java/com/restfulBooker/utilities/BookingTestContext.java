package com.restfulBooker.utilities;

import com.restfulBooker.models.BookingDates;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BookingTestContext {

    private static final Logger logger = LogManager.getLogger(BookingTestContext.class);

    private static ThreadLocal<Integer> bookingId = new ThreadLocal<>();
    private static ThreadLocal<String> expectedFirstname = new ThreadLocal<>();
    private static ThreadLocal<String> expectedLastname = new ThreadLocal<>();
    private static ThreadLocal<Integer> expectedTotalPrice = new ThreadLocal<>();
    private static ThreadLocal<Boolean> expectedDepositPaid = new ThreadLocal<>();
    private static ThreadLocal<BookingDates> expectedBookingDates = new ThreadLocal<>();
    private static ThreadLocal<String> expectedAdditionalNeeds = new ThreadLocal<>();

    public static Integer getBookingId() {
        return bookingId.get();
    }

    public static void setBookingId(Integer id) {
        logger.info("Setting bookingId to {}", id);
        bookingId.set(id);
    }

    public static String getExpectedFirstname() {
        return expectedFirstname.get();
    }

    public static void setExpectedFirstname(String firstname) {
        logger.info("Setting expectedFirstname to {}", firstname);
        expectedFirstname.set(firstname);
    }

    public static String getExpectedLastname() {
        return expectedLastname.get();
    }

    public static void setExpectedLastname(String lastname) {
        logger.info("Setting expectedLastname to {}", lastname);
        expectedLastname.set(lastname);
    }

    public static Integer getExpectedTotalPrice() {
        return expectedTotalPrice.get();
    }

    public static void setExpectedTotalPrice(Integer totalPrice) {
        logger.info("Setting expectedTotalPrice to {}", totalPrice);
        expectedTotalPrice.set(totalPrice);
    }

    public static Boolean getExpectedDepositPaid() {
        return expectedDepositPaid.get();
    }

    public static void setExpectedDepositPaid(Boolean depositPaid) {
        logger.info("Setting expectedDepositPaid to {}", depositPaid);
        expectedDepositPaid.set(depositPaid);
    }

    public static BookingDates getExpectedBookingDates() {
        return expectedBookingDates.get();
    }

    public static void setExpectedBookingDates(BookingDates bookingDates) {
        logger.info("Setting expectedBookingDates to {}", bookingDates);
        expectedBookingDates.set(bookingDates);
    }

    public static String getExpectedAdditionalNeeds() {
        return expectedAdditionalNeeds.get();
    }

    public static void setExpectedAdditionalNeeds(String additionalNeeds) {
        logger.info("Setting expectedAdditionalNeeds to {}", additionalNeeds);
        expectedAdditionalNeeds.set(additionalNeeds);
    }

    public static void clear() {
        logger.info("Clearing BookingTestContext");
        bookingId.remove();
        expectedFirstname.remove();
        expectedLastname.remove();
        expectedTotalPrice.remove();
        expectedDepositPaid.remove();
        expectedBookingDates.remove();
        expectedAdditionalNeeds.remove();
    }
}
