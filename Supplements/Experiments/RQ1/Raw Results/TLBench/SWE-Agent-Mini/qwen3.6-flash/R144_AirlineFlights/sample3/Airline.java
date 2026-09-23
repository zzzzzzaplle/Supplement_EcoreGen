import java.util.*;
import java.text.SimpleDateFormat;
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
        if (f != null && !flights.contains(f)) {
            flights.add(f);
        }
    }

    public void removeFlight(Flight f) {
        if (f != null) {
            flights.remove(f);
        }
    }

    public boolean publishFlight(Flight f, Date now) {
        if (f == null) {
            return false;
        }
        // Check that the flight is not already published (openForBooking must be false before publishing)
        if (f.isOpenForBooking()) {
            return false;
        }
        // Validate departure and arrival timestamps
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String departureStr = sdf.format(f.getDepartureTime());
            String arrivalStr = sdf.format(f.getArrivalTime());
            sdf.parse(departureStr);
            sdf.parse(arrivalStr);
        } catch (Exception e) {
            return false;
        }
        // Check temporal consistency: currentTime < departureTime < arrivalTime
        if (now == null || f.getDepartureTime() == null || f.getArrivalTime() == null) {
            return false;
        }
        if (!now.before(f.getDepartureTime())) {
            return false;
        }
        if (!f.getDepartureTime().before(f.getArrivalTime())) {
            return false;
        }
        // Check route integrity: departureAirport != arrivalAirport
        Airport dep = f.getDepartureAirport();
        Airport arr = f.getArrivalAirport();
        if (dep == null || arr == null) {
            return false;
        }
        if (dep.getId() != null && dep.getId().equals(arr.getId())) {
            return false;
        }
        // Mark the flight as open for booking
        f.setOpenForBooking(true);
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        if (flightId == null || flightId.isEmpty() || now == null) {
            return false;
        }
        Flight flight = null;
        for (Flight f : flights) {
            if (f != null && flightId.equals(f.getId())) {
                flight = f;
                break;
            }
        }
        if (flight == null) {
            return false;
        }
        // Check that the flight is currently open
        if (!flight.isOpenForBooking()) {
            return false;
        }
        // Check that the flight has not yet departed
        if (!now.before(flight.getDepartureTime())) {
            return false;
        }
        // Change status to closed
        flight.setOpenForBooking(false);
        // Cancel every confirmed reservation
        for (Reservation res : flight.getReservations()) {
            if (res != null && res.getStatus() == ReservationStatus.CONFIRMED) {
                res.setStatus(ReservationStatus.CANCELED);
            }
        }
        return true;
    }

    public List<Flight> searchFlights(String origin, String dest, Date date) {
        List<Flight> results = new ArrayList<>();
        for (Flight f : flights) {
            if (f == null) {
                continue;
            }
            Airport dep = f.getDepartureAirport();
            Airport arr = f.getArrivalAirport();
            if (dep == null || arr == null || date == null) {
                continue;
            }
            // Search by origin cities
            boolean originMatch = false;
            if (origin != null && !origin.isEmpty()) {
                List<City> depCities = dep.getCities();
                if (depCities != null) {
                    for (City c : depCities) {
                        if (c != null && origin.equalsIgnoreCase(c.getName())) {
                            originMatch = true;
                            break;
                        }
                    }
                }
            } else {
                originMatch = true;
            }
            // Search by dest cities
            boolean destMatch = false;
            if (dest != null && !dest.isEmpty()) {
                List<City> arrCities = arr.getCities();
                if (arrCities != null) {
                    for (City c : arrCities) {
                        if (c != null && dest.equalsIgnoreCase(c.getName())) {
                            destMatch = true;
                            break;
                        }
                    }
                }
            } else {
                destMatch = true;
            }
            if (originMatch && destMatch) {
                // Match by date comparison (same day)
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dateStr = sdf.format(date);
                String depDateStr = sdf.format(f.getDepartureTime());
                if (dateStr.compareTo(depDateStr) == 0) {
                    results.add(f);
                }
            }
        }
        return results;
    }
}
