import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Airline {
    private List<Flight> flights;

    public Airline() {
        this.flights = new ArrayList<Flight>();
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    public void addFlight(Flight f) {
        if (flights == null) {
            flights = new ArrayList<Flight>();
        }
        flights.add(f);
    }

    public void removeFlight(Flight f) {
        if (flights != null) {
            flights.remove(f);
        }
    }

    public boolean publishFlight(Flight f, Date now) {
        if (f == null || now == null || f.isOpenForBooking()) {
            return false;
        }
        if (f.getDepartureTime() == null || f.getArrivalTime() == null || f.getDepartureAirport() == null || f.getArrivalAirport() == null) {
            return false;
        }
        if (!DateUtil.isValidDateTimeFormat(DateUtil.formatDateTime(f.getDepartureTime())) || !DateUtil.isValidDateTimeFormat(DateUtil.formatDateTime(f.getArrivalTime()))) {
            return false;
        }
        if (!(now.before(f.getDepartureTime()) && f.getDepartureTime().before(f.getArrivalTime()))) {
            return false;
        }
        if (f.getDepartureAirport().equals(f.getArrivalAirport())) {
            return false;
        }
        f.setOpenForBooking(true);
        if (flights == null) {
            flights = new ArrayList<Flight>();
        }
        if (!flights.contains(f)) {
            flights.add(f);
        }
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        if (flightId == null || now == null || flights == null) {
            return false;
        }
        for (Flight flight : flights) {
            if (flight != null && flightId.equals(flight.getId())) {
                if (!flight.isOpenForBooking() || flight.getDepartureTime() == null || !now.before(flight.getDepartureTime())) {
                    return false;
                }
                flight.setOpenForBooking(false);
                for (Reservation reservation : flight.getReservations()) {
                    if (reservation != null && reservation.getStatus() == ReservationStatus.CONFIRMED) {
                        reservation.setStatus(ReservationStatus.CANCELED);
                    }
                }
                return true;
            }
        }
        return false;
    }

    public List<Flight> searchFlights(String origin, String dest, Date date) {
        return new ArrayList<Flight>();
    }
}
