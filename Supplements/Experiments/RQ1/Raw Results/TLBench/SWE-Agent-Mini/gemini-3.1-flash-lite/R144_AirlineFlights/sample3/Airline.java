import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Airline {
    private List<Flight> flights = new ArrayList<>();

    public Airline() {}

    public List<Flight> getFlights() { return flights; }
    public void setFlights(List<Flight> flights) { this.flights = flights; }

    public void addFlight(Flight f) { flights.add(f); }
    public void removeFlight(Flight f) { flights.remove(f); }

    public boolean publishFlight(Flight f, Date now) {
        if (f.isOpenForBooking()) return false;
        if (f.getDepartureTime() == null || f.getArrivalTime() == null) return false;
        if (!(now.before(f.getDepartureTime()) && f.getDepartureTime().before(f.getArrivalTime()))) return false;
        if (f.getDepartureAirport() == null || f.getArrivalAirport() == null || f.getDepartureAirport().getId().equals(f.getArrivalAirport().getId())) return false;
        
        f.setOpenForBooking(true);
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        Flight target = null;
        for (Flight f : flights) {
            if (f.getId().equals(flightId)) {
                target = f;
                break;
            }
        }
        if (target == null || !target.isOpenForBooking() || now.after(target.getDepartureTime())) return false;
        
        target.setOpenForBooking(false);
        for (Reservation r : target.getReservations()) {
            if (r.getStatus() == ReservationStatus.CONFIRMED) {
                r.setStatus(ReservationStatus.CANCELED);
            }
        }
        return true;
    }

    public List<Flight> searchFlights(String origin, String dest, Date date) {
        return new ArrayList<Flight>();
    }
}
