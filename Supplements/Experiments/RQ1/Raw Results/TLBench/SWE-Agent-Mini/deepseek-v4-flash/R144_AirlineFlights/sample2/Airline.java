import java.util.List;
import java.util.ArrayList;
import java.util.Date;

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
        if (f == null || now == null) return false;
        if (f.isOpenForBooking()) return false;
        if (f.getDepartureTime() == null || f.getArrivalTime() == null) return false;
        if (!now.before(f.getDepartureTime())) return false;
        if (!f.getDepartureTime().before(f.getArrivalTime())) return false;
        if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) return false;
        if (f.getDepartureAirport().getId().equals(f.getArrivalAirport().getId())) return false;

        f.setOpenForBooking(true);
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        if (flightId == null || now == null) return false;
        for (Flight f : flights) {
            if (f.getId() != null && f.getId().equals(flightId)) {
                if (!f.isOpenForBooking()) return false;
                if (!now.before(f.getDepartureTime())) return false;
                f.setOpenForBooking(false);
                List<Reservation> reservations = f.getReservations();
                if (reservations != null) {
                    List<Reservation> toCancel = new ArrayList<>(reservations);
                    for (Reservation r : toCancel) {
                        if (r.getStatus() == ReservationStatus.CONFIRMED) {
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
        if (origin == null || dest == null || date == null) return result;
        for (Flight f : flights) {
            if (f.isOpenForBooking() && f.getDepartureAirport() != null && f.getArrivalAirport() != null) {
                boolean originMatch = f.getDepartureAirport().getId().equals(origin);
                boolean destMatch = f.getArrivalAirport().getId().equals(dest);
                boolean dateMatch = f.getDepartureTime() != null && 
                    (Math.abs(f.getDepartureTime().getTime() - date.getTime()) < 86400000L);
                if (originMatch && destMatch && dateMatch) {
                    result.add(f);
                }
            }
        }
        return result;
    }
}
