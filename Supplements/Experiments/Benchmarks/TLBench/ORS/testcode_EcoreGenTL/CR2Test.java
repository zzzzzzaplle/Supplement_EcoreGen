package edu.rideshare.test;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import edu.rideshare.RideshareFactory;
import edu.rideshare.Customer;
import edu.rideshare.Driver;
import edu.rideshare.Trip;
import edu.rideshare.MembershipPackage;
import edu.rideshare.Award;

/**
 * CR2: Calculate Discounted Trip Price
 * Uses Trip.calculateDiscountedPrice() from deepseek-v4-flash/rideshare1.
 * Implementation: 20% discount when DISCOUNTS award + booking >=24h before departure.
 */
public class CR2Test {

    private RideshareFactory factory;

    @Before
    public void setUp() {
        factory = RideshareFactory.eINSTANCE;
    }

    // ---- Helpers ----

    private Customer createCustomer(String id) {
        Customer c = factory.createCustomer();
        c.setId(id);
        return c;
    }

    private Trip createTrip(double price, String depTime) {
        Trip t = factory.createTrip();
        t.setDepartureStation("A");
        t.setArrivalStation("B");
        t.setDepartureTime(depTime);
        t.setArrivalTime("2025-06-15 14:00");
        t.setNumberOfSeats(10);
        t.setPrice(price);
        return t;
    }

    private MembershipPackage createMembership(Customer customer, Award... awards) {
        MembershipPackage pkg = factory.createMembershipPackage();
        for (Award a : awards) { pkg.getAwards().add(a); }
        customer.setMembershipPackage(pkg);
        return pkg;
    }

    // ---- CR2 Test Cases ----

    /**
     * TC1: Early booking with DISCOUNTS benefit → 20% off.
     * price=100, departure=2025-06-15 12:00, booking=2025-06-14 11:00 (25h before) → 80.0
     */
    @Test
    public void testCase1_EarlyBookingWithDiscount() {
        Customer customer = createCustomer("C001");
        createMembership(customer, Award.DISCOUNTS);
        Trip trip = createTrip(100.0, "2025-06-15 12:00");

        double price = trip.calculateDiscountedPrice(customer, "2025-06-14 11:00");

        assertEquals("Final price should be 80.0 with DISCOUNTS + early booking", 80.0, price, 0.01);
    }

    /**
     * TC2: Late booking (1.5h before departure) → original price.
     * price=200, departure=2025-06-15 12:00, booking=2025-06-15 10:30 (1.5h before) → 200.0
     */
    @Test
    public void testCase2_LateBooking() {
        Customer customer = createCustomer("C002");
        createMembership(customer, Award.DISCOUNTS);
        Trip trip = createTrip(200.0, "2025-06-15 12:00");

        double price = trip.calculateDiscountedPrice(customer, "2025-06-15 10:30");

        assertEquals("Late booking should return original price", 200.0, price, 0.01);
    }

    /**
     * TC3: Exact 24h boundary → discount applies.
     * price=100, departure=2025-06-15 12:00, booking=2025-06-14 12:00 (exactly 24h) → 80.0
     */
    @Test
    public void testCase3_Exact24HourBoundary() {
        Customer customer = createCustomer("C003");
        createMembership(customer, Award.DISCOUNTS);
        Trip trip = createTrip(100.0, "2025-06-15 12:00");

        double price = trip.calculateDiscountedPrice(customer, "2025-06-14 12:00");

        assertEquals("Exact 24h boundary should receive discount", 80.0, price, 0.01);
    }

    /**
     * TC4: No membership → original price.
     * price=200, no membership → 200.0
     */
    @Test
    public void testCase4_NoMembership() {
        Customer customer = createCustomer("C004");
        // No membership package
        Trip trip = createTrip(200.0, "2025-06-15 12:00");

        double price = trip.calculateDiscountedPrice(customer, "2025-06-15 09:00");

        assertEquals("No membership should return original price", 200.0, price, 0.01);
    }

    /**
     * TC5: CASHBACK-only membership → no discount.
     * price=200, CASHBACK only → 200.0
     */
    @Test
    public void testCase5_CashbackOnly() {
        Customer customer = createCustomer("C005");
        createMembership(customer, Award.CASHBACK);
        Trip trip = createTrip(200.0, "2025-06-15 12:00");

        double price = trip.calculateDiscountedPrice(customer, "2025-06-15 09:00");

        // CASHBACK doesn't qualify for DISCOUNTS → original price
        assertEquals("CASHBACK only should return original price", 200.0, price, 0.01);
    }

    /**
     * TC6: Missing membership → original price.
     * price=200, null membership → 200.0
     */
    @Test
    public void testCase6_MissingMembership() {
        Customer customer = createCustomer("C006");
        Trip trip = createTrip(200.0, "2025-06-15 12:00");

        double price = trip.calculateDiscountedPrice(customer, "2025-06-15 09:00");

        assertEquals("Missing membership should return original price", 200.0, price, 0.01);
    }

    /**
     * TC7: Missing booking time → original price.
     * price=100, null bookingTime → 100.0
     */
    @Test
    public void testCase7_MissingBookingTime() {
        Customer customer = createCustomer("C007");
        createMembership(customer, Award.DISCOUNTS);
        Trip trip = createTrip(100.0, "2025-06-15 12:00");

        double price = trip.calculateDiscountedPrice(customer, null);

        assertEquals("Missing booking time should return original price", 100.0, price, 0.01);
    }
}
