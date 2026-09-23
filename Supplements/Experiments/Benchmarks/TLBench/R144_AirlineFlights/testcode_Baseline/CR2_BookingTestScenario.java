import static org.junit.Assert.*;
import org.junit.Test;
import java.util.*;
import java.util.stream.Collectors;

public class CR2_BookingTestScenario {
    public Airport createAirport(String id) {
        Airport airport = new Airport();
        airport.setId(id);
        return airport;
    }
    public Flight createFlight(String id, Date departureTime, Date arrivalTime, Airport departureAirport,
            Airport arrivalAirport) {
        Flight flight = new Flight();
        flight.setId(id);
        flight.setDepartureTime(departureTime);
        flight.setArrivalTime(arrivalTime);
        flight.setDepartureAirport(departureAirport);
        flight.setArrivalAirport(arrivalAirport);
        return flight;
    }
    public Passenger createPassenger(String name) {
        Passenger passenger = new Passenger();
        passenger.setName(name);
        return passenger;
    }
    public Reservation createReservation(String id, ReservationStatus status, Passenger passenger, Flight flight) {
        Reservation reservation = new Reservation();
        reservation.setId(id);
        reservation.setStatus(status);
        reservation.setPassenger(passenger);
        reservation.setFlight(flight);
        return reservation;
    }
    private Booking createBooking(Customer customer) {
        Booking booking = new Booking();
        booking.setCustomer(customer);
        return booking;
    }
    @Test
    public void tc1_TwoNewPassengersSucceed() {
        Airline airline = new Airline();
        Airport departureAirport = createAirport("AP100");
        Airport arrivalAirport = createAirport("AP101");
        Date departureTime = new GregorianCalendar(2025, Calendar.OCTOBER, 5, 8, 0).getTime();
        Date arrivalTime = new GregorianCalendar(2025, Calendar.OCTOBER, 5, 12, 0).getTime();
        Flight flight = createFlight("F300", departureTime, arrivalTime, departureAirport, arrivalAirport);
        flight.setOpenForBooking(true);
        airline.addFlight(flight);

        Customer customer = new Customer();
        List<String> passengerNames = Arrays.asList("Peter", "Beck");
        Date currentTime = new GregorianCalendar(2025, Calendar.OCTOBER, 1, 9, 0).getTime();

        // Action
        boolean result = customer.addBooking(flight, currentTime, passengerNames);

        // Assertions
        assertTrue("Booking should succeed for two new passengers", result);
        assertEquals("There should be two reservations", 2, customer.getBookings().get(0).getReservations().size());
        assertEquals("First passenger name mismatch", "Peter",
                customer.getBookings().get(0).getReservations().get(0).getPassenger().getName());
        assertEquals("Second passenger name mismatch", "Beck",
                customer.getBookings().get(0).getReservations().get(1).getPassenger().getName());
        assertEquals("Customer should have 1 booking", 1, customer.getBookings().size());
        Booking booking = customer.getBookings().get(0);
        assertEquals("Booking customer should match", customer, booking.getCustomer());

        for (Reservation reservation : booking.getReservations()) {
            assertEquals("Reservation should be PENDING initially", ReservationStatus.PENDING, reservation.getStatus());
            assertEquals("Reservation flight should match", flight, reservation.getFlight());
            assertNotNull("Reservation ID should not be null", reservation.getId());
        }

        assertEquals("Flight should have 2 reservations", 2, flight.getReservations().size());
        assertTrue("Flight should have both passengers",
                flight.getReservations().stream()
                        .map(r -> r.getPassenger().getName())
                        .collect(Collectors.toList())
                        .containsAll(Arrays.asList("Peter", "Beck")));
    }
    @Test
    public void tc2_DuplicatePassengerInSameRequest() {
        // Setup
        Airline airline = new Airline();
        Airport departureAirport = createAirport("AP102");
        Airport arrivalAirport = createAirport("AP103");
        Date departureTime = new GregorianCalendar(2025, Calendar.OCTOBER, 5, 8, 0).getTime();
        Date arrivalTime = new GregorianCalendar(2025, Calendar.OCTOBER, 5, 10, 0).getTime();
        Flight flight = createFlight("F301", departureTime, arrivalTime, departureAirport, arrivalAirport);
        flight.setOpenForBooking(true);
        airline.addFlight(flight);

        Customer customer = new Customer();
        List<String> passengerNames = Arrays.asList("Alice", "Alice");
        Date currentTime = new GregorianCalendar(2025, Calendar.OCTOBER, 1, 9, 0).getTime();

        // Action
        boolean result = customer.addBooking(flight, currentTime, passengerNames);

        // Assertions
        assertFalse("Booking should fail due to duplicate passenger", result);
        assertTrue("Customer should have no bookings", customer.getBookings().isEmpty());
        assertTrue("Flight should have no reservations", flight.getReservations().isEmpty());
    }
    @Test
    public void tc3_PassengerAlreadyBookedEarlier() {
        // Setup
        Airline airline = new Airline();
        Airport departureAirport = createAirport("AP104");
        Airport arrivalAirport = createAirport("AP105");
        Date departureTime = new GregorianCalendar(2025, Calendar.OCTOBER, 5, 18, 0).getTime();
        Date arrivalTime = new GregorianCalendar(2025, Calendar.OCTOBER, 6, 2, 0).getTime();
        Flight flight = createFlight("F302", departureTime, arrivalTime, departureAirport, arrivalAirport);
        flight.setOpenForBooking(true);
        airline.addFlight(flight);

        Customer customer = new Customer();
        passengerPreExistingBooking(customer, flight);
        List<String> passengerNames = Arrays.asList("Jucy");
        Date currentTime = new GregorianCalendar(2025, Calendar.OCTOBER, 1, 9, 0).getTime();

        boolean result = customer.addBooking(flight, currentTime, passengerNames);

        assertFalse("Booking should fail because passenger 'Jucy' already booked", result);
        assertEquals("Customer should still have 1 booking", 1, customer.getBookings().size());
    }

