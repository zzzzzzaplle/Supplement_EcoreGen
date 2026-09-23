package edu.rideshare.test;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import edu.rideshare.RideshareFactory;
import edu.rideshare.Customer;
import edu.rideshare.Trip;
import edu.rideshare.Booking;
import edu.rideshare.MembershipPackage;
import edu.rideshare.Award;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * CR4: Compute Monthly Reward Points
 * Uses Customer.computeMonthlyRewardPoints() from deepseek-v4-flash/rideshare1.
 * Implementation: POINTS award required, seats * 5 per booking in target month.
 */
public class CR4Test {

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

    private Trip createTrip(double price) {
        Trip t = factory.createTrip();
        t.setDepartureStation("A");
        t.setArrivalStation("B");
        t.setDepartureTime("2025-06-15 10:00");
        t.setArrivalTime("2025-06-15 12:00");
        t.setNumberOfSeats(20);
        t.setPrice(price);
        return t;
    }

    private Booking createBooking(Customer customer, Trip trip, int seats, Date bookingDate) {
        Booking b = factory.createBooking();
        b.setNumberOfSeats(seats);
        b.setBookingDate(bookingDate);
        b.setCustomer(customer);
        b.setTrip(trip);
        customer.getBookings().add(b);
        trip.getBookings().add(b);
        return b;
    }

    private MembershipPackage createMembership(Customer customer, Award... awards) {
        MembershipPackage pkg = factory.createMembershipPackage();
        for (Award a : awards) { pkg.getAwards().add(a); }
        customer.setMembershipPackage(pkg);
        return pkg;
    }

    private Date parseDate(String str) {
        try { return dtFormat.parse(str); }
        catch (ParseException e) { throw new RuntimeException(e); }
    }

    // ---- CR4 Test Cases ----

    /**
     * TC1: Multiple bookings in same month accumulate points.
     * 2 seats + 3 seats in June → (2+3)*5 = 25.
     */
    @Test
    public void testCase1_MultipleBookingsSameMonth() {
        Customer customer = createCustomer("C001");
        createMembership(customer, Award.POINTS);
        Trip trip1 = createTrip(100.0);
        Trip trip2 = createTrip(150.0);

        createBooking(customer, trip1, 2, parseDate("2025-06-10 09:00"));
        createBooking(customer, trip2, 3, parseDate("2025-06-20 09:00"));

        int points = customer.computeMonthlyRewardPoints("2025-06");

        assertEquals("Total points should be 25", 25, points);
    }

    /**
     * TC2: Booking outside target month → 0 points.
     */
    @Test
    public void testCase2_BookingOutsideMonth() {
        Customer customer = createCustomer("C002");
        createMembership(customer, Award.POINTS);
        Trip trip = createTrip(100.0);

        createBooking(customer, trip, 3, parseDate("2025-05-15 09:00"));

        int points = customer.computeMonthlyRewardPoints("2025-06");

        assertEquals("Points should be 0 for booking outside target month", 0, points);
    }

    /**
     * TC3: Only in-month bookings counted.
     * June booking (3 seats) + May booking (2 seats) → 15.
     */
    @Test
    public void testCase3_OnlyInMonthCounted() {
        Customer customer = createCustomer("C003");
        createMembership(customer, Award.POINTS);
        Trip trip1 = createTrip(100.0);
        Trip trip2 = createTrip(120.0);

        createBooking(customer, trip1, 3, parseDate("2025-06-10 09:00"));
        createBooking(customer, trip2, 2, parseDate("2025-05-15 09:00"));

        int points = customer.computeMonthlyRewardPoints("2025-06");

        assertEquals("Points should be 15 (only June counted)", 15, points);
    }

    /**
     * TC4: Multiple seats earn multiplied points.
     * 1 booking, 2 seats → 2*5 = 10.
     */
    @Test
    public void testCase4_MultipliedPointsForSeats() {
        Customer customer = createCustomer("C004");
        createMembership(customer, Award.POINTS);
        Trip trip = createTrip(100.0);

        createBooking(customer, trip, 2, parseDate("2025-06-10 09:00"));

        int points = customer.computeMonthlyRewardPoints("2025-06");

        assertEquals("Points should be 10 for 2 seats", 10, points);
    }

    /**
     * TC5: Large seat counts calculated independently per rider.
     * Rider1: 100 seats → 500; Rider2: 50 seats → 250.
     */
    @Test
    public void testCase5_LargeSeatCountsIndependent() {
        Customer customer1 = createCustomer("C005a");
        createMembership(customer1, Award.POINTS);
        Trip trip1 = createTrip(500.0);
        createBooking(customer1, trip1, 100, parseDate("2025-06-10 09:00"));

        Customer customer2 = createCustomer("C005b");
        createMembership(customer2, Award.POINTS);
        Trip trip2 = createTrip(250.0);
        createBooking(customer2, trip2, 50, parseDate("2025-06-10 09:00"));

        int points1 = customer1.computeMonthlyRewardPoints("2025-06");
        int points2 = customer2.computeMonthlyRewardPoints("2025-06");

        assertEquals("Rider 1 should receive 500 points", 500, points1);
        assertEquals("Rider 2 should receive 250 points", 250, points2);
    }

    /**
     * TC6: Missing membership → 0 points.
     */
    @Test
    public void testCase6_NoMembership() {
        Customer customer = createCustomer("C006");
        // No membership package
        Trip trip = createTrip(100.0);
        createBooking(customer, trip, 3, parseDate("2025-06-10 09:00"));

        int points = customer.computeMonthlyRewardPoints("2025-06");

        assertEquals("Points should be 0 without membership", 0, points);
    }

    /**
     * TC7: Missing target month → 0 points.
     */
    @Test
    public void testCase7_MissingTargetMonth() {
        Customer customer = createCustomer("C007");
        createMembership(customer, Award.POINTS);
        Trip trip = createTrip(100.0);
        createBooking(customer, trip, 3, parseDate("2025-06-10 09:00"));

        int points = customer.computeMonthlyRewardPoints(null);

        assertEquals("Points should be 0 when target month is null", 0, points);
    }
}
