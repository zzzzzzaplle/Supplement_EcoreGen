import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

public class Airline {
    private List<Flight> flights;

    public Airline() {
        this.flights = new ArrayList<Flight>();
    }

    public List<Flight> getFlights() {
        if (this.flights == null) {
            this.flights = new ArrayList<Flight>();
        }
        return this.flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    public void addFlight(Flight f) {
        if (this.flights == null) {
            this.flights = new ArrayList<Flight>();
        }
        if (f != null && !this.flights.contains(f)) {
            this.flights.add(f);
        }
    }

    public void removeFlight(Flight f) {
        if (this.flights != null && f != null) {
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            sdf.format(f.getDepartureTime());
            sdf.format(f.getArrivalTime());
        } catch (Exception e) {
            return false;
        }
        if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) {
            return false;
        }
        String depId = f.getDepartureAirport().getId();
        String arrId = f.getArrivalAirport().getId();
        if (depId == null || arrId == null) {
            return false;
        }
        if (depId.equals(arrId)) {
            return false;
        }
        if (now == null) {
            return false;
        }
        if (!now.before(f.getDepartureTime())) {
            return false;
        }
        if (!f.getDepartureTime().before(f.getArrivalTime())) {
            return false;
        }
        f.setOpenForBooking(true);
        if (!this.getFlights().contains(f)) {
            this.addFlight(f);
        }
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        if (flightId == null) {
            return false;
        }
        Flight target = null;
        for (Flight f : this.getFlights()) {
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
        if (now != null && target.getDepartureTime() != null
                && !now.before(target.getDepartureTime())) {
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
        List<Flight> results = new ArrayList<Flight>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Flight f : this.getFlights()) {
            if (f == null) {
                continue;
            }
            if (origin != null) {
                if (f.getDepartureAirport() == null
                        || !origin.equals(f.getDepartureAirport().getId())) {
                    continue;
                }
            }
            if (dest != null) {
                if (f.getArrivalAirport() == null
                        || !dest.equals(f.getArrivalAirport().getId())) {
                    continue;
                }
            }
            if (date != null) {
                if (f.getDepartureTime() == null) {
                    continue;
                }
                String flightDate = sdf.format(f.getDepartureTime());
                String searchDate = sdf.format(date);
                if (!flightDate.equals(searchDate)) {
                    continue;
                }
            }
            results.add(f);
        }
        return results;
    }
}
