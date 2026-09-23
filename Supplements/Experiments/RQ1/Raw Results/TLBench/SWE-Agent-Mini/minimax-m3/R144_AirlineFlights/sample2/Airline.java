import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Iterator;

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
        if (f != null && !flights.contains(f)) {
            flights.add(f);
        }
    }

    public void removeFlight(Flight f) {
        if (f != null) {
            flights.remove(f);
        }
    }

    public boolean publishFlight(Flight f, Date now) {
        if (f == null || now == null) {
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
        if (f.getDepartureAirport().getId().equals(f.getArrivalAirport().getId())) {
            return false;
        }
        if (!now.before(f.getDepartureTime())) {
            return false;
        }
        if (!f.getDepartureTime().before(f.getArrivalTime())) {
            return false;
        }
        f.setOpenForBooking(true);
        if (!flights.contains(f)) {
            flights.add(f);
        }
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        if (flightId == null) {
            return false;
        }
        for (Flight f : flights) {
            if (f != null && flightId.equals(f.getId())) {
                if (!f.isOpenForBooking()) {
                    return false;
                }
                f.setOpenForBooking(false);
                List<Reservation> reservations = f.getReservations();
                if (reservations != null) {
                    for (Reservation r : reservations) {
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
        List<Flight> result = new ArrayList<Flight>();
        for (Flight f : flights) {
            if (f == null || !f.isOpenForBooking()) {
                continue;
            }
            if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) {
                continue;
            }
            if (origin != null && !origin.equals(f.getDepartureAirport().getId())) {
                continue;
            }
            if (dest != null && !dest.equals(f.getArrivalAirport().getId())) {
                continue;
            }
            if (date != null) {
                if (f.getDepartureTime() == null) {
                    continue;
                }
                // Compare year, month, day
                java.util.Calendar c1 = java.util.Calendar.getInstance();
                java.util.Calendar c2 = java.util.Calendar.getInstance();
                c1.setTime(date);
                c2.setTime(f.getDepartureTime());
                if (c1.get(java.util.Calendar.YEAR) != c2.get(java.util.Calendar.YEAR)
                        || c1.get(java.util.Calendar.MONTH) != c2.get(java.util.Calendar.MONTH)
                        || c1.get(java.util.Calendar.DAY_OF_MONTH) != c2.get(java.util.Calendar.DAY_OF_MONTH)) {
                    continue;
                }
            }
            result.add(f);
        }
        return result;
    }
}
