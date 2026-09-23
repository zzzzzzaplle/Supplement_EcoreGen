package edu.rideshare.test;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import edu.rideshare.RideshareFactory;
import edu.rideshare.Driver;
import edu.rideshare.Trip;
import edu.rideshare.Stop;

/**
 * CR3: Check Stop Overlap for Indirect Trips
 * Uses Driver.checkStopOverlap() from deepseek-v4-flash/rideshare1.
 * Implementation: checks null trips, empty stop lists, case-sensitive comparison.
 * getStopStations() returns only intermediate stop stations (not dep/arr).
 */
public class CR3Test {

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

    private Trip createTrip() {
        Trip t = factory.createTrip();
        t.setDepartureStation("StartA");
        t.setArrivalStation("EndA");
        t.setDepartureTime("2025-06-15 10:00");
        t.setArrivalTime("2025-06-15 12:00");
        t.setNumberOfSeats(10);
        t.setPrice(100.0);
        return t;
    }

    private void addStop(Trip trip, String stationName) {
        Stop s = factory.createStop();
        s.setStopStation(stationName);
        trip.getStops().add(s);
    }

    // ---- CR3 Test Cases ----

    /**
     * TC1: Two indirect trips share a common stop → true.
     */
    @Test
    public void testCase1_CommonStop() {
        Driver driver = createDriver("D001");
        Trip trip1 = createTrip();
        addStop(trip1, "StationB");
        addStop(trip1, "StationC");

        Trip trip2 = createTrip();
        addStop(trip2, "StationC");  // common with trip1
        addStop(trip2, "StationZ");

        boolean result = driver.checkStopOverlap(trip1, trip2);

        assertTrue("Should detect common stop", result);
    }

    /**
     * TC2: Two indirect trips do not share any stop → false.
     */
    @Test
    public void testCase2_NoCommonStop() {
        Driver driver = createDriver("D002");
        Trip trip1 = createTrip();
        addStop(trip1, "StationB");
        addStop(trip1, "StationC");

        Trip trip2 = createTrip();
        addStop(trip2, "StationM");
        addStop(trip2, "StationN");

        boolean result = driver.checkStopOverlap(trip1, trip2);

        assertFalse("Should not detect overlap with different stops", result);
    }

    /**
     * TC3: Trips without stops → false (empty stop lists).
     */
    @Test
    public void testCase3_EmptyStopLists() {
        Driver driver = createDriver("D003");
        Trip trip1 = createTrip();  // no intermediate stops
        Trip trip2 = createTrip();  // no intermediate stops

        boolean result = driver.checkStopOverlap(trip1, trip2);

        assertFalse("Empty stop lists should return false", result);
    }

    /**
     * TC4: Multiple common stops → true.
     */
    @Test
    public void testCase4_MultipleCommonStops() {
        Driver driver = createDriver("D004");
        Trip trip1 = createTrip();
        addStop(trip1, "StationB");
        addStop(trip1, "StationC");
        addStop(trip1, "StationD");

        Trip trip2 = createTrip();
        addStop(trip2, "StationB");  // common
        addStop(trip2, "StationD");  // common

        boolean result = driver.checkStopOverlap(trip1, trip2);

        assertTrue("Should detect overlap with multiple common stops", result);
    }

    /**
     * TC5: Stop comparison is case-sensitive → false.
     */
    @Test
    public void testCase5_CaseSensitive() {
        Driver driver = createDriver("D005");
        Trip trip1 = createTrip();
        addStop(trip1, "CentralStation");

        Trip trip2 = createTrip();
        addStop(trip2, "centralstation");

        boolean result = driver.checkStopOverlap(trip1, trip2);

        assertFalse("Comparison should be case-sensitive", result);
    }

    /**
     * TC6: A missing (null) trip → false.
     */
    @Test
    public void testCase6_MissingTrip() {
        Driver driver = createDriver("D006");
        Trip trip1 = createTrip();
        addStop(trip1, "StationB");

        boolean result1 = driver.checkStopOverlap(trip1, null);
        boolean result2 = driver.checkStopOverlap(null, trip1);

        assertFalse("Should return false when second trip is null", result1);
        assertFalse("Should return false when first trip is null", result2);
    }
}
