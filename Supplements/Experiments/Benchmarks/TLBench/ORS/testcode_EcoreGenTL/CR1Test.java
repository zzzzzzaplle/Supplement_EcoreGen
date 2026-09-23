package edu.rideshare.test;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import edu.rideshare.RideshareFactory;
import edu.rideshare.Customer;
import edu.rideshare.Driver;
import edu.rideshare.Trip;
import edu.rideshare.Booking;
import edu.rideshare.Stop;
import edu.rideshare.MembershipPackage;
import edu.rideshare.Award;

import java.util.Date;
import java.util.Calendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * CR1: Validate Booking Eligibility
 * Uses Booking.isBookingEligible() from deepseek-v4-flash/rideshare1.
 */
public class CR1Test {

    private RideshareFactory factory;
    private SimpleDateFormat dtFormat;

    @Before
    public void setUp() {
        factory = RideshareFactory.eINSTANCE;
        dtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }

    // ---- Helpers ----

    private Customer createCustomer(String id) {
        Customer c = factory.createCustomer();
        c.setId(id);
        return c;
    }

    private Trip createTrip(String depTime, String arrTime, int seats, double price) {
        Trip t = factory.createTrip();
        t.setDepartureStation("A");
        t.setArrivalStation("B");
        t.setDepartureTime(depTime);
        t.setArrivalTime(arrTime);
        t.setNumberOfSeats(seats);
        t.setPrice(price);
        return t;
    }

    private Booking createBooking(Customer customer, Trip trip, int seats, Date bookingDate) {
        Booking b = factory.createBooking();
        b.setNumberOfSeats(seats);
        b.setBookingDate(bookingDate);
        b.setCustomer(customer);
        b.setTrip(trip);
        return b;
    }

    private Date parseDate(String str) {
        try { return dtFormat.parse(str); }
        catch (ParseException e) { throw new RuntimeException(e); }
    }

    // ---- CR1 Test Cases ----

    /**
     * TC1: Booking succeeds when seats are available.
     * 3 seats requested, 5 available → accepted, remaining = 2.
     */
    @Test
    public void testCase1_SeatsAvailable() {
        Customer customer = createCustomer("C001");
        Trip trip = createTrip("2025-06-15 12:00", "2025-06-15 14:00", 5, 100.0);
        // Booking 3h before departure (09:00)
        Booking booking = createBooking(customer, trip, 3, parseDate("2025-06-15 09:00"));

        boolean result = booking.isBookingEligible();

        assertTrue("Booking should be accepted when seats available", result);
        assertEquals("Remaining seats should be 2", 2, trip.getNumberOfSeats());
    }

    /**
     * TC2: Booking is rejected when seats are insufficient.
     * 3 seats requested, only 2 available → rejected, seats stay 2.
     */
    @Test
    public void testCase2_InsufficientSeats() {
        Customer customer = createCustomer("C002");
        Trip trip = createTrip("2025-06-15 12:00", "2025-06-15 14:00", 2, 100.0);
        Booking booking = createBooking(customer, trip, 3, parseDate("2025-06-15 09:00"));

        boolean result = booking.isBookingEligible();

        assertFalse("Booking should be rejected when seats insufficient", result);
        assertEquals("Seats should stay 2", 2, trip.getNumberOfSeats());
    }

    /**
     * TC3: Booking is rejected at the exact two-hour cutoff.
     * Booking at 10:00 for departure at 12:00 (exactly 2h) → rejected.
     */
    @Test
    public void testCase3_ExactlyTwoHourCutoff() {
        Customer customer = createCustomer("C003");
        Trip trip = createTrip("2025-06-15 12:00", "2025-06-15 14:00", 50, 100.0);
        // Exactly 2 hours before departure
        Booking booking = createBooking(customer, trip, 3, parseDate("2025-06-15 10:00"));

        boolean result = booking.isBookingEligible();

        assertFalse("Booking should be rejected at exactly 2h cutoff", result);
        assertEquals("Seats should stay 50", 50, trip.getNumberOfSeats());
    }

