package edu.flights;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class CR4Test {

  private FlightsFactory factory;
  private SimpleDateFormat format;

  @Before
  public void setUp() {
    factory = FlightsFactory.eINSTANCE;
    format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    format.setLenient(false);
  }

  @Test
  public void testCase1NoReservationsToCancel() throws Exception {
    Airline airline = factory.createAirline();
    Flight flight = createFlight("F200", "AP10", "CityJ", "AP11", "CityK", "2025-06-20 09:00:00",
        "2025-06-20 13:00:00", true);
    airline.addFlight(flight);

    boolean result = airline.closeFlight("F200", date("2025-05-01 08:00:00"));

    assertTrue(result);
    assertFalse(flight.isOpenForBooking());
    assertEquals(0, flight.getReservations().size());
  }

  @Test
  public void testCase2ThreeConfirmedReservationsCanceled() throws Exception {
    Airline airline = factory.createAirline();
    Flight flight = createFlight("F201", "AP12", "CityL", "AP13", "CityM", "2025-07-15 14:00:00",
        "2025-07-15 18:00:00", true);
    airline.addFlight(flight);
    createReservation(flight, "R201-1", "P1", ReservationStatus.CONFIRMED);
    createReservation(flight, "R201-2", "P2", ReservationStatus.CONFIRMED);
    createReservation(flight, "R201-3", "P3", ReservationStatus.CONFIRMED);

    boolean result = airline.closeFlight("F201", date("2025-06-10 12:00:00"));

    assertTrue(result);
    assertFalse(flight.isOpenForBooking());
    assertEquals(ReservationStatus.CANCELED, flight.getReservations().get(0).getStatus());
    assertEquals(ReservationStatus.CANCELED, flight.getReservations().get(1).getStatus());
    assertEquals(ReservationStatus.CANCELED, flight.getReservations().get(2).getStatus());
  }

  @Test
  public void testCase3FlightAlreadyClosed() throws Exception {
    Airline airline = factory.createAirline();
    Flight flight = createFlight("F202", "AP14", "CityN", "AP15", "CityO", "2025-08-10 11:00:00",
        "2025-08-10 13:30:00", false);
    airline.addFlight(flight);

    boolean result = airline.closeFlight("F202", date("2025-07-01 09:00:00"));

    assertFalse(result);
    assertFalse(flight.isOpenForBooking());
  }

  @Test
  public void testCase4CloseOnDepartureDayAfterDepartureTime() throws Exception {
    Airline airline = factory.createAirline();
    Flight flight = createFlight("F203", "AP16", "CityP", "AP17", "CityQ", "2025-09-10 09:00:00",
        "2025-09-10 15:30:00", true);
    airline.addFlight(flight);
    createReservation(flight, "R203-1", "P4", ReservationStatus.CONFIRMED);
    createReservation(flight, "R203-2", "P5", ReservationStatus.CONFIRMED);

    boolean result = airline.closeFlight("F203", date("2025-09-10 09:10:00"));

    assertFalse(result);
    assertTrue(flight.isOpenForBooking());
    assertEquals(ReservationStatus.CONFIRMED, flight.getReservations().get(0).getStatus());
    assertEquals(ReservationStatus.CONFIRMED, flight.getReservations().get(1).getStatus());
  }

  @Test
  public void testCase5AttemptToCloseAfterDeparture() throws Exception {
    Airline airline = factory.createAirline();
    Flight flight = createFlight("F204", "AP18", "CityR", "AP19", "CityS", "2025-04-01 22:00:00",
        "2025-04-02 01:30:00", true);
    airline.addFlight(flight);

    boolean result = airline.closeFlight("F204", date("2025-04-01 22:05:00"));

    assertFalse(result);
    assertTrue(flight.isOpenForBooking());
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

  private Reservation createReservation(Flight flight, String reservationId, String passengerName,
      ReservationStatus status) {
    Passenger passenger = factory.createPassenger();
    passenger.setName(passengerName);

    Reservation reservation = factory.createReservation();
    reservation.setId(reservationId);
    reservation.setPassenger(passenger);
    reservation.setFlight(flight);
    reservation.setStatus(status);
    flight.getReservations().add(reservation);
    return reservation;
  }

  private Date date(String value) throws ParseException {
    return format.parse(value);
  }
}
