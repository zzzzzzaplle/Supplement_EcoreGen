import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        if (flights == null) {
            flights = new ArrayList<>();
        }
        flights.add(f);
    }
    
    public void removeFlight(Flight f) {
        if (flights != null) {
            flights.remove(f);
        }
    }
    
    public boolean publishFlight(Flight f, Date now) {
        if (f == null || now == null) return false;
        
        // Flight must not be already published
        if (f.isOpenForBooking()) return false;
        
        // Check valid timestamps (format validation is implied by having Date objects)
        Date depTime = f.getDepartureTime();
        Date arrTime = f.getArrivalTime();
        
        if (depTime == null || arrTime == null) return false;
        
        // currentTime < departureTime < arrivalTime
        if (!now.before(depTime)) return false;
        if (!depTime.before(arrTime)) return false;
        
        // departureAirport != arrivalAirport
        if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) return false;
        if (f.getDepartureAirport().getId().equals(f.getArrivalAirport().getId())) return false;
        
        // Publish the flight
        f.setOpenForBooking(true);
        return true;
    }
    
    public boolean closeFlight(String flightId, Date now) {
        if (flightId == null || now == null) return false;
        
        for (Flight f : flights) {
            if (f.getId().equals(flightId)) {
                if (!f.isOpenForBooking()) return false;
                if (now.after(f.getDepartureTime()) || now.equals(f.getDepartureTime())) return false;
                
                f.setOpenForBooking(false);
                
                // Cancel every confirmed reservation
                List<Reservation> reservations = f.getReservations();
                if (reservations != null) {
                    for (Reservation r : reservations) {
                        if (r.getStatus() == ReservationStatus.CONFIRMED) {
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
        List<Flight> result = new ArrayList<>();
        if (origin == null || dest == null || date == null) return result;
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String targetDate = sdf.format(date);
        
        for (Flight f : flights) {
            if (f.isOpenForBooking()) {
                String depDate = sdf.format(f.getDepartureTime());
                if (f.getDepartureAirport() != null && f.getArrivalAirport() != null) {
                    if (f.getDepartureAirport().getId().equals(origin) && 
                        f.getArrivalAirport().getId().equals(dest) &&
                        depDate.equals(targetDate)) {
                        result.add(f);
                    }
                }
            }
        }
        return result;
    }
}
