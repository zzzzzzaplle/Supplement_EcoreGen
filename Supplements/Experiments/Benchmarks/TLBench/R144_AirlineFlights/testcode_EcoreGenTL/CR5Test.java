package edu.flights;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.emf.common.util.EList;
import org.junit.Before;
import org.junit.Test;

public class CR5Test {

  private FlightsFactory factory;

  @Before
  public void setUp() {
    factory = FlightsFactory.eINSTANCE;
  }

  @Test
  public void testCase1FlightWithThreeConfirmations() {
    Flight flight = createFlight("F501", true);
    createReservation(flight, "R501-1", "P1", ReservationStatus.CONFIRMED);
    createReservation(flight, "R501-2", "P2", ReservationStatus.CONFIRMED);
    createReservation(flight, "R501-3", "P3", ReservationStatus.CONFIRMED);

    EList<Reservation> confirmedReservations = flight.getConfirmedReservations();

    assertEquals(3, confirmedReservations.size());
    assertTrue(containsReservation(confirmedReservations, "R501-1"));
    assertTrue(containsReservation(confirmedReservations, "R501-2"));
    assertTrue(containsReservation(confirmedReservations, "R501-3"));
  }

  @Test
  public void testCase2NoConfirmedReservations() {
    Flight flight = createFlight("F502", true);
    createReservation(flight, "R502-1", "P1", ReservationStatus.PENDING);
    createReservation(flight, "R502-2", "P2", ReservationStatus.PENDING);

    EList<Reservation> confirmedReservations = flight.getConfirmedReservations();

    assertTrue(confirmedReservations.isEmpty());
  }

  @Test
  public void testCase3FlightClosedReturnsZero() {
    Flight flight = createFlight("F503", false);
    createReservation(flight, "R503-1", "P1", ReservationStatus.CONFIRMED);

    EList<Reservation> confirmedReservations = flight.getConfirmedReservations();

    assertTrue(confirmedReservations.isEmpty());
  }

  @Test
  public void testCase4UnknownFlightId() {
    // NLTC Setup: Airline AL24 holds flights F504, F505 only.
    Airline airline = factory.createAirline();
    Flight f504 = createFlight("F504", true);
    Flight f505 = createFlight("F505", true);
    airline.addFlight(f504);
    airline.addFlight(f505);
    createReservation(f504, "R504-X", "P1", ReservationStatus.CONFIRMED);
    createReservation(f505, "R505-X", "P2", ReservationStatus.CONFIRMED);

    // FX999 is not in AL24's flight list — unknown flight id
    Flight fx999 = createFlight("FX999", true);

    EList<Reservation> confirmedReservations = fx999.getConfirmedReservations();

    assertTrue(confirmedReservations.isEmpty());
  }

  @Test
  public void testCase5MixedReservationStatuses() {
    Flight flight = createFlight("F504", true);
    createReservation(flight, "R504-A", "P1", ReservationStatus.CONFIRMED);
    createReservation(flight, "R504-B", "P2", ReservationStatus.CONFIRMED);
    createReservation(flight, "R504-C", "P3", ReservationStatus.CANCELED);
    createReservation(flight, "R504-D", "P4", ReservationStatus.PENDING);

    EList<Reservation> confirmedReservations = flight.getConfirmedReservations();

    assertEquals(2, confirmedReservations.size());
    assertTrue(containsReservation(confirmedReservations, "R504-A"));
    assertTrue(containsReservation(confirmedReservations, "R504-B"));
  }

  private Flight createFlight(String flightId, boolean openForBooking) {
    Flight flight = factory.createFlight();
    flight.setId(flightId);
    flight.setOpenForBooking(openForBooking);
    return flight;
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

  private boolean containsReservation(EList<Reservation> reservations, String reservationId) {
    for (Reservation reservation : reservations) {
      if (reservationId.equals(reservation.getId())) {
        return true;
      }
    }
    return false;
  }
}
