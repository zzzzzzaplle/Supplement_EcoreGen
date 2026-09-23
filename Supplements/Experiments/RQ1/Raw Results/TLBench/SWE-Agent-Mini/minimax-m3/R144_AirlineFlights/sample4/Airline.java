import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
        if (!this.flights.contains(f)) {
            this.flights.add(f);
        }
    }

    public void removeFlight(Flight f) {
        if (this.flights != null) {
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
        if (now == null) {
            return false;
        }
        if (!now.before(f.getDepartureTime())) {
            return false;
        }
        if (!f.getDepartureTime().before(f.getArrivalTime())) {
            return false;
        }
        if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) {
            return false;
        }
        if (f.getDepartureAirport().getId() == null || f.getArrivalAirport().getId() == null) {
            return false;
        }
        if (f.getDepartureAirport().getId().equals(f.getArrivalAirport().getId())) {
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
        if (this.flights == null) {
            return false;
        }
        for (Flight f : this.flights) {
            if (f != null && flightId.equals(f.getId())) {
                if (!f.isOpenForBooking()) {
                    return false;
                }
                if (f.getDepartureTime() != null && !now.before(f.getDepartureTime())) {
                    return false;
                }
                f.setOpenForBooking(false);
                if (f.getReservations() != null) {
                    for (Reservation r : f.getReservations()) {
                        if (r != null && r.getStatus() == ReservationStatus.CONFIRMED) {
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
            if (origin != null && (f.getDepartureAirport() == null
                    || !origin.equals(f.getDepartureAirport().getId()))) {
                continue;
            }
            if (dest != null && (f.getArrivalAirport() == null
                    || !dest.equals(f.getArrivalAirport().getId()))) {
                continue;
            }
            if (date != null && f.getDepartureTime() != null) {
                java.util.Calendar cal1 = java.util.Calendar.getInstance();
                cal1.setTime(date);
                java.util.Calendar cal2 = java.util.Calendar.getInstance();
                cal2.setTime(f.getDepartureTime());
                boolean sameDay = cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR)
                        && cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR);
                if (!sameDay) {
                    continue;
                }
            }
            result.add(f);
        }
        return result;
    }
}
