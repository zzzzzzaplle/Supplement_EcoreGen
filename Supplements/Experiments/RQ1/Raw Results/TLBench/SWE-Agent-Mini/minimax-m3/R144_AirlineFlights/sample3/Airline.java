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
        if (!this.flights.contains(f)) {
            this.flights.add(f);
        }
    }

    public void removeFlight(Flight f) {
        if (this.flights != null) {
            this.flights.remove(f);
        }
    }

    public boolean publishFlight(Flight f, Date now) {
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
        if (flightId == null || this.flights == null) {
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
                // simple date match: same year, month, day
                java.util.Calendar cd = java.util.Calendar.getInstance();
                java.util.Calendar cf = java.util.Calendar.getInstance();
                cd.setTime(date);
                cf.setTime(f.getDepartureTime());
                if (cd.get(java.util.Calendar.YEAR) != cf.get(java.util.Calendar.YEAR)
                        || cd.get(java.util.Calendar.MONTH) != cf.get(java.util.Calendar.MONTH)
                        || cd.get(java.util.Calendar.DAY_OF_MONTH) != cf.get(java.util.Calendar.DAY_OF_MONTH)) {
                    continue;
                }
            }
            result.add(f);
        }
        return result;
    }
}
