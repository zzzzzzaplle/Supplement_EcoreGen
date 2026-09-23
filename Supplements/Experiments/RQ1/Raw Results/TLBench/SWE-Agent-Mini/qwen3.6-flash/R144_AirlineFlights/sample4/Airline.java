import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
        flights.remove(f);
    }

    public boolean publishFlight(Flight f, Date now) {
        // A flight can only be published once
        if (f == null || f.isOpenForBooking()) {
            return false;
        }
        
        // Check temporal consistency
        if (f.getDepartureTime() == null || f.getArrivalTime() == null || now == null) {
            return false;
        }
        
        if (now.getTime() >= f.getDepartureTime().getTime() || 
            f.getDepartureTime().getTime() >= f.getArrivalTime().getTime()) {
            return false;
        }
        
        // Check route integrity
        if (f.getDepartureAirport() == null || f.getArrivalAirport() == null ||
            f.getDepartureAirport().getId().equals(f.getArrivalAirport().getId())) {
            return false;
        }
        
        // Set openForBooking to true
        f.setOpenForBooking(true);
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        Flight flight = findFlightById(flightId);
        if (flight == null) {
            return false;
        }
        
        // Flight must be open and not yet departed
        if (!flight.isOpenForBooking()) {
            return false;
        }
        
        if (now.getTime() >= flight.getDepartureTime().getTime()) {
            return false;
        }
        
        // Close the flight
        flight.setOpenForBooking(false);
        
        // Cancel every confirmed reservation
        for (Reservation reservation : flight.getReservations()) {
            if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
                reservation.setStatus(ReservationStatus.CANCELED);
            }
        }
        
        return true;
    }

    public List<Flight> searchFlights(String origin, String dest, Date date) {
        List<Flight> result = new ArrayList<>();
        for (Flight flight : flights) {
            if (flight.getDepartureAirport() != null && 
                flight.getArrivalAirport() != null &&
                flight.getDepartureTime() != null) {
                
                boolean dateMatch = true;
                if (date != null) {
                    // Compare using Calendar for year, month, and day
                    Calendar flightCal = Calendar.getInstance();
                    flightCal.setTime(flight.getDepartureTime());
                    
                    Calendar searchCal = Calendar.getInstance();
                    searchCal.setTime(date);
                    
                    if (flightCal.get(Calendar.YEAR) != searchCal.get(Calendar.YEAR) ||
                        flightCal.get(Calendar.MONTH) != searchCal.get(Calendar.MONTH) ||
                        flightCal.get(Calendar.DAY_OF_MONTH) != searchCal.get(Calendar.DAY_OF_MONTH)) {
                        dateMatch = false;
                    }
                }
                
                if (dateMatch && 
                    flight.getDepartureAirport().getId().equals(origin) &&
                    flight.getArrivalAirport().getId().equals(dest)) {
                    result.add(flight);
                }
            }
        }
        return result;
    }

    private Flight findFlightById(String flightId) {
        for (Flight flight : flights) {
            if (flight.getId().equals(flightId)) {
                return flight;
            }
        }
        return null;
    }
}
