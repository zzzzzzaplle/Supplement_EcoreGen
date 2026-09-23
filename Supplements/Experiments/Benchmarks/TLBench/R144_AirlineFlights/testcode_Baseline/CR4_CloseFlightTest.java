import static org.junit.Assert.*;
import org.junit.Test;
import java.util.*;
import java.util.stream.Collectors;
// Other required imports

public class CR4_CloseFlightTest {

    private Airline airline;
    private Flight flight;
    private Date now;

    // Helper methods (A模式)
    private Airport createAirport(String id) {
        Airport airport = new Airport();
        airport.setId(id);
        return airport;
    }

    private Flight createFlight(String id, Date departureTime, Date arrivalTime, Airport departureAirport, Airport arrivalAirport) {
        Flight flight = new Flight();
        flight.setId(id);
        flight.setDepartureTime(departureTime);
        flight.setArrivalTime(arrivalTime);
        flight.setDepartureAirport(departureAirport);
        flight.setArrivalAirport(arrivalAirport);
        return flight;
    }

    private Booking createBooking(Customer customer) {
        Booking booking = new Booking();
        booking.setCustomer(customer);
        return booking;
    }

    @Test
    public void testCase1_NoReservationsToCancel() {
        // Setup
        airline = new Airline();
        Airport departureAirport = createAirport("AP10");
        Airport arrivalAirport = createAirport("AP11");
        flight = createFlight("F200", new Date(2025, 6, 20, 9, 0, 0), new Date(2025, 6, 20, 13, 0, 0), departureAirport,
                arrivalAirport);
        flight.setOpenForBooking(true);
        airline.addFlight(flight);

        now = new Date(2025, 5, 1, 8, 0, 0);

        // Test
        boolean result = airline.closeFlight("F200", now);

        // Assert
        assertTrue("Flight should be closed successfully", result);
        assertFalse("Flight should now be closed", flight.isOpenForBooking());
        assertEquals("Airline should have 1 flight", 1, airline.getFlights().size());
        assertTrue("Airline should contain the flight", airline.getFlights().contains(flight));
        assertEquals("Flight ID should match", "F200", flight.getId());
        assertEquals("Departure airport ID should match", "AP10", flight.getDepartureAirport().getId());
        assertEquals("Arrival airport ID should match", "AP11", flight.getArrivalAirport().getId());
        assertTrue("Flight should have no reservations", flight.getReservations().isEmpty());
        assertTrue("Flight should have no confirmed reservations", flight.getConfirmedReservations().isEmpty());
    }

    @Test
    public void testCase2_ThreeConfirmedReservationsCanceled() {
        // Setup
        airline = new Airline();
        Airport departureAirport = createAirport("AP12");
        Airport arrivalAirport = createAirport("AP13");
        flight = createFlight("F201", new Date(2025, 7, 15, 14, 0, 0), new Date(2025, 7, 15, 18, 0, 0), departureAirport,
                arrivalAirport);
        flight.setOpenForBooking(true);
        airline.addFlight(flight);

        // Create reservations
        Customer customer = new Customer();
        Booking booking = createBooking(customer);
        booking.createReservation(flight, "Passenger1", new Date(2025, 6, 10, 12, 0, 0));
        booking.createReservation(flight, "Passenger2", new Date(2025, 6, 10, 12, 0, 0));
        booking.createReservation(flight, "Passenger3", new Date(2025, 6, 10, 12, 0, 0));

        // Confirm all reservations
        for (Reservation reservation : flight.getReservations()) {
            reservation.setStatus(ReservationStatus.CONFIRMED);
        }

        now = new Date(2025, 6, 10, 12, 0, 0);

        // Test
        boolean result = airline.closeFlight("F201", now);

        // Assert
        assertTrue("Flight should be closed successfully", result);
        assertFalse("Flight should now be closed", flight.isOpenForBooking());

        // Verify reservations were canceled
        long canceledCount = flight.getReservations().stream()
                .filter(r -> r.getStatus() == ReservationStatus.CANCELED)
                .count();
        assertEquals("All confirmed reservations should be canceled", 3, canceledCount);
        assertEquals("Booking should have 3 reservations", 3, booking.getReservations().size());
    }

    @Test
    public void testCase3_FlightAlreadyClosed() {
        // Setup
        airline = new Airline();
        Airport departureAirport = createAirport("AP202");
        Airport arrivalAirport = createAirport("AP203");
        flight = createFlight("F202", new Date(2025, 8, 10, 11, 0, 0), new Date(2025, 8, 10, 13, 30, 0), departureAirport, arrivalAirport);
        flight.setOpenForBooking(false);
        airline.addFlight(flight);

        now = new Date(2025, 7, 1, 9, 0, 0);

        // Test
        boolean result = airline.closeFlight("F202", now);

        // Assert
        assertFalse("Should not be able to close already closed flight", result);
        assertFalse("Flight should remain closed", flight.isOpenForBooking());
    }

    @Test
    public void testCase4_CloseOnDepartureDay() {
        // Setup
        airline = new Airline();
        Airport departureAirport = createAirport("AP204");
        Airport arrivalAirport = createAirport("AP205");
        flight = createFlight("F203", new Date(2025, 9, 10, 9, 0, 0), new Date(2025, 9, 10, 15, 30, 0), departureAirport, arrivalAirport);
        flight.setOpenForBooking(true);
        airline.addFlight(flight);

        // Create two confirmed reservations (R203-1, R203-2) as per NLTC.md
        Customer customer = new Customer();
        Booking booking = createBooking(customer);
        booking.createReservation(flight, "Passenger1", new Date(2025, 8, 1, 10, 0, 0));
        booking.createReservation(flight, "Passenger2", new Date(2025, 8, 1, 10, 0, 0));
        
        // Confirm all reservations
        for (Reservation reservation : flight.getReservations()) {
            reservation.setStatus(ReservationStatus.CONFIRMED);
        }

        now = new Date(2025, 9, 10, 9, 10, 0);

        // Test
        boolean result = airline.closeFlight("F203", now);

        // Assert
        assertFalse("Should not be able to close flight after departure time", result);
        assertTrue("Flight should remain open for booking", flight.isOpenForBooking());
        assertTrue("Current time should be after departure time", now.after(flight.getDepartureTime()));
        assertTrue("Arrival time should be after departure time",
                flight.getArrivalTime().after(flight.getDepartureTime()));
        // Verify reservations remain confirmed (not canceled) since flight was not closed
        assertEquals("Should have 2 reservations", 2, flight.getReservations().size());
        long confirmedCount = flight.getReservations().stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .count();
        assertEquals("Reservations should remain confirmed", 2, confirmedCount);
    }

    @Test
    public void testCase5_AttemptToCloseAfterDeparture() {
        // Setup
        airline = new Airline();
        Airport departureAirport = createAirport("AP206");
        Airport arrivalAirport = createAirport("AP207");
        flight = createFlight("F204", new Date(2025, 4, 1, 22, 0, 0), new Date(2025, 4, 2, 1, 30, 0), departureAirport, arrivalAirport);
        flight.setOpenForBooking(true);
        airline.addFlight(flight);

        now = new Date(2025, 4, 1, 22, 5, 0);

        // Test
        boolean result = airline.closeFlight("F204", now);

        // Assert
        assertFalse("Should not be able to close flight after departure", result);
        assertTrue("Flight should remain open for booking", flight.isOpenForBooking());
        assertFalse("Current time should be after departure time", flight.getDepartureTime().after(now));
    }
}
