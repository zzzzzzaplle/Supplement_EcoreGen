import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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
        if (f != null && flights != null) {
            flights.add(f);
        }
    }

    public void removeFlight(Flight f) {
        if (f != null && flights != null) {
            flights.remove(f);
        }
    }

    public boolean publishFlight(Flight f, Date now) {
        if (f == null || now == null) {
            return false;
        }
        if (f.isOpenForBooking() == true) {
            return false;
        }
        if (f.getDepartureTime() == null || f.getArrivalTime() == null) {
            return false;
        }
        if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) {
            return false;
        }
        if (f.getDepartureAirport().getId() != null && f.getArrivalAirport().getId() != null) {
            if (f.getDepartureAirport().getId().equals(f.getArrivalAirport().getId())) {
                return false;
            }
        }
        if (f.getDepartureTime().before(now) || f.getArrivalTime().before(now)) {
            return false;
        }
        if (f.getDepartureTime().after(f.getArrivalTime())) {
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
            if (f.getId() != null && f.getId().equals(flightId)) {
                if (f.isOpenForBooking() == false) {
                    return false;
                }
                if (f.getDepartureTime().before(now)) {
                    return false;
                }
                f.setOpenForBooking(false);
                for (Reservation r : f.getReservations()) {
                    if (ReservationStatus.CONFIRMED.equals(r.getStatus())) {
                        r.setStatus(ReservationStatus.CANCELED);
                    }
                }
                return true;
            }
        }
        return false;
    }

    public List<Flight> searchFlights(String origin, String dest, Date date) {
        List<Flight> result = new ArrayList<>();
        if (origin == null || dest == null || date == null) {
            return result;
        }
        for (Flight f : flights) {
            if (f.isOpenForBooking() == false) {
                continue;
            }
            boolean matchOrigin = false;
            boolean matchDest = false;
            if (f.getDepartureAirport() != null && f.getDepartureAirport().getCities() != null) {
                for (City c : f.getDepartureAirport().getCities()) {
                    if (origin.equals(c.toString())) {
                        matchOrigin = true;
                        break;
                    }
                }
            }
            if (f.getArrivalAirport() != null && f.getArrivalAirport().getCities() != null) {
                for (City c : f.getArrivalAirport().getCities()) {
                    if (dest.equals(c.toString())) {
                        matchDest = true;
                        break;
                    }
                }
            }
            if (matchOrigin && matchDest) {
                result.add(f);
            }
        }
        return result;
    }
}
