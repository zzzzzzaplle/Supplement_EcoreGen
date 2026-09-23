import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;

public class CR3_ReservationTest {

    // Helper methods
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

    private Passenger createPassenger(String name) {
        Passenger passenger = new Passenger();
        passenger.setName(name);
        return passenger;
    }

    private Booking createBooking(Customer customer) {
        Booking booking = new Booking();
        booking.setCustomer(customer);
        return booking;
    }

    private Reservation createReservation(String id, ReservationStatus status, Passenger passenger, Flight flight) {
        Reservation reservation = new Reservation();
        reservation.setId(id);
        reservation.setStatus(status);
        reservation.setPassenger(passenger);
        reservation.setFlight(flight);
        return reservation;
    }

    @Test
    public void testConfirmPendingReservation() {
        // Setup for Test Case 1
        Airline airline = new Airline();
        Airport departureAirport = createAirport("AP160");
        Airport arrivalAirport = createAirport("AP161");

        // Add cities served by airports
        City cityAA = new City();
        City cityAB = new City();
        departureAirport.addCity(cityAA);
        arrivalAirport.addCity(cityAB);

        Flight flight = createFlight("F401", new GregorianCalendar(2025, Calendar.DECEMBER, 10, 11, 0).getTime(),
                new GregorianCalendar(2025, Calendar.DECEMBER, 10, 15, 0).getTime(), departureAirport, arrivalAirport);
        flight.setOpenForBooking(true);
        airline.addFlight(flight);

        Customer customer = new Customer();
        Booking booking = createBooking(customer);
        Passenger passenger = createPassenger("P9");
        Reservation reservation = createReservation("R401", ReservationStatus.PENDING, passenger, flight);
        booking.setReservations(Collections.singletonList(reservation));
        customer.setBookings(Collections.singletonList(booking));

        Date now = new GregorianCalendar(2025, Calendar.NOVEMBER, 1, 9, 0).getTime();

        // Invoke method
        boolean result = customer.confirm("R401", now);

        // Assertions
        assertTrue("Failed to confirm pending reservation", result);
        assertEquals("Reservation status mismatch", ReservationStatus.CONFIRMED, reservation.getStatus());
    }

    @Test
    public void testCancelConfirmedReservation() {
        // Setup for Test Case 2
        Airline airline = new Airline();
        Airport departureAirport = createAirport("AP170");
        Airport arrivalAirport = createAirport("AP171");

        // Add cities served by airports
        City cityAC = new City();
        City cityAD = new City();

        departureAirport.addCity(cityAC);
        arrivalAirport.addCity(cityAD);

        Flight flight = createFlight("F402", new GregorianCalendar(2025, Calendar.DECEMBER, 15, 15, 0).getTime(), null,
                departureAirport, arrivalAirport);
        flight.setOpenForBooking(true);
        airline.addFlight(flight);

        Customer customer = new Customer();
        Booking booking = createBooking(customer);
        Passenger passenger = createPassenger("P10");
        Reservation reservation = createReservation("R402", ReservationStatus.CONFIRMED, passenger, flight);
        booking.setReservations(Collections.singletonList(reservation));
        customer.setBookings(Collections.singletonList(booking));

        Date now = new GregorianCalendar(2025, Calendar.DECEMBER, 1, 12, 0).getTime();

        // Invoke method
        boolean result = customer.cancel("R402", now);

        // Assertions
        assertTrue("Failed to cancel confirmed reservation", result);
        assertEquals("Reservation status mismatch", ReservationStatus.CANCELED, reservation.getStatus());
    }

