import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        flights.add(f);
    }

    public void removeFlight(Flight f) {
        flights.remove(f);
    }

    public boolean publishFlight(Flight f, Date now) {
        if (f == null || !flights.contains(f)) {
            return false;
        }
        if (f.isOpenForBooking()) {
            return false;
        }
        Date depTime = f.getDepartureTime();
        Date arrTime = f.getArrivalTime();
        if (depTime == null || arrTime == null) {
            return false;
        }
        if (now.after(depTime) || depTime.after(arrTime)) {
            return false;
        }
        Airport depAirport = f.getDepartureAirport();
        Airport arrAirport = f.getArrivalAirport();
        if (depAirport == null || arrAirport == null) {
            return false;
        }
        if (depAirport.equals(arrAirport)) {
            return false;
        }
        f.setOpenForBooking(true);
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        for (Flight f : flights) {
            if (flightId.equals(f.getId()) && f.isOpenForBooking()) {
                f.setOpenForBooking(false);
                for (Reservation r : f.getReservations()) {
                    if (r.getStatus() == ReservationStatus.CONFIRMED) {
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
        for (Flight f : flights) {
            Airport dep = f.getDepartureAirport();
            Airport arr = f.getArrivalAirport();
            if (dep != null && arr != null) {
                boolean originMatch = false;
                boolean destMatch = false;
                for (City c : dep.getCities()) {
                    if (c != null && c.getName() != null && c.getName().equals(origin)) {
                        originMatch = true;
                        break;
                    }
                }
                for (City c : arr.getCities()) {
                    if (c != null && c.getName() != null && c.getName().equals(dest)) {
                        destMatch = true;
                        break;
                    }
                }
                if (originMatch && destMatch) {
                    result.add(f);
                }
            }
        }
        return result;
    }
}
