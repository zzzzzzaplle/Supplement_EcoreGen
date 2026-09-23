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
        if (f == null) {
            return;
        }
        if (this.flights == null) {
            this.flights = new ArrayList<Flight>();
        }
        this.flights.add(f);
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
        if (!now.before(f.getDepartureTime())) {
            return false;
        }
        if (!f.getDepartureTime().before(f.getArrivalTime())) {
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
        if (flightId == null || now == null || this.flights == null) {
            return false;
        }
        for (Flight f : this.flights) {
            if (f != null && flightId.equals(f.getId())) {
                if (!f.isOpenForBooking()) {
                    return false;
                }
                if (f.getDepartureTime() == null || !now.before(f.getDepartureTime())) {
                    return false;
                }
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
        List<Flight> result = new ArrayList<Flight>();
        if (this.flights == null) {
            return result;
        }
        for (Flight f : this.flights) {
            if (f == null) {
                continue;
            }
            boolean ok = true;
            if (origin != null) {
                ok = f.getDepartureAirport() != null && f.getDepartureAirport().getId() != null && origin.equals(f.getDepartureAirport().getId());
            }
            if (ok && dest != null) {
                ok = f.getArrivalAirport() != null && f.getArrivalAirport().getId() != null && dest.equals(f.getArrivalAirport().getId());
            }
            if (ok && date != null) {
                ok = f.getDepartureTime() != null && f.getDepartureTime().equals(date);
            }
            if (ok) {
                result.add(f);
            }
        }
        return result;
    }
}
