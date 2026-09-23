import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
        if (this.flights == null) {
            this.flights = new ArrayList<Flight>();
        }
        if (f != null && !this.flights.contains(f)) {
            this.flights.add(f);
        }
    }

    public void removeFlight(Flight f) {
        if (this.flights != null) {
            this.flights.remove(f);
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
        if (f.getDepartureAirport().getId() == null || f.getArrivalAirport().getId() == null) {
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
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        if (flightId == null || now == null) {
            return false;
        }
        Flight target = null;
        for (Flight f : this.flights) {
            if (f != null && flightId.equals(f.getId())) {
                target = f;
                break;
            }
        }
        if (target == null) {
            return false;
        }
        if (!target.isOpenForBooking()) {
            return false;
        }
        if (target.getDepartureTime() != null && !now.before(target.getDepartureTime())) {
            return false;
        }
        target.setOpenForBooking(false);
        List<Reservation> reservations = target.getReservations();
        if (reservations != null) {
            for (Reservation r : reservations) {
                if (r != null && r.getStatus() == ReservationStatus.CONFIRMED) {
                    r.setStatus(ReservationStatus.CANCELED);
                }
            }
        }
        return true;
    }

    public List<Flight> searchFlights(String origin, String dest, Date date) {
        List<Flight> result = new ArrayList<Flight>();
        if (this.flights == null) {
            return result;
        }
        for (Flight f : this.flights) {
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
            if (date != null && f.getDepartureTime() != null) {
                java.util.Calendar c1 = java.util.Calendar.getInstance();
                c1.setTime(date);
                java.util.Calendar c2 = java.util.Calendar.getInstance();
                c2.setTime(f.getDepartureTime());
                if (c1.get(java.util.Calendar.YEAR) != c2.get(java.util.Calendar.YEAR)
                        || c1.get(java.util.Calendar.DAY_OF_YEAR) != c2.get(java.util.Calendar.DAY_OF_YEAR)) {
                    continue;
                }
            }
            result.add(f);
        }
        return result;
    }
}