    /**
     * TC4: Booking is allowed when existing trip does not overlap.
     * Existing trip 08:00-10:00, new trip 14:00-16:00 → accepted, remaining = 36.
     */
    @Test
    public void testCase4_NoOverlapWithExistingTrip() {
        Customer customer = createCustomer("C004");
        Trip existingTrip = createTrip("2025-06-15 08:00", "2025-06-15 10:00", 40, 80.0);
        Trip newTrip = createTrip("2025-06-15 14:00", "2025-06-15 16:00", 40, 90.0);

        // Create existing booking
        Booking existingBooking = createBooking(customer, existingTrip, 2, parseDate("2025-06-15 05:00"));
        customer.getBookings().add(existingBooking);
        existingTrip.getBookings().add(existingBooking);

        // New booking for non-overlapping trip
        Booking newBooking = createBooking(customer, newTrip, 4, parseDate("2025-06-15 10:00"));

        boolean result = newBooking.isBookingEligible();

        assertTrue("Booking should be accepted when no overlap", result);
        assertEquals("Remaining seats on new trip should be 36", 36, newTrip.getNumberOfSeats());
    }

    /**
     * TC5: Booking is rejected when existing trip overlaps.
     * Existing trip 09:00-11:00, new trip 10:00-12:00 → rejected.
     */
    @Test
    public void testCase5_OverlapWithExistingTrip() {
        Customer customer = createCustomer("C005");
        Trip existingTrip = createTrip("2025-06-15 09:00", "2025-06-15 11:00", 40, 80.0);
        Trip newTrip = createTrip("2025-06-15 10:00", "2025-06-15 12:00", 40, 90.0);

        // Create existing booking for overlapping trip
        Booking existingBooking = createBooking(customer, existingTrip, 2, parseDate("2025-06-15 05:00"));
        customer.getBookings().add(existingBooking);
        existingTrip.getBookings().add(existingBooking);

        // New booking for overlapping trip
        Booking newBooking = createBooking(customer, newTrip, 3, parseDate("2025-06-15 06:00"));

        boolean result = newBooking.isBookingEligible();

        assertFalse("Booking should be rejected when trip overlaps", result);
        assertEquals("Seats on new trip should stay 40", 40, newTrip.getNumberOfSeats());
    }

    /**
     * TC6: Booking is rejected when required input is missing.
     * Null customer or null trip → false.
     */
    @Test
    public void testCase6_MissingInput() {
        Customer customer = createCustomer("C006");
        Trip trip = createTrip("2025-06-15 12:00", "2025-06-15 14:00", 10, 100.0);

        // Booking with null customer
        Booking bookingNoCustomer = factory.createBooking();
        bookingNoCustomer.setNumberOfSeats(2);
        bookingNoCustomer.setBookingDate(parseDate("2025-06-15 09:00"));
        bookingNoCustomer.setTrip(trip);
        // customer is null

        // Booking with null trip
        Booking bookingNoTrip = factory.createBooking();
        bookingNoTrip.setNumberOfSeats(2);
        bookingNoTrip.setBookingDate(parseDate("2025-06-15 09:00"));
        bookingNoTrip.setCustomer(customer);
        // trip is null

        assertFalse("Should reject when customer is null", bookingNoCustomer.isBookingEligible());
        assertFalse("Should reject when trip is null", bookingNoTrip.isBookingEligible());
    }

    /**
     * TC7: Booking is rejected when booking time is missing.
     */
    @Test
    public void testCase7_MissingBookingTime() {
        Customer customer = createCustomer("C007");
        Trip trip = createTrip("2025-06-15 12:00", "2025-06-15 14:00", 10, 100.0);
        Booking booking = createBooking(customer, trip, 2, null);

        boolean result = booking.isBookingEligible();

        assertFalse("Booking should be rejected when booking date is null", result);
    }
}