    @Test
    public void tc4_FlightClosedBlocksBooking() {
        // Setup
        Airline airline = new Airline();
        Airport departureAirport = createAirport("AP106");
        Airport arrivalAirport = createAirport("AP107");
        Date departureTime = new GregorianCalendar(2025, Calendar.OCTOBER, 5, 18, 0).getTime();
        Date arrivalTime = new GregorianCalendar(2025, Calendar.OCTOBER, 6, 2, 0).getTime();
        Flight flight = createFlight("F303", departureTime, arrivalTime, departureAirport, arrivalAirport);
        flight.setOpenForBooking(false);
        airline.addFlight(flight);

        Customer customer = new Customer();
        List<String> passengerNames = Arrays.asList("Ruby");
        Date currentTime = new GregorianCalendar(2025, Calendar.OCTOBER, 1, 9, 0).getTime();

        boolean result = customer.addBooking(flight, currentTime, passengerNames);

        // Assertions
        assertFalse("Booking should fail as flight is closed for booking", result);
        assertTrue("Customer should have no bookings", customer.getBookings().isEmpty());
        assertTrue("Flight should have no reservations", flight.getReservations().isEmpty());
    }

    @Test
    public void tc5_TimeIsAfterTheDepartureTime() {
        // Setup
        Airline airline = new Airline();
        Airport departureAirport = createAirport("AP106");
        Airport arrivalAirport = createAirport("AP107");
        Date departureTime = new GregorianCalendar(2025, Calendar.OCTOBER, 5, 18, 0).getTime();
        Date arrivalTime = new GregorianCalendar(2025, Calendar.OCTOBER, 6, 2, 0).getTime();
        Flight flight = createFlight("F303", departureTime, arrivalTime, departureAirport, arrivalAirport);
        flight.setOpenForBooking(true);
        airline.addFlight(flight);

        Customer customer = new Customer();
        List<String> passengerNames = Arrays.asList("Ruby");
        Date currentTime = new GregorianCalendar(2025, Calendar.OCTOBER, 6, 9, 0).getTime();

        boolean result = customer.addBooking(flight, currentTime, passengerNames);

        assertFalse("Booking should fail as the current time is after the departure time", result);
        assertTrue("Customer should have no bookings", customer.getBookings().isEmpty());
        assertTrue("Flight should have no reservations", flight.getReservations().isEmpty());
    }

    private void passengerPreExistingBooking(Customer customer, Flight flight) {
        Booking booking = createBooking(customer);
        Passenger passenger = createPassenger("Jucy");
        Reservation reservation = createReservation("R302-A", ReservationStatus.PENDING, passenger, flight);
        booking.getReservations().add(reservation);
        customer.getBookings().add(booking);
    }
}
