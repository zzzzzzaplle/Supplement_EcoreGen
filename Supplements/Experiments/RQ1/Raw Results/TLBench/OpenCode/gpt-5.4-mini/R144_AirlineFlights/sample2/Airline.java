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
        if (flights == null) {
            flights = new ArrayList<Flight>();
        }
        if (f != null && !flights.contains(f)) {
            flights.add(f);
        }
    }

    public void removeFlight(Flight f) {
        if (flights != null) {
            flights.remove(f);
        }
    }

    public boolean publishFlight(Flight f, Date now) {
        if (f == null || now == null || f.isOpenForBooking()) {
            return false;
        }
        if (f.getDepartureTime() == null || f.getArrivalTime() == null || f.getDepartureAirport() == null || f.getArrivalAirport() == null) {
            return false;
        }
        if (now.compareTo(f.getDepartureTime()) >= 0) {
            return false;
        }
        if (!f.getDepartureTime().before(f.getArrivalTime())) {
            return false;
        }
        if (f.getDepartureAirport().equals(f.getArrivalAirport())) {
            return false;
        }
        f.setOpenForBooking(true);
        addFlight(f);
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        if (flightId == null || now == null || flights == null) {
            return false;
        }
        for (Flight flight : flights) {
            if (flight != null && flightId.equals(flight.getId())) {
                if (!flight.isOpenForBooking() || flight.getDepartureTime() == null || !now.before(flight.getDepartureTime())) {
                    return false;
                }
                flight.setOpenForBooking(false);
                if (flight.getReservations() != null) {
                    for (Reservation reservation : flight.getReservations()) {
                        if (reservation != null && reservation.getStatus() == ReservationStatus.CONFIRMED) {
                            reservation.setStatus(ReservationStatus.CANCELED);
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
        if (flights == null) {
            return result;
        }
        for (Flight flight : flights) {
            if (flight != null && flight.getDepartureAirport() != null && flight.getArrivalAirport() != null) {
                boolean matchesOrigin = origin == null || (flight.getDepartureAirport().getId() != null && flight.getDepartureAirport().getId().equals(origin));
                boolean matchesDest = dest == null || (flight.getArrivalAirport().getId() != null && flight.getArrivalAirport().getId().equals(dest));
                boolean matchesDate = date == null || (flight.getDepartureTime() != null && flight.getDepartureTime().equals(date));
                if (matchesOrigin && matchesDest && matchesDate) {
                    result.add(flight);
                }
            }
        }
        return result;
    }
}
