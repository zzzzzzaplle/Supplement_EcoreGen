import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
        if (f != null) {
            flights.add(f);
        }
    }

    public void removeFlight(Flight f) {
        if (f != null) {
            flights.remove(f);
        }
    }

    public boolean publishFlight(Flight f, Date now) {
        if (f == null || now == null) {
            return false;
        }

        if (f.isOpenForBooking()) {
            return false;
        }

        if (f.getDepartureTime() == null || f.getArrivalTime() == null) {
            return false;
        }

        if (now.getTime() >= f.getDepartureTime().getTime()) {
            return false;
        }

        if (f.getDepartureTime().getTime() >= f.getArrivalTime().getTime()) {
            return false;
        }

        if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) {
            return false;
        }

        if (f.getDepartureAirport().getId().equals(f.getArrivalAirport().getId())) {
            return false;
        }

        f.setOpenForBooking(true);
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        for (Flight f : flights) {
            if (f.getId().equals(flightId)) {
                if (f.isOpenForBooking() && now.getTime() < f.getDepartureTime().getTime()) {
                    f.setOpenForBooking(false);
                    
                    for (Reservation r : f.getReservations()) {
                        if (r.getStatus() == ReservationStatus.CONFIRMED) {
                            r.setStatus(ReservationStatus.CANCELED);
                        }
                    }
                    
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public List<Flight> searchFlights(String origin, String dest, Date date) {
        if (origin == null || dest == null || date == null) {
            return new ArrayList<>();
        }
        
        return flights.stream()
            .filter(f -> f.isOpenForBooking())
            .filter(f -> f.getDepartureAirport() != null && f.getDepartureAirport().getId().equals(origin))
            .filter(f -> f.getArrivalAirport() != null && f.getArrivalAirport().getId().equals(dest))
            .filter(f -> {
                if (f.getDepartureTime() != null) {
                    return date.getTime() <= f.getDepartureTime().getTime();
                }
                return false;
            })
            .collect(Collectors.toList());
    }
}
