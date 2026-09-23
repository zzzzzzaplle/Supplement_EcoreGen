package edu.flights;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class CR3Test {

  private FlightsFactory factory;
  private SimpleDateFormat format;

  @Before
  public void setUp() {
    factory = FlightsFactory.eINSTANCE;
    format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    format.setLenient(false);
  }

  @Test
  public void testCase1ConfirmPendingReservation() throws Exception {
    Flight flight = createFlight("F401", "AP160", "CityAA", "AP161", "CityAB", "2025-12-10 11:00:00",
        "2025-12-10 15:00:00", true);
    Customer customer = factory.createCustomer();
    Reservation reservation = attachReservation(customer, flight, "R401", "P9", ReservationStatus.PENDING);

    boolean result = customer.confirm("R401", date("2025-11-01 09:00:00"));

    assertTrue(result);
    assertEquals(ReservationStatus.CONFIRMED, reservation.getStatus());
  }

  @Test
  public void testCase2CancelConfirmedReservation() throws Exception {
    Flight flight = createFlight("F402", "AP170", "CityAC", "AP171", "CityAD", "2025-12-15 15:00:00",
        "2025-12-15 18:00:00", true);
    Customer customer = factory.createCustomer();
    Reservation reservation = attachReservation(customer, flight, "R402", "P10", ReservationStatus.CONFIRMED);

    boolean result = customer.cancel("R402", date("2025-12-01 12:00:00"));

    assertTrue(result);
    assertEquals(ReservationStatus.CANCELED, reservation.getStatus());
  }

  @Test
  public void testCase3FlightDepartedBlocksConfirmation() throws Exception {
    Flight flight = createFlight("F403", "AP180", "CityAE", "AP181", "CityAF", "2025-01-05 06:00:00",
        "2025-01-05 09:00:00", true);
    Customer customer = factory.createCustomer();
    Reservation reservation = attachReservation(customer, flight, "R403", "P11", ReservationStatus.PENDING);

    boolean result = customer.confirm("R403", date("2025-01-05 07:00:00"));

    assertFalse(result);
    assertEquals(ReservationStatus.PENDING, reservation.getStatus());
  }

  @Test
  public void testCase4ClosedFlightBlocksCancellation() throws Exception {
    Flight flight = createFlight("F404", "AP190", "CityAG", "AP191", "CityAH", "2025-02-01 09:00:00",
        "2025-02-01 12:00:00", false);
    Customer customer = factory.createCustomer();
    Reservation reservation = attachReservation(customer, flight, "R404", "P12", ReservationStatus.CONFIRMED);

    boolean result = customer.cancel("R404", date("2025-01-20 08:00:00"));

    assertFalse(result);
    assertEquals(ReservationStatus.CONFIRMED, reservation.getStatus());
  }

  @Test
  public void testCase5UnknownReservationIdentifier() throws Exception {
    Flight flight = createFlight("F405", "AP200", "CityAI", "AP201", "CityAJ", "2025-03-10 10:00:00",
        "2025-03-10 13:00:00", true);
    Customer customer20 = factory.createCustomer();
    Customer customer21 = factory.createCustomer();
    attachReservation(customer20, flight, "R405", "P13", ReservationStatus.PENDING);
    Reservation reservation406 = attachReservation(customer21, flight, "R406", "P14", ReservationStatus.PENDING);

    boolean result = customer20.confirm("R406", date("2025-02-15 09:00:00"));

    assertFalse(result);
    assertEquals(ReservationStatus.PENDING, reservation406.getStatus());
  }

  private Flight createFlight(String id, String departureAirportId, String departureCity, String arrivalAirportId,
      String arrivalCity, String departureTime, String arrivalTime, boolean openForBooking) throws ParseException {
    Flight flight = factory.createFlight();
    flight.setId(id);
    flight.setDepartureAirport(createAirport(departureAirportId, departureCity));
    flight.setArrivalAirport(createAirport(arrivalAirportId, arrivalCity));
    flight.setDepartureTime(date(departureTime));
    flight.setArrivalTime(date(arrivalTime));
    flight.setOpenForBooking(openForBooking);
    return flight;
  }

  private Airport createAirport(String airportId, String cityName) {
    Airport airport = factory.createAirport();
    airport.setId(airportId);
    City city = factory.createCity();
    airport.addCity(city);
    return airport;
  }

  private Reservation attachReservation(Customer customer, Flight flight, String reservationId, String passengerName,
      ReservationStatus status) {
    Booking booking = factory.createBooking();
    booking.setCustomer(customer);

    Passenger passenger = factory.createPassenger();
    passenger.setName(passengerName);

    Reservation reservation = factory.createReservation();
    reservation.setId(reservationId);
    reservation.setPassenger(passenger);
    reservation.setFlight(flight);
    reservation.setStatus(status);

    booking.getReservations().add(reservation);
    customer.getBookings().add(booking);
    flight.getReservations().add(reservation);
    return reservation;
  }

  private Date date(String value) throws ParseException {
    return format.parse(value);
  }
}
