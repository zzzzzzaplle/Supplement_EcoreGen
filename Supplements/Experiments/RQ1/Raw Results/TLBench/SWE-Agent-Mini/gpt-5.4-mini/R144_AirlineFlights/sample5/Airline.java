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
        if (f != null) {
            flights.add(f);
        }
    }

    public void removeFlight(Flight f) {
        flights.remove(f);
    }

    public boolean publishFlight(Flight f, Date now) {
        if (f == null || now == null || f.isOpenForBooking() || f.getDepartureTime() == null || f.getArrivalTime() == null || f.getDepartureAirport() == null || f.getArrivalAirport() == null) {
            return false;
        }
        if (!(now.before(f.getDepartureTime()) && f.getDepartureTime().before(f.getArrivalTime()))) {
            return false;
        }
        if (f.getDepartureAirport().equals(f.getArrivalAirport())) {
            return false;
        }
        f.setOpenForBooking(true);
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        if (flightId == null || now == null) {
            return false;
        }
        for (Flight f : flights) {
            if (f != null && flightId.equals(f.getId()) && f.isOpenForBooking() && now.before(f.getDepartureTime())) {
                f.setOpenForBooking(false);
                for (Reservation r : f.getReservations()) {
                    if (r != null && r.getStatus() == ReservationStatus.CONFIRMED) {
                        r.setStatus(ReservationStatus.CANCELED);
                    }
                }
                return true;
            }
        }
        return false;
    }

    public List<Flight> searchFlights(String origin, String dest, Date date) {
        List<Flight> result = new ArrayList<Flight>();
        for (Flight f : flights) {
            if (f != null && f.getDepartureAirport() != null && f.getArrivalAirport() != null && f.getDepartureAirport().getId() != null && f.getArrivalAirport().getId() != null) {
                if (f.getDepartureAirport().getId().equals(origin) && f.getArrivalAirport().getId().equals(dest)) {
                    result.add(f);
                }
            }
        }
        return result;
    }
}
