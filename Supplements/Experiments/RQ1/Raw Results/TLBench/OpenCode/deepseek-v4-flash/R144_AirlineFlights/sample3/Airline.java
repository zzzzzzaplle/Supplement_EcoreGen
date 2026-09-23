import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class Airline {
    private List<Flight> flights;

    public Airline() {
        this.flights = new ArrayList<Flight>();
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void addFlight(Flight f) {
        flights.add(f);
    }

    public void removeFlight(Flight f) {
        flights.remove(f);
    }

    public boolean publishFlight(Flight f, Date now) {
        if (f.getDepartureTime() == null || f.getArrivalTime() == null) {
            return false;
        }
        if (!now.before(f.getDepartureTime())) {
            return false;
        }
        if (!f.getDepartureTime().before(f.getArrivalTime())) {
            return false;
        }
        if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) {
            return false;
        }
        if (f.getDepartureAirport().getId().equals(f.getArrivalAirport().getId())) {
            return false;
        }
        if (f.isOpenForBooking()) {
            return false;
        }
        f.setOpenForBooking(true);
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        for (Flight f : flights) {
            if (f.getId().equals(flightId)) {
                if (!f.isOpenForBooking()) {
                    return false;
                }
                if (f.getDepartureTime().before(now)) {
                    return false;
                }
                f.setOpenForBooking(false);
                for (Reservation r : f.getReservations()) {
                    if (r.getStatus() == ReservationStatus.CONFIRMED) {
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
            if (!f.isOpenForBooking()) {
                continue;
            }
            if (f.getDepartureAirport() != null && f.getDepartureAirport().getId().equals(origin)
                    && f.getArrivalAirport() != null && f.getArrivalAirport().getId().equals(dest)) {
                if (date != null) {
                    if (f.getDepartureTime().equals(date)) {
                        result.add(f);
                    }
                } else {
                    result.add(f);
                }
            }
        }
        return result;
    }
}
