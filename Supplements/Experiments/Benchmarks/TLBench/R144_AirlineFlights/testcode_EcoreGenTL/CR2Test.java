package edu.flights;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.junit.Before;
import org.junit.Test;

public class CR2Test {

  private FlightsFactory factory;
  private SimpleDateFormat format;

  @Before
  public void setUp() {
    factory = FlightsFactory.eINSTANCE;
    format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    format.setLenient(false);
  }

  @Test
  public void testCase1TwoNewPassengersSucceed() throws Exception {
    Airline airline = factory.createAirline();
    Flight flight = createOpenFlight(airline, "F300", "AP100", "CityDep", "AP101", "CityArr",
        "2025-10-05 08:00:00", "2025-10-05 12:00:00");
    Customer customer = factory.createCustomer();

    boolean result = customer.addBooking(flight, date("2025-10-01 09:00:00"), names("Peter", "Beck"));

    assertTrue(result);
    assertEquals(1, customer.getBookings().size());
    assertEquals(2, customer.getBookings().get(0).getReservations().size());
    assertEquals(2, flight.getReservations().size());
    assertEquals(ReservationStatus.PENDING, flight.getReservations().get(0).getStatus());
    assertEquals(ReservationStatus.PENDING, flight.getReservations().get(1).getStatus());
  }

  @Test
  public void testCase2DuplicatePassengerInSameRequest() throws Exception {
    Airline airline = factory.createAirline();
    Flight flight = createOpenFlight(airline, "F301", "AP102", "CityDep", "AP103", "CityArr",
        "2025-10-05 08:00:00", "2025-10-05 10:00:00");
    Customer customer = factory.createCustomer();

    boolean result = customer.addBooking(flight, date("2025-10-01 09:00:00"), names("Alice", "Alice"));

    assertFalse(result);
    assertEquals(0, customer.getBookings().size());
    assertEquals(0, flight.getReservations().size());
  }

  @Test
  public void testCase3PassengerAlreadyBookedEarlier() throws Exception {
    Airline airline = factory.createAirline();
    Flight flight = createOpenFlight(airline, "F302", "AP104", "CityDep", "AP105", "CityArr",
        "2025-10-05 18:00:00", "2025-10-06 02:00:00");
    Customer customer = factory.createCustomer();
    Booking existingBooking = createBooking(customer);
    Reservation existingReservation = createReservation("R302-A", flight, "Jucy", ReservationStatus.PENDING);
    existingBooking.getReservations().add(existingReservation);
    // flight.getReservations().add(existingReservation);
    customer.getBookings().add(existingBooking);

    boolean result = customer.addBooking(flight, date("2025-10-01 09:00:00"), names("Jucy"));

    assertFalse(result);
    assertEquals(1, customer.getBookings().size());
    assertEquals(1, flight.getReservations().size());
  }

  @Test
  public void testCase4FlightClosedBlocksBooking() throws Exception {
    Airline airline = factory.createAirline();
    Flight flight = createFlight("F303", createAirport("AP106", "CityDep"), createAirport("AP107", "CityArr"),
        "2025-10-05 18:00:00", "2025-10-06 02:00:00", false);
    airline.addFlight(flight);
    Customer customer = factory.createCustomer();

    boolean result = customer.addBooking(flight, date("2025-10-01 09:00:00"), names("Ruby"));

    assertFalse(result);
    assertEquals(0, customer.getBookings().size());
    assertEquals(0, flight.getReservations().size());
  }

  @Test
  public void testCase5TimeAfterDepartureBlocksBooking() throws Exception {
    Airline airline = factory.createAirline();
    Flight flight = createOpenFlight(airline, "F303", "AP106", "CityDep", "AP107", "CityArr",
        "2025-10-05 18:00:00", "2025-10-06 02:00:00");
    Customer customer = factory.createCustomer();

    boolean result = customer.addBooking(flight, date("2025-10-06 09:00:00"), names("Ruby"));

    assertFalse(result);
    assertEquals(0, customer.getBookings().size());
    assertEquals(0, flight.getReservations().size());
  }

  private Flight createOpenFlight(Airline airline, String flightId, String departureAirportId, String departureCity,
      String arrivalAirportId, String arrivalCity, String departureTime, String arrivalTime) throws ParseException {
    Flight flight = createFlight(flightId, createAirport(departureAirportId, departureCity),
        createAirport(arrivalAirportId, arrivalCity), departureTime, arrivalTime, true);
    airline.addFlight(flight);
    return flight;
  }

  private Airport createAirport(String airportId, String cityName) {
    Airport airport = factory.createAirport();
    airport.setId(airportId);
    City city = factory.createCity();
    airport.addCity(city);
    return airport;
  }

  private Flight createFlight(String id, Airport departureAirport, Airport arrivalAirport, String departureTime,
      String arrivalTime, boolean openForBooking) throws ParseException {
    Flight flight = factory.createFlight();
    flight.setId(id);
    flight.setDepartureAirport(departureAirport);
    flight.setArrivalAirport(arrivalAirport);
    flight.setDepartureTime(date(departureTime));
    flight.setArrivalTime(date(arrivalTime));
    flight.setOpenForBooking(openForBooking);
    return flight;
  }

  private Booking createBooking(Customer customer) {
    Booking booking = factory.createBooking();
    booking.setCustomer(customer);
    return booking;
  }

  private Reservation createReservation(String reservationId, Flight flight, String passengerName,
      ReservationStatus status) {
    Passenger passenger = factory.createPassenger();
    passenger.setName(passengerName);

    Reservation reservation = factory.createReservation();
    reservation.setId(reservationId);
    reservation.setFlight(flight);
    reservation.setPassenger(passenger);
    reservation.setStatus(status);
    return reservation;
  }

  private EList<String> names(String... values) {
    BasicEList<String> names = new BasicEList<String>();
    for (String value : values) {
      names.add(value);
    }
    return names;
  }

  private Date date(String value) throws ParseException {
    return format.parse(value);
  }
}
