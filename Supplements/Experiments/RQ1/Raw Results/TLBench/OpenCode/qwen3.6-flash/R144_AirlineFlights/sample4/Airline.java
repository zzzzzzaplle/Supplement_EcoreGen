import java.util.*;
import java.text.SimpleDateFormat;

public class Airline {
    private List<Flight> flights;

    public Airline() {
        this.flights = new ArrayList<>();
    }

    public List<Flight> getFlights() { return flights; }
    public void setFlights(List<Flight> flights) { this.flights = flights; }

    public void addFlight(Flight f) {
        this.flights.add(f);
    }

    public void removeFlight(Flight f) {
        this.flights.remove(f);
    }

    public boolean publishFlight(Flight f, Date now) {
        if (f == null) return false;
        if (f.isOpenForBooking()) return false;
        if (f.getDepartureTime() == null || f.getArrivalTime() == null) return false;
        if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) return false;
        if (f.getDepartureAirport().getId() != null && f.getArrivalAirport().getId() != null
                && f.getDepartureAirport().getId().equals(f.getArrivalAirport().getId())) {
            return false;
        }
        if (now.after(f.getDepartureTime()) || f.getDepartureTime().after(f.getArrivalTime())) {
            return false;
        }
        f.setOpenForBooking(true);
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        if (flightId == null || flights == null) return false;
        for (Flight f : flights) {
            if (f.getId() != null && f.getId().equals(flightId)) {
                if (!f.isOpenForBooking()) return false;
                if (now.after(f.getDepartureTime())) return false;
                for (Reservation r : f.getReservations()) {
                    if (r.getStatus() == ReservationStatus.CONFIRMED) {
                        r.setStatus(ReservationStatus.CANCELED);
                    }
                }
                f.setOpenForBooking(false);
                return true;
            }
        }
        return false;
    }

    public List<Flight> searchFlights(String origin, String dest, Date date) {
        List<Flight> result = new ArrayList<>();
        if (flights == null || origin == null || dest == null || date == null) return result;
        for (Flight f : flights) {
            if (f.getDepartureAirport() != null && f.getArrivalAirport() != null) {
                String originCity = origin.toLowerCase();
                String destCity = dest.toLowerCase();
                boolean originMatch = false;
                boolean destMatch = false;
                for (City c : f.getDepartureAirport().getCities()) {
                    if (c.getName() == null) continue;
                    if (c.getName().toLowerCase().contains(originCity)) {
                        originMatch = true;
                        break;
                    }
                }
                for (City c : f.getArrivalAirport().getCities()) {
                    if (c.getName() == null) continue;
                    if (c.getName().toLowerCase().contains(destCity)) {
                        destMatch = true;
                        break;
                    }
                }
                if (originMatch && destMatch) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    if (!sdf.format(date).equals(sdf.format(f.getDepartureTime()))) {
                        continue;
                    }
                    result.add(f);
                }
            }
        }
        return result;
    }
}
