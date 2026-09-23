import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

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
        if (f == null || now == null) {
            return false;
        }
        // Flight may be published only once
        if (f.isOpenForBooking()) {
            return false;
        }
        // Check departure and arrival timestamps are not null
        if (f.getDepartureTime() == null || f.getArrivalTime() == null) {
            return false;
        }
        // Check temporal consistency: currentTime < departureTime < arrivalTime
        if (!now.before(f.getDepartureTime()) || !f.getDepartureTime().before(f.getArrivalTime())) {
            return false;
        }
        // Check route integrity: departureAirport != arrivalAirport
        if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) {
            return false;
        }
        // Compare by object reference or ID
        if (f.getDepartureAirport() == f.getArrivalAirport()) {
            return false;
        }
        String depId = f.getDepartureAirport().getId();
        String arrId = f.getArrivalAirport().getId();
        if (depId != null && arrId != null && depId.equals(arrId)) {
            return false;
        }
        
        f.setOpenForBooking(true);
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        if (flightId == null || now == null) {
            return false;
        }
        Flight targetFlight = null;
        if (flights != null) {
            for (Flight f : flights) {
                if (flightId.equals(f.getId())) {
                    targetFlight = f;
                    break;
                }
            }
        }
        if (targetFlight == null) {
            return false;
        }
        // Check flight has not yet departed
        if (targetFlight.getDepartureTime() != null && !now.before(targetFlight.getDepartureTime())) {
            return false;
        }
        // Check flight is currently open
        if (!targetFlight.isOpenForBooking()) {
            return false;
        }
        // Change status to closed
        targetFlight.setOpenForBooking(false);
        // Cancel every confirmed reservation
        List<Reservation> reservations = targetFlight.getReservations();
        if (reservations != null) {
            for (Reservation r : reservations) {
                if (r.getStatus() == ReservationStatus.CONFIRMED) {
                    r.setStatus(ReservationStatus.CANCELED);
                }
            }
        }
        return true;
    }

    public List<Flight> searchFlights(String origin, String dest, Date date) {
        List<Flight> result = new ArrayList<Flight>();
        if (flights != null) {
            for (Flight f : flights) {
                if (!f.isOpenForBooking()) {
                    continue;
                }
                boolean matchOrigin = false;
                boolean matchDest = false;
                if (origin == null || origin.isEmpty()) {
                    matchOrigin = true;
                } else if (f.getDepartureAirport() != null && origin.equals(f.getDepartureAirport().getId())) {
                    matchOrigin = true;
                }
                if (dest == null || dest.isEmpty()) {
                    matchDest = true;
                } else if (f.getArrivalAirport() != null && dest.equals(f.getArrivalAirport().getId())) {
                    matchDest = true;
                }
                boolean matchDate = true;
                if (date != null && f.getDepartureTime() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    if (!sdf.format(date).equals(sdf.format(f.getDepartureTime()))) {
                        matchDate = false;
                    }
                }
                if (matchOrigin && matchDest && matchDate) {
                    result.add(f);
                }
            }
        }
        return result;
    }
}
