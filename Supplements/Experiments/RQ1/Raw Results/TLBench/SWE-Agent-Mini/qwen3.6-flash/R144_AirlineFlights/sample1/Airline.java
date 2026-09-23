import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

class Airline {
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
        if (f.isOpenForBooking()) {
            return false;
        }
        Date departureTime = f.getDepartureTime();
        Date arrivalTime = f.getArrivalTime();
        if (departureTime == null || arrivalTime == null) {
            return false;
        }
        if (now == null) {
            return false;
        }
        if (now.after(departureTime)) {
            return false;
        }
        if (departureTime.after(arrivalTime)) {
            return false;
        }
        Airport departureAirport = f.getDepartureAirport();
        Airport arrivalAirport = f.getArrivalAirport();
        if (departureAirport == null || arrivalAirport == null) {
            return false;
        }
        if (departureAirport.getId() != null && arrivalAirport.getId() != null
                && departureAirport.getId().equals(arrivalAirport.getId())) {
            return false;
        }
        f.setOpenForBooking(true);
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        if (flightId == null || now == null) {
            return false;
        }
        Flight flight = null;
        for (Flight f : flights) {
            if (flightId.equals(f.getId())) {
                flight = f;
                break;
            }
        }
        if (flight == null) {
            return false;
        }
        if (!flight.isOpenForBooking()) {
            return false;
        }
        Date departureTime = flight.getDepartureTime();
        if (now.after(departureTime)) {
            return false;
        }
        flight.setOpenForBooking(false);
        List<Reservation> reservations = flight.getReservations();
        for (Reservation r : reservations) {
            if (r.getStatus() == ReservationStatus.CONFIRMED) {
                r.setStatus(ReservationStatus.CANCELED);
            }
        }
        return true;
    }

    public List<Flight> searchFlights(String origin, String dest, Date date) {
        List<Flight> result = new ArrayList<>();
        if (origin == null || dest == null || date == null) {
            return result;
        }
        for (Flight f : flights) {
            if (f.getDepartureAirport() != null && f.getArrivalAirport() != null
                    && f.getDepartureTime() != null) {
                String orig = origin.toUpperCase();
                String destStr = dest.toUpperCase();
                boolean originMatch = false;
                List<City> originCities = f.getDepartureAirport().getCities();
                if (originCities != null) {
                    for (City c : originCities) {
                        if (c != null && c.getName() != null
                                && c.getName().toUpperCase().contains(orig)) {
                            originMatch = true;
                            break;
                        }
                        if (f.getDepartureAirport().getId() != null
                                && f.getDepartureAirport().getId().toUpperCase().contains(orig)) {
                            originMatch = true;
                            break;
                        }
                    }
                }
                if (!originMatch && f.getDepartureAirport().getId() != null) {
                    orig = f.getDepartureAirport().getId().toUpperCase();
                    // Fallback: try id match
                }
                if (originMatch || (f.getDepartureAirport() != null && f.getDepartureAirport().getId() != null
                        && f.getDepartureAirport().getId().equalsIgnoreCase(origin))) {
                    boolean destMatch = false;
                    if (f.getArrivalAirport() != null && f.getArrivalAirport().getId() != null
                            && f.getArrivalAirport().getId().equalsIgnoreCase(dest)) {
                        destMatch = true;
                    } else if (f.getArrivalAirport() != null) {
                        List<City> destCities = f.getArrivalAirport().getCities();
                        if (destCities != null) {
                            for (City c : destCities) {
                                if (c != null && c.getName() != null
                                        && c.getName().toUpperCase().contains(destStr)) {
                                    destMatch = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (destMatch) {
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                        String flightDateStr = sdf.format(f.getDepartureTime());
                        String searchDateStr = sdf.format(date);
                        if (flightDateStr.equals(searchDateStr)) {
                            result.add(f);
                        }
                    }
                }
            }
        }
        return result;
    }
}
