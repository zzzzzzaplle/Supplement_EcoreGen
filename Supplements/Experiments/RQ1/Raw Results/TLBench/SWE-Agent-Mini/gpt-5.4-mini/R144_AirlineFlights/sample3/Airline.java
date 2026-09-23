import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Airline {
    private List<Flight> flights;

    public Airline() {
        this.flights = new ArrayList<>();
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    public void addFlight(Flight f) {
        if (flights == null) {
            flights = new ArrayList<>();
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
        if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) {
            return false;
        }
        if (f.getDepartureAirport().equals(f.getArrivalAirport())) {
            return false;
        }
        if (f.getDepartureTime() == null || f.getArrivalTime() == null) {
            return false;
        }
        if (!(now.before(f.getDepartureTime()) && f.getDepartureTime().before(f.getArrivalTime()))) {
            return false;
        }
        f.setOpenForBooking(true);
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        if (flightId == null || now == null || flights == null) {
            return false;
        }
        for (Flight f : flights) {
            if (f != null && flightId.equals(f.getId()) && f.isOpenForBooking() && now.before(f.getDepartureTime())) {
                f.setOpenForBooking(false);
                if (f.getReservations() != null) {
                    for (Reservation r : f.getReservations()) {
                        if (r != null && r.getStatus() == ReservationStatus.CONFIRMED) {
                            r.setStatus(ReservationStatus.CANCELED);
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    public List<Flight> searchFlights(String origin, String dest, Date date) {
        List<Flight> result = new ArrayList<>();
        if (flights == null) {
            return result;
        }
        for (Flight f : flights) {
            if (f != null) {
                result.add(f);
            }
        }
        return result;
    }
}
