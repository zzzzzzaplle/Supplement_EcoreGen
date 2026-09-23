import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

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
        if (flights == null) {
            flights = new ArrayList<Flight>();
        }
        flights.add(f);
    }

    public void removeFlight(Flight f) {
        if (flights != null) {
            flights.remove(f);
        }
    }

    public boolean publishFlight(Flight f, Date now) {
        if (f == null || now == null) return false;
        if (f.isOpenForBooking()) return false;
        if (f.getDepartureTime() == null || f.getArrivalTime() == null) return false;
        if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) return false;
        if (f.getDepartureAirport().getId() == null || f.getArrivalAirport().getId() == null) return false;
        if (f.getDepartureAirport().getId().equals(f.getArrivalAirport().getId())) return false;
        if (!now.before(f.getDepartureTime())) return false;
        if (!f.getDepartureTime().before(f.getArrivalTime())) return false;

        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            sdf.format(f.getDepartureTime());
            sdf.format(f.getArrivalTime());
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
                if (f.getDepartureTime() == null) return false;
                if (!now.before(f.getDepartureTime())) return false;
                f.setOpenForBooking(false);
                if (f.getReservations() != null) {
                    for (Reservation r : f.getReservations()) {
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
        List<Flight> result = new ArrayList<Flight>();
        if (flights == null) return result;
        for (Flight f : flights) {
            if (!f.isOpenForBooking()) continue;
            if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) continue;
            if (f.getDepartureAirport().getId() == null || f.getArrivalAirport().getId() == null) continue;
            boolean originMatch = (origin == null) || f.getDepartureAirport().getId().equals(origin);
            boolean destMatch = (dest == null) || f.getArrivalAirport().getId().equals(dest);
            boolean dateMatch = true;
            if (date != null && f.getDepartureTime() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                dateMatch = sdf.format(f.getDepartureTime()).equals(sdf.format(date));
            }
            if (originMatch && destMatch && dateMatch) {
                result.add(f);
            }
        }
        return result;
    }
}
