import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;

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
        if (!f.isOpenForBooking() && f.getDepartureAirport() != null && f.getArrivalAirport() != null) {
            if (!f.getDepartureAirport().equals(f.getArrivalAirport())) {
                if (now.before(f.getDepartureTime()) && f.getDepartureTime().before(f.getArrivalTime())) {
                    f.setOpenForBooking(true);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean closeFlight(String flightId, Date now) {
        Flight target = null;
        for (Flight f : flights) {
            if (f.getId().equals(flightId)) {
                target = f;
                break;
            }
        }
        if (target == null || !target.isOpenForBooking() || !now.before(target.getDepartureTime())) {
            return false;
        }
        target.setOpenForBooking(false);
        for (Reservation r : target.getReservations()) {
            if (r.getStatus() == ReservationStatus.CONFIRMED) {
                r.setStatus(ReservationStatus.CANCELED);
            }
        }
        return true;
    }

    public List<Flight> searchFlights(String origin, String dest, Date date) {
        List<Flight> result = new ArrayList<>();
        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(date);
        for (Flight f : flights) {
            String originCity = "";
            String destCity = "";
            Airport depAirport = f.getDepartureAirport();
            if (depAirport != null) {
                originCity = depAirport.getCities().get(0).getName();
            }
            Airport arrAirport = f.getArrivalAirport();
            if (arrAirport != null) {
                destCity = arrAirport.getCities().get(0).getName();
            }
            String flightDateStr = new SimpleDateFormat("yyyy-MM-dd").format(f.getDepartureTime());
            if (originCity.equals(origin) && destCity.equals(dest) && flightDateStr.equals(dateStr)) {
                result.add(f);
            }
        }
        return result;
    }
}
