package edu.rideshare.test;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import edu.rideshare.RideshareFactory;
import edu.rideshare.Driver;
import edu.rideshare.Trip;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * CR5: Validate Trip Posting Feasibility
 * Uses Driver.canPostTrip() from deepseek-v4-flash/rideshare1.
 * Implementation: null check, time validity (dep < arr), time conflict check.
 * Adjacent boundaries (one ends when another starts) are NOT conflicts.
 */
public class CR5Test {

    private RideshareFactory factory;

    @Before
    public void setUp() {
        factory = RideshareFactory.eINSTANCE;
    }

    // ---- Helpers ----

    private Driver createDriver(String id) {
        Driver d = factory.createDriver();
        d.setId(id);
        return d;
    }

    private Trip createTrip(String depTime, String arrTime) {
        Trip t = factory.createTrip();
        t.setDepartureStation("A");
        t.setArrivalStation("B");
        t.setDepartureTime(depTime);
        t.setArrivalTime(arrTime);
        t.setNumberOfSeats(10);
        t.setPrice(100.0);
        return t;
    }

    private Trip addToDriver(Driver driver, Trip trip) {
        driver.getTrips().add(trip);
        trip.setDriver(driver);
        return trip;
    }

    // ---- CR5 Test Cases ----

    /**
     * TC1: No conflict → true.
     * Existing 08:00-10:00, new 14:00-16:00.
     */
    @Test
    public void testCase1_NoConflict() {
        Driver driver = createDriver("D001");
        addToDriver(driver, createTrip("2025-06-15 08:00", "2025-06-15 10:00"));
        Trip newTrip = createTrip("2025-06-15 14:00", "2025-06-15 16:00");

        boolean result = driver.canPostTrip(newTrip);

        assertTrue("Should allow posting when no time conflict", result);
    }

    /**
     * TC2: Time periods overlap → false.
     * Existing 09:00-11:00, new 10:00-12:00.
     */
    @Test
    public void testCase2_TimeOverlap() {
        Driver driver = createDriver("D002");
        addToDriver(driver, createTrip("2025-06-15 09:00", "2025-06-15 11:00"));
        Trip newTrip = createTrip("2025-06-15 10:00", "2025-06-15 12:00");

        boolean result = driver.canPostTrip(newTrip);

        assertFalse("Should reject when time periods overlap", result);
    }

    /**
     * TC3: Back-to-back timing (adjacent boundaries) → true.
     * Existing 08:00-10:00, new 10:00-12:00.
     */
    @Test
    public void testCase3_BackToBack() {
        Driver driver = createDriver("D003");
        addToDriver(driver, createTrip("2025-06-15 08:00", "2025-06-15 10:00"));
        Trip newTrip = createTrip("2025-06-15 10:00", "2025-06-15 12:00");

        boolean result = driver.canPostTrip(newTrip);

        assertTrue("Should allow back-to-back timing", result);
    }

    /**
     * TC4: Fully enclosed time window → false.
     * Existing 08:00-14:00, new 09:00-11:00.
     */
    @Test
    public void testCase4_FullyEnclosed() {
        Driver driver = createDriver("D004");
        addToDriver(driver, createTrip("2025-06-15 08:00", "2025-06-15 14:00"));
        Trip newTrip = createTrip("2025-06-15 09:00", "2025-06-15 11:00");

        boolean result = driver.canPostTrip(newTrip);

        assertFalse("Should reject fully enclosed time window", result);
    }

    /**
     * TC5: Multiple existing schedules checked → false if any overlaps.
     * Two existing: 08:00-10:00, 14:00-16:00. New: 15:00-17:00.
     */
    @Test
    public void testCase5_MultipleSchedules() {
        Driver driver = createDriver("D005");
        addToDriver(driver, createTrip("2025-06-15 08:00", "2025-06-15 10:00"));
        addToDriver(driver, createTrip("2025-06-15 14:00", "2025-06-15 16:00"));
        Trip newTrip = createTrip("2025-06-15 15:00", "2025-06-15 17:00");

        boolean result = driver.canPostTrip(newTrip);

        assertFalse("Should reject when overlapping any existing schedule", result);
    }

    /**
     * TC6: Missing (null) new trip → false.
     */
    @Test
    public void testCase6_MissingTrip() {
        Driver driver = createDriver("D006");

        boolean result = driver.canPostTrip(null);

        assertFalse("Should reject null trip", result);
    }

    /**
     * TC7: Invalid time window (departure > arrival) → false.
     */
    @Test
    public void testCase7_InvalidTimeWindow() {
        Driver driver = createDriver("D007");
        Trip newTrip = createTrip("2025-06-15 14:00", "2025-06-15 10:00");  // departure after arrival

        boolean result = driver.canPostTrip(newTrip);

        assertFalse("Should reject trip with departure after arrival", result);
    }

    /**
     * TC8: Identical time window → false.
     * Existing 09:00-11:00, new 09:00-11:00.
     */
    @Test
    public void testCase8_IdenticalTimeWindow() {
        Driver driver = createDriver("D008");
        addToDriver(driver, createTrip("2025-06-15 09:00", "2025-06-15 11:00"));
        Trip newTrip = createTrip("2025-06-15 09:00", "2025-06-15 11:00");

        boolean result = driver.canPostTrip(newTrip);

        assertFalse("Should reject identical time window", result);
    }
}