    @Test
    public void testFlightDepartedBlocksConfirmation() {
        // Setup for Test Case 3
        Airline airline = new Airline();
        Airport departureAirport = createAirport("AP180");
        Airport arrivalAirport = createAirport("AP181");

        // Add cities served by airports
        City cityAE = new City();
        City cityAF = new City();

        departureAirport.addCity(cityAE);
        arrivalAirport.addCity(cityAF);

        Flight flight = createFlight("F403", new GregorianCalendar(2025, Calendar.JANUARY, 5, 6, 0).getTime(), null,
                departureAirport, arrivalAirport);
        flight.setOpenForBooking(true);
        airline.addFlight(flight);

        Passenger passenger = createPassenger("P11");
        Reservation reservation = createReservation("R403", ReservationStatus.PENDING, passenger, flight);

        Date now = new GregorianCalendar(2025, Calendar.JANUARY, 5, 7, 0).getTime();

        // Invoke method
        Customer customer = new Customer();
        Booking booking = createBooking(customer);
        booking.setReservations(Collections.singletonList(reservation));
        customer.setBookings(Arrays.asList(booking));

        boolean result = customer.confirm(reservation.getId(), now);

        // Assertions
        assertFalse("Confirmed reservation after flight departure", result);
        assertEquals("Reservation status should remain unchanged", ReservationStatus.PENDING, reservation.getStatus());
    }

    @Test
    public void testClosedFlightBlocksCancellation() {
        // Setup for Test Case 4
        Airline airline = new Airline();
        Airport departureAirport = createAirport("AP190");
        Airport arrivalAirport = createAirport("AP191");

        // Add cities served by airports
        City cityAG = new City();
        City cityAH = new City();

        departureAirport.addCity(cityAG);
        arrivalAirport.addCity(cityAH);

        Flight flight = createFlight("F404", new GregorianCalendar(2025, Calendar.FEBRUARY, 1, 9, 0).getTime(), null,
                departureAirport, arrivalAirport);
        flight.setOpenForBooking(false);
        airline.addFlight(flight);

        Passenger passenger = createPassenger("P12");
        Reservation reservation = createReservation("R404", ReservationStatus.CONFIRMED, passenger, flight);

        Date now = new GregorianCalendar(2025, Calendar.JANUARY, 20, 8, 0).getTime();

        // Invoke method
        Customer customer = new Customer();
        Booking booking = createBooking(customer);
        booking.setReservations(Collections.singletonList(reservation));
        customer.setBookings(Arrays.asList(booking));

        boolean result = customer.cancel(reservation.getId(), now);

        // Assertions
        assertFalse("Canceled reservation on closed flight", result);
        assertEquals("Reservation status should remain unchanged", ReservationStatus.CONFIRMED,
                reservation.getStatus());
    }

    @Test
    public void testUnknownReservationIdentifier() {
        // Setup for Test Case 5
        Airline airline = new Airline();
        Airport departureAirport = createAirport("AP200");
        Airport arrivalAirport = createAirport("AP201");

        // Add cities served by airports
        City cityAI = new City();
        City cityAJ = new City();

        departureAirport.addCity(cityAI);
        arrivalAirport.addCity(cityAJ);

        Flight flight = createFlight("F405", new GregorianCalendar(2025, Calendar.MARCH, 10, 10, 0).getTime(), null,
                departureAirport, arrivalAirport);
        flight.setOpenForBooking(true);
        airline.addFlight(flight);

        Customer customer1 = new Customer();
        Booking booking1 = createBooking(customer1);
        Passenger passenger1 = createPassenger("P13");
        Reservation reservation1 = createReservation("R405", ReservationStatus.PENDING, passenger1, flight);
        booking1.setReservations(Collections.singletonList(reservation1));
        customer1.setBookings(Collections.singletonList(booking1));

        Customer customer2 = new Customer();
        Booking booking2 = createBooking(customer2);
        Passenger passenger2 = createPassenger("P14");
        Reservation reservation2 = createReservation("R406", ReservationStatus.PENDING, passenger2, flight);
        booking2.setReservations(Collections.singletonList(reservation2));
        customer2.setBookings(Collections.singletonList(booking2));

        Date now = new GregorianCalendar(2025, Calendar.FEBRUARY, 15, 9, 0).getTime();

        // Invoke method
        boolean result = customer1.confirm("R406", now);

        // Assertions
        assertFalse("Confirmed reservation with unknown identifier", result);
    }
}
