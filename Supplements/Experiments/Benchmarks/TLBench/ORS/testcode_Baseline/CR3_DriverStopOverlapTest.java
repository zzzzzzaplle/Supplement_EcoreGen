import java.util.*;
import java.text.ParseException;
import static org.junit.Assert.*;
import org.junit.Test;

public class CR3_DriverStopOverlapTest {

    // Helper methods
    Driver createDriver() {
        return new Driver();
    }
    
    Trip createTrip() {
        return new Trip();
    }
    
    Stop createStop(String stopStation) {
        Stop stop = new Stop();
        stop.setStopStation(stopStation);
        return stop;
    }

    @Test
    public void tc1_sharedStopInIndirectTrips() throws ParseException {
        // Setup for Test Case 1 - Shared stop in indirect trips
        Driver driverD3 = createDriver();

        Trip tripA1 = createTrip();
        tripA1.addStop(createStop("CityX"));
        tripA1.addStop(createStop("CityY"));

        Trip tripA2 = createTrip();
        tripA2.addStop(createStop("CityY"));
        tripA2.addStop(createStop("CityZ"));

        assertTrue("Expected common stop in trips A1 and A2",
                driverD3.checkStopOverlap(tripA1, tripA2));
    }

    @Test
    public void tc2_noCommonStopsInIndirectTrips() throws ParseException {
        // Setup for Test Case 2 - No common stops in indirect trips
        Driver driverD4 = createDriver();

        Trip tripB1 = createTrip();
        tripB1.addStop(createStop("CityM"));
        tripB1.addStop(createStop("CityN"));

        Trip tripB2 = createTrip();
        tripB2.addStop(createStop("CityP"));
        tripB2.addStop(createStop("CityQ"));

        assertFalse("Expected no common stops between trips B1 and B2",
                driverD4.checkStopOverlap(tripB1, tripB2));
    }

    @Test
    public void tc3_emptyStopListsComparison() throws ParseException {
        // Setup for Test Case 3 - Empty stop lists comparison
        Driver driver = createDriver();

        Trip tripC1 = createTrip();

        Trip tripC2 = createTrip();

        assertFalse("Expected no common stops with empty lists",
                driver.checkStopOverlap(tripC1, tripC2));
    }

    @Test
    public void tc4_multipleSharedStopsDetection() throws ParseException {
        // Setup for Test Case 4 - Multiple shared stops detection
        Driver driver = createDriver();

        Trip tripD1 = createTrip();
        tripD1.addStop(createStop("A"));
        tripD1.addStop(createStop("B"));
        tripD1.addStop(createStop("C"));

        Trip tripD2 = createTrip();
        tripD2.addStop(createStop("X"));
        tripD2.addStop(createStop("B"));
        tripD2.addStop(createStop("C"));

        assertTrue("Expected shared stops 'B' and 'C' between trips D1 and D2",
                driver.checkStopOverlap(tripD1, tripD2));
    }

    @Test
    public void tc5_caseSensitiveStopComparison() throws ParseException {
        // Setup for Test Case 5 - Case-sensitive stop comparison
        Driver driver = createDriver();

        Trip tripE1 = createTrip();
        tripE1.addStop(createStop("Boston"));

        Trip tripE2 = createTrip();
        tripE2.addStop(createStop("boston"));

        assertFalse("Expected no common stops due to case sensitivity",
                driver.checkStopOverlap(tripE1, tripE2));
    }

    @Test
    public void tc6_overlapCheckWithNullTrips() throws ParseException {
        // Test logic: "Overlap check with null trips"
        Driver driver = createDriver();
        Trip trip = createTrip();
        assertFalse("Expected no overlap with null trips", driver.checkStopOverlap(null, trip));
        assertFalse("Expected no overlap with null trips", driver.checkStopOverlap(trip, null));
    }
}
