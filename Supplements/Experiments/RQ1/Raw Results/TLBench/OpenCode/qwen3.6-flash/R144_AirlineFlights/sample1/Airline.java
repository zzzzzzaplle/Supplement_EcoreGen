import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Airline {

    private List<Flight> flights = new ArrayList<>();

    public Airline() {}

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    public void addFlight(Flight f) {
        if (f != null && !this.flights.contains(f)) {
            this.flights.add(f);
        }
    }

    public void removeFlight(Flight f) {
        if (f != null) {
            this.flights.remove(f);
        }
    }

    public boolean publishFlight(Flight f, Timestamp now) {
        if (f == null) {
            return false;
        }
        if (f.isOpenForBooking()) {
            return false;
        }
        if (f.getDepartureTime() == null || f.getArrivalTime() == null) {
            return false;
        }
        if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) {
            return false;
        }
        if (f.getDepartureAirport().equals(f.getArrivalAirport())) {
            return false;
        }
        if (now != null && f.getDepartureTime() != null) {
            if (!now.before(f.getDepartureTime())) {
                return false;
            }
        }
        if (f.getDepartureTime() != null && f.getArrivalTime() != null) {
            if (!f.getDepartureTime().before(f.getArrivalTime())) {
                return false;
            }
        }
        f.setOpenForBooking(true);
        return true;
    }

    public boolean closeFlight(String flightId, Timestamp now) {
        if (flightId == null || flightId.trim().isEmpty()) {
            return false;
        }
        for (Flight f : this.flights) {
            if (f != null && flightId.equals(f.getId())) {
                if (!f.isOpenForBooking()) {
                    return false;
                }
                if (now != null && f.getDepartureTime() != null) {
                    if (now.after(f.getDepartureTime())) {
                        return false;
                    }
                }
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

    public List<Flight> searchFlights(String origin, String dest, Timestamp date) {
        List<Flight> found = new ArrayList<>();
        for (Flight f : this.flights) {
            if (f == null || !f.isOpenForBooking()) {
                continue;
            }
            if (f.getDepartureAirport() != null && f.getArrivalAirport() != null) {
                if (origin != null) {
                    boolean originMatch = false;
                    for (City city : f.getDepartureAirport().getCities()) {
                        if (city != null && city.getName() != null && city.getName().equals(origin)) {
                            originMatch = true;
                            break;
                        }
                    }
                    if (!originMatch) {
                        continue;
                    }
                }
                boolean destMatch = false;
                for (City city : f.getArrivalAirport().getCities()) {
                    if (city != null && city.getName() != null && city.getName().equals(dest)) {
                        destMatch = true;
                        break;
                    }
                }
                if (!destMatch) {
                    continue;
                }
                if (date != null && f.getDepartureTime() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        String flightDateStr = sdf.format(f.getDepartureTime()).replace("-", "/");
                        String searchDateStr = sdf.format(date).replace("-", "/");
                        java.util.Date flightDate = sdf.parse(flightDateStr.replace("/", "-"));
                        java.util.Date searchDate = sdf.parse(searchDateStr.replace("/", "-"));
                        if (flightDate != null && searchDate != null && flightDate.equals(searchDate)) {
                            found.add(f);
                        }
                    } catch (ParseException e) {
                        if (f.getDepartureTime().compareTo(date) == 0) {
                            found.add(f);
                        } else {
                            continue;
                        }
                    }
                } else {
                    found.add(f);
                }
            }
        }
        return found;
    }
}
