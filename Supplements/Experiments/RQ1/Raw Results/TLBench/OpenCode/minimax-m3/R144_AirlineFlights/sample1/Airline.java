import java.text.ParseException;
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
        if (f == null) {
            return;
        }
        if (this.flights == null) {
            this.flights = new ArrayList<Flight>();
        }
        if (!this.flights.contains(f)) {
            this.flights.add(f);
        }
    }

    public void removeFlight(Flight f) {
        if (f == null || this.flights == null) {
            return;
        }
        this.flights.remove(f);
    }

    public boolean publishFlight(Flight f, Date now) {
        if (f == null || now == null) {
            return false;
        }
        if (f.getDepartureTime() == null || f.getArrivalTime() == null) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setLenient(false);
        try {
            String depStr = sdf.format(f.getDepartureTime());
            String arrStr = sdf.format(f.getArrivalTime());
            sdf.parse(depStr);
            sdf.parse(arrStr);
        } catch (ParseException e) {
            return false;
        }
        if (now.compareTo(f.getDepartureTime()) >= 0) {
            return false;
        }
        if (f.getDepartureTime().compareTo(f.getArrivalTime()) >= 0) {
            return false;
        }
        if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) {
            return false;
        }
        if (f.getDepartureAirport().getId() == null
                || f.getDepartureAirport().getId().equals(f.getArrivalAirport().getId())) {
            return false;
        }
        if (f.isOpenForBooking()) {
            return false;
        }
        f.setOpenForBooking(true);
        if (this.flights == null) {
            this.flights = new ArrayList<Flight>();
        }
        if (!this.flights.contains(f)) {
            this.flights.add(f);
        }
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        if (flightId == null || now == null) {
            return false;
        }
        Flight target = null;
        if (this.flights != null) {
            for (Flight f : this.flights) {
                if (f != null && flightId.equals(f.getId())) {
                    target = f;
                    break;
                }
            }
        }
        if (target == null) {
            return false;
        }
        if (!target.isOpenForBooking()) {
            return false;
        }
        if (now.compareTo(target.getDepartureTime()) >= 0) {
            return false;
        }
        target.setOpenForBooking(false);
        for (Reservation r : target.getReservations()) {
            if (r != null && r.getStatus() == ReservationStatus.CONFIRMED) {
                r.setStatus(ReservationStatus.CANCELED);
            }
        }
        return true;
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
            if (!f.isOpenForBooking()) {
                continue;
            }
            if (origin != null && f.getDepartureAirport() != null
                    && !origin.equals(f.getDepartureAirport().getId())) {
                continue;
            }
            if (dest != null && f.getArrivalAirport() != null
                    && !dest.equals(f.getArrivalAirport().getId())) {
                continue;
            }
            if (date != null && f.getDepartureTime() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String searchDay = sdf.format(date);
                String flightDay = sdf.format(f.getDepartureTime());
                if (!searchDay.equals(flightDay)) {
                    continue;
                }
            }
            result.add(f);
        }
        return result;
    }
}
