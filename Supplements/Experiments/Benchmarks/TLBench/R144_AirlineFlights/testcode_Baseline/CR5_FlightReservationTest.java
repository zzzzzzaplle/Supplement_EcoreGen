import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CR5_FlightReservationTest {

    @Test
    public void tc1_flightWithThreeConfirmations() {
        // Setup
        Airline airline = new Airline();
        Flight flight = createTestFlight("F501", true);
        airline.addFlight(flight);

        // Add confirmed reservations
        addReservation(flight, "R501-1", ReservationStatus.CONFIRMED);
        addReservation(flight, "R501-2", ReservationStatus.CONFIRMED);
        addReservation(flight, "R501-3", ReservationStatus.CONFIRMED);

        // Test
        List<Reservation> result = flight.getConfirmedReservations();

        // Assertions
        assertNotNull("Should return list of reservations", result);
        assertEquals(3, result.size());
        assertTrue("Should contain all confirmed reservations",
                result.stream().allMatch(r -> r.getStatus() == ReservationStatus.CONFIRMED));
        assertTrue("Should contain R501-1", result.stream().anyMatch(r -> r.getId().equals("R501-1")));
        assertTrue("Should contain R501-2", result.stream().anyMatch(r -> r.getId().equals("R501-2")));
        assertTrue("Should contain R501-3", result.stream().anyMatch(r -> r.getId().equals("R501-3")));
    }

    @Test
    public void tc2_noConfirmedReservations() {
        // Setup
        Airline airline = new Airline();
        Flight flight = createTestFlight("F502", true);
        airline.addFlight(flight);

        // Add pending reservations
        addReservation(flight, "R502-1", ReservationStatus.PENDING);
        addReservation(flight, "R502-2", ReservationStatus.PENDING);

        // Test
        List<Reservation> result = flight.getConfirmedReservations();

        // Assertions
        assertTrue("Should return empty list when no confirmed reservations",
                result != null && result.isEmpty());
    }

    @Test
    public void tc3_flightClosedReturnsEmpty() {
        // Setup
        Airline airline = new Airline();
        Flight flight = createTestFlight("F503", false); // closed flight
        airline.addFlight(flight);

        // Add confirmed reservation (shouldn't matter since flight is closed)
        addReservation(flight, "R503-1", ReservationStatus.CONFIRMED);

        // Test
        List<Reservation> result = flight.getConfirmedReservations();

        // Assertions
        assertTrue("Should return empty list when flight is closed",
                result != null && result.isEmpty());
    }

    @Test
    public void tc4_unknownFlightId() {
        // Setup - Test Case 4: "Unknown flight id"
        // Note: Since the implementation uses Flight object method (not flight ID parameter),
        // we cannot directly test "unknown flight id" scenario.
        // Instead, this test verifies that a flight not in the airline's flights list
        // (but flight object exists) returns empty list when it has no reservations.
        // This represents a scenario where the flight object exists but is not managed by the airline.
        Airline airline = new Airline();
        airline.addFlight(createTestFlight("F504", true));
        airline.addFlight(createTestFlight("F505", true));
        
        // Create a flight object that is NOT added to the airline
        Flight unknownFlight = createTestFlight("FX999", true);
        // No reservations added to this flight

        // Test - get confirmed reservations for flight not in airline's list
        List<Reservation> result = unknownFlight.getConfirmedReservations();

        // Assertions
        assertNotNull("Should return a list (not null)", result);
        assertTrue("Should return empty list for flight with no reservations", result.isEmpty());
        assertFalse("Flight should not be in airline's flights list", 
                airline.getFlights().contains(unknownFlight));
    }

    @Test
    public void tc5_mixedReservationStatuses() {
        // Setup
        Airline airline = new Airline();
        Flight flight = createTestFlight("F504", true);
        airline.addFlight(flight);

        // Add mixed reservations
        addReservation(flight, "R504-A", ReservationStatus.CONFIRMED);
        addReservation(flight, "R504-B", ReservationStatus.CONFIRMED);
        addReservation(flight, "R504-C", ReservationStatus.CANCELED);
        addReservation(flight, "R504-D", ReservationStatus.PENDING);

        // Test
        List<Reservation> result = flight.getConfirmedReservations();

        // Assertions
        assertNotNull("Should return list of reservations", result);
        assertEquals(2, result.size());
        assertTrue("Should contain only confirmed reservations",
                result.stream().allMatch(r -> r.getStatus() == ReservationStatus.CONFIRMED));
        assertTrue("Should contain R504-A", result.stream().anyMatch(r -> r.getId().equals("R504-A")));
        assertTrue("Should contain R504-B", result.stream().anyMatch(r -> r.getId().equals("R504-B")));
    }

    // Helper methods (A模式)
    private Airport createAirport(String id) {
        Airport airport = new Airport();
        airport.setId(id);
        return airport;
    }

    private Flight createTestFlight(String flightId, boolean openForBooking) {
        Airport departure = createAirport("AP1");
        Airport arrival = createAirport("AP2");
        Flight flight = new Flight();
        flight.setId(flightId);
        flight.setDepartureTime(new Date());
        flight.setArrivalTime(new Date());
        flight.setDepartureAirport(departure);
        flight.setArrivalAirport(arrival);
        flight.setOpenForBooking(openForBooking);
        return flight;
    }

    private Passenger createPassenger(String name) {
        Passenger passenger = new Passenger();
        passenger.setName(name);
        return passenger;
    }

    private Reservation createReservation(String id, ReservationStatus status, Passenger passenger, Flight flight) {
        Reservation reservation = new Reservation();
        reservation.setId(id);
        reservation.setStatus(status);
        reservation.setPassenger(passenger);
        reservation.setFlight(flight);
        return reservation;
    }

    private void addReservation(Flight flight, String reservationId, ReservationStatus status) {
        Passenger passenger = createPassenger("Test Passenger");
        Reservation reservation = createReservation(reservationId, status, passenger, flight);
        flight.getReservations().add(reservation);
    }
}
