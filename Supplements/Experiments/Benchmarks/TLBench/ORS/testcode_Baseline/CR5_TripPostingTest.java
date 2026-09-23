import static org.junit.Assert.*;
import org.junit.Test;
import java.text.ParseException;

public class CR5_TripPostingTest {
    private Driver driver;
    private Trip existingTrip;
    private Trip newTrip;

    // Helper methods
    Driver createDriver(String id) {
        Driver driver = new Driver();
        driver.setId(id);
        return driver;
    }
    
    Trip createTrip(String departureTime, String arrivalTime) {
        Trip trip = new Trip();
        trip.setDepartureTime(departureTime);
        trip.setArrivalTime(arrivalTime);
        return trip;
    }

    @Test
    public void tc1_ValidTripPostingWithTimeGap() throws ParseException {
        // Setting up driver D5
        driver = createDriver("D5");

        // Setting up existing trip
        existingTrip = createTrip("2023-12-25 09:00", "2023-12-25 11:00");

        driver.addTrip(existingTrip);

        // Setting up new trip without conflict
        newTrip = createTrip("2023-12-25 13:00", "2023-12-25 15:00");

        // Testing: canPostTrip(Trip) returns true
        assertTrue("The new trip should not conflict with existing trips.", driver.canPostTrip(newTrip));
    }

    @Test
    public void tc2_PostingDeniedDueToTimeConflict() throws ParseException {
        // Setting up driver D6
        driver = createDriver("D6");

        // Setting up existing trip
        existingTrip = createTrip("2023-12-25 14:00", "2023-12-25 16:00");

        driver.addTrip(existingTrip);

        // Setting up new conflicting trip
        newTrip = createTrip("2023-12-25 14:30", "2023-12-25 17:30");

        // Testing: canPostTrip(Trip) returns false
        assertFalse("The new trip conflicts with existing trips.", driver.canPostTrip(newTrip));
    }

    @Test
    public void tc3_BackToBackTripsAllowed() throws ParseException {
        // Setting up driver D7
        driver = createDriver("D7");

        // Setting up existing trip
        existingTrip = createTrip("2023-12-25 09:00", "2023-12-25 11:00");

        driver.addTrip(existingTrip);

        // Setting up back-to-back trip
        newTrip = createTrip("2023-12-25 11:00", "2023-12-25 13:00");

        // Testing: canPostTrip(Trip) returns true
        assertTrue("Back-to-back trips should be allowed.", driver.canPostTrip(newTrip));
    }

    @Test
    public void tc4_CompleteTimeEnclosureRejection() throws ParseException {
        // Setting up driver D8
        driver = createDriver("D8");

        // Setting up existing trip
        existingTrip = createTrip("2023-12-25 10:00", "2023-12-25 16:00");

        driver.addTrip(existingTrip);

        // Setting up new trip completely within existing trip
        newTrip = createTrip("2023-12-25 12:00", "2023-12-25 14:00");

        // Testing: canPostTrip(Trip) returns false
        assertFalse("The new trip is completely enclosed by the existing trip.", driver.canPostTrip(newTrip));
    }

    @Test
    public void tc5_MultipleExistingTripComparison() throws ParseException {
        // Setting up driver D9
        driver = createDriver("D9");

        // Setting up existing trips
        Trip trip1 = createTrip("2023-12-21 08:00", "2023-12-21 10:00");

        Trip trip2 = createTrip("2023-12-21 11:00", "2023-12-21 13:00");

        Trip trip3 = createTrip("2023-12-23 14:00", "2023-12-23 16:00");

        driver.addTrip(trip1);
        driver.addTrip(trip2);
        driver.addTrip(trip3);

        // Setting up new conflicting trip
        newTrip = createTrip("2023-12-21 09:30", "2023-12-21 10:30");

        // Testing: canPostTrip(Trip) returns false
        assertFalse("The new trip overlaps with existing trips.", driver.canPostTrip(newTrip));
    }

    @Test
    public void tc6_TripPostingWithNullTrip() throws ParseException {
        // Test logic: "Trip posting with null trip"
        driver = createDriver("D10");
        assertFalse("Should return false when posting null trip", driver.canPostTrip(null));
    }

    @Test
    public void tc7_invalidTripTimeWindowShouldBeRejected() throws ParseException {
        driver = createDriver("D11");
        newTrip = createTrip("2023-12-25 15:00", "2023-12-25 13:00");
        assertFalse("Trip with departure after arrival should be rejected", driver.canPostTrip(newTrip));
    }

    @Test
    public void tc8_identicalTimePeriodsShouldConflict() throws ParseException {
        driver = createDriver("D12");
        existingTrip = createTrip("2023-12-25 10:00", "2023-12-25 12:00");
        driver.addTrip(existingTrip);
        newTrip = createTrip("2023-12-25 10:00", "2023-12-25 12:00");
        assertFalse("Identical time periods should be treated as conflict", driver.canPostTrip(newTrip));
    }
}

/*
 * compile_result:
 * 
 * 
 * 
 * run_result:
 * JUnit version 4.13.2
 * .....
 * Time: 0.035
 * 
 * OK (5 tests)
 * 
 * 
 */
