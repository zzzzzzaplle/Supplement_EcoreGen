import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

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
        flights.remove(f);
    }

    public boolean publishFlight(Flight f, Date now) {
        if (f == null || now == null) return false;
        if (f.isOpenForBooking()) return false;
        if (f.getDepartureTime() == null || f.getArrivalTime() == null) return false;
        if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) return false;

        if (f.getDepartureAirport().getId().equals(f.getArrivalAirport().getId())) return false;

        if (!now.before(f.getDepartureTime())) return false;
        if (!f.getDepartureTime().before(f.getArrivalTime())) return false;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String depStr = sdf.format(f.getDepartureTime());
        String arrStr = sdf.format(f.getArrivalTime());

        try {
            sdf.parse(depStr);
            sdf.parse(arrStr);
        } catch (Exception e) {
            return false;
        }

        f.setOpenForBooking(true);
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        if (flightId == null || now == null) return false;
        for (Flight f : flights) {
            if (flightId.equals(f.getId())) {
                if (!f.isOpenForBooking()) return false;
                if (!now.before(f.getDepartureTime())) return false;
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

    public List<Reservation> getConfirmedReservations(Flight f) {
        if (f == null) return new ArrayList<>();
        if (!f.isOpenForBooking()) return new ArrayList<>();
        return f.getConfirmedReservations();
    }

    public List<Flight> searchFlights(String origin, String dest, Date date) {
        List<Flight> result = new ArrayList<>();
        if (origin == null || dest == null || date == null) return result;
        for (Flight f : flights) {
            if (!f.isOpenForBooking()) continue;
            if (f.getDepartureAirport() != null && origin.equals(f.getDepartureAirport().getId()) &&
                f.getArrivalAirport() != null && dest.equals(f.getArrivalAirport().getId())) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String flightDate = sdf.format(f.getDepartureTime());
                String searchDate = sdf.format(date);
                if (flightDate.equals(searchDate)) {
                    result.add(f);
                }
            }
        }
        return result;
    }
}
