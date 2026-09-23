import java.util.List;
import java.util.ArrayList;
import java.util.Date;

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
        if (!flights.contains(f)) {
            flights.add(f);
        }
    }

    public void removeFlight(Flight f) {
        flights.remove(f);
    }

    public boolean publishFlight(Flight f, Date now) {
        if (f == null || now == null) return false;
        if (f.isOpenForBooking()) return false;
        
        // Check valid timestamps (not null)
        if (f.getDepartureTime() == null || f.getArrivalTime() == null) return false;
        
        // Check temporal consistency: currentTime < departureTime < arrivalTime
        if (!now.before(f.getDepartureTime())) return false;
        if (!f.getDepartureTime().before(f.getArrivalTime())) return false;
        
        // Check route integrity: departureAirport != arrivalAirport
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
                // Check flight has not yet departed
                if (!now.before(f.getDepartureTime())) return false;
                
                f.setOpenForBooking(false);
                
                // Cancel every confirmed reservation
                List<Reservation> confirmedReservations = f.getConfirmedReservations();
                List<Reservation> allReservations = f.getReservations();
                if (allReservations != null) {
                    for (Reservation r : allReservations) {
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
        for (Flight f : flights) {
            if (f.isOpenForBooking() && 
                f.getDepartureAirport() != null && 
                f.getArrivalAirport() != null &&
                f.getDepartureAirport().getId().equals(origin) &&
                f.getArrivalAirport().getId().equals(dest)) {
                // Check if the flight is on the same date
                if (date != null && f.getDepartureTime() != null) {
                    // Compare dates only (ignore time)
                    if (sameDay(f.getDepartureTime(), date)) {
                        result.add(f);
                    }
                } else {
                    result.add(f);
                }
            }
        }
        return result;
    }

    private boolean sameDay(Date d1, Date d2) {
        java.util.Calendar cal1 = java.util.Calendar.getInstance();
        java.util.Calendar cal2 = java.util.Calendar.getInstance();
        cal1.setTime(d1);
        cal2.setTime(d2);
        return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
               cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR);
    }
}
