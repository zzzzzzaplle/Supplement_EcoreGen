import java.util.ArrayList;
import java.util.List;
import java.util.Date;

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
        if (f == null || flights == null || !flights.contains(f)) {
            return false;
        }
        if (f.isOpenForBooking()) {
            return false;
        }
        Date departureTime = f.getDepartureTime();
        Date arrivalTime = f.getArrivalTime();
        if (departureTime == null || arrivalTime == null) {
            return false;
        }
        if (now == null) {
            return false;
        }
        if (!now.before(departureTime)) {
            return false;
        }
        if (!departureTime.before(arrivalTime)) {
            return false;
        }
        if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) {
            return false;
        }
        if (f.getDepartureAirport().equals(f.getArrivalAirport())) {
            return false;
        }
        f.setOpenForBooking(true);
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        if (flightId == null || flights == null) {
            return false;
        }
        for (Flight f : flights) {
            if (f != null && flightId.equals(f.getId())) {
                if (!f.isOpenForBooking()) {
                    return false;
                }
                Date departureTime = f.getDepartureTime();
                if (departureTime != null && now != null && !now.before(departureTime)) {
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
        if (flights == null) {
            return result;
        }
        for (Flight f : flights) {
            if (f == null) continue;
            if (!f.isOpenForBooking()) continue;
            Airport dep = f.getDepartureAirport();
            Airport arr = f.getArrivalAirport();
            if (dep == null || arr == null) continue;
            if (origin != null && !origin.equals(dep.getId())) continue;
            if (dest != null && !dest.equals(arr.getId())) continue;
            if (date != null && f.getDepartureTime() != null) {
                if (!isSameDay(date, f.getDepartureTime())) continue;
            }
            result.add(f);
        }
        return result;
    }

    private boolean isSameDay(Date d1, Date d2) {
        if (d1 == null || d2 == null) return false;
        java.util.Calendar c1 = java.util.Calendar.getInstance();
        java.util.Calendar c2 = java.util.Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        return c1.get(java.util.Calendar.YEAR) == c2.get(java.util.Calendar.YEAR)
                && c1.get(java.util.Calendar.DAY_OF_YEAR) == c2.get(java.util.Calendar.DAY_OF_YEAR);
    }
}
