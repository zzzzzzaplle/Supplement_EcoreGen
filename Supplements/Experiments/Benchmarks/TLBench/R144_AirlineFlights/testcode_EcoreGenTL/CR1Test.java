package edu.flights;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class CR1Test {

  private FlightsFactory factory;
  private SimpleDateFormat format;

  @Before
  public void setUp() {
    factory = FlightsFactory.eINSTANCE;
    format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    format.setLenient(false);
  }

  @Test
  public void testCase1CorrectScheduleAndRoute() throws Exception {
    Airline airline = factory.createAirline();
    Airport ap01 = createAirport("AP01", "CityA");
    Airport ap02 = createAirport("AP02", "CityB");
    Flight flight = createFlight("F100", ap01, ap02, "2025-01-10 10:00:00", "2025-01-10 14:00:00", false);

    boolean result = airline.publishFlight(flight, date("2024-12-01 09:00:00"));

    assertTrue(result);
    assertTrue(flight.isOpenForBooking());
  }

  @Test
  public void testCase2DepartureAfterArrival() throws Exception {
    Airline airline = factory.createAirline();
    Airport ap03 = createAirport("AP03", "CityC");
    Airport ap04 = createAirport("AP04", "CityD");
    Flight flight = createFlight("F101", ap03, ap04, "2025-02-05 20:00:00", "2025-02-05 18:00:00", false);

    boolean result = airline.publishFlight(flight, date("2024-12-15 10:00:00"));

    assertFalse(result);
    assertFalse(flight.isOpenForBooking());
  }

  @Test
  public void testCase3SameDepartureAndArrivalAirport() throws Exception {
    Airline airline = factory.createAirline();
    Airport ap05 = createAirport("AP05", "CityE");
    Flight flight = createFlight("F102", ap05, ap05, "2025-03-01 08:00:00", "2025-03-01 12:00:00", false);

    boolean result = airline.publishFlight(flight, date("2025-02-01 09:00:00"));

    assertFalse(result);
    assertFalse(flight.isOpenForBooking());
  }

  @Test
  public void testCase4DepartureTimeInThePast() throws Exception {
    Airline airline = factory.createAirline();
    Airport ap06 = createAirport("AP06", "CityF");
    Airport ap07 = createAirport("AP07", "CityG");
    Flight flight = createFlight("F103", ap06, ap07, "2025-03-30 10:00:00", "2025-03-30 12:00:00", false);

    boolean result = airline.publishFlight(flight, date("2025-04-01 09:00:00"));

    assertFalse(result);
    assertFalse(flight.isOpenForBooking());
  }

  @Test
  public void testCase5SecondPublishAttempt() throws Exception {
    Airline airline = factory.createAirline();
    Airport ap08 = createAirport("AP08", "CityH");
    Airport ap09 = createAirport("AP09", "CityI");
    Flight flight = createFlight("F104", ap08, ap09, "2025-05-05 07:00:00", "2025-05-05 10:00:00", false);

    boolean firstPublish = airline.publishFlight(flight, date("2025-04-01 10:00:00"));
    boolean secondPublish = airline.publishFlight(flight, date("2025-04-01 10:00:00"));

    assertTrue(firstPublish);
    assertFalse(secondPublish);
    assertTrue(flight.isOpenForBooking());
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

  private Date date(String value) throws ParseException {
    return format.parse(value);
  }
}
