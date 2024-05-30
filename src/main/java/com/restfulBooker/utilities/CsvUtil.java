package com.restfulBooker.utilities;

import com.restfulBooker.models.Booking;
import com.restfulBooker.models.BookingDates;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CsvUtil {

    public static List<Booking> readBookingsFromCsv(String filePath) throws IOException {
        List<Booking> bookings = new ArrayList<>();
        try (Reader reader = new FileReader(filePath);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader())) {

            for (CSVRecord csvRecord : csvParser) {
                String firstname = csvRecord.get("firstname");
                String lastname = csvRecord.get("lastname");
                int totalprice = Integer.parseInt(csvRecord.get("totalprice"));
                boolean depositpaid = Boolean.parseBoolean(csvRecord.get("depositpaid"));
                String checkin = csvRecord.get("checkin");
                String checkout = csvRecord.get("checkout");
                String additionalneeds = csvRecord.get("additionalneeds");

                BookingDates bookingDates = new BookingDates(checkin, checkout);
                Booking booking = new Booking(firstname, lastname, totalprice, depositpaid, bookingDates, additionalneeds);
                bookings.add(booking);
            }
        }
        return bookings;
    }

}
