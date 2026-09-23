import java.text.SimpleDateFormat;
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
        if (f == null || now == null || f.isOpenForBooking()) {
            return false;
        }
        if (!isValidDateTime(f.getDepartureTime()) || !isValidDateTime(f.getArrivalTime())) {
            return false;
        }
        if (!now.before(f.getDepartureTime()) || !f.getDepartureTime().before(f.getArrivalTime())) {
            return false;
        }
        if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) {
            return false;
        }
        if (sameAirport(f.getDepartureAirport(), f.getArrivalAirport())) {
            return false;
        }
        f.setOpenForBooking(true);
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        if (flightId == null || now == null) {
            return false;
        }
        for (Flight f : flights) {
            if (f != null && flightId.equals(f.getId()) && f.isOpenForBooking() && f.getDepartureTime() != null && now.before(f.getDepartureTime())) {
                f.setOpenForBooking(false);
                for (Reservation r : f.getReservations()) {
                    if (r != null && r.getStatus() == ReservationStatus.CONFIRMED) {
                        r.setStatus(ReservationStatus.CANCELED);
                    }
                }
                return true;
            }
        }
        return false;
    }

    public List<Flight> searchFlights(String origin, String dest, Date date) {
        List<Flight> result = new ArrayList<Flight>();
        if (origin == null || dest == null || date == null) {
            return result;
        }
        for (Flight f : flights) {
            if (f != null && f.getDepartureAirport() != null && f.getArrivalAirport() != null && origin.equals(f.getDepartureAirport().getId()) && dest.equals(f.getArrivalAirport().getId())) {
                result.add(f);
            }
        }
        return result;
    }

    private boolean isValidDateTime(Date date) {
        if (date == null) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setLenient(false);
        try {
            sdf.parse(sdf.format(date));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean sameAirport(Airport a, Airport b) {
        if (a == null || b == null) {
            return false;
        }
        if (a.getId() == null) {
            return b.getId() == null;
        }
        return a.getId().equals(b.getId());
    }
}
