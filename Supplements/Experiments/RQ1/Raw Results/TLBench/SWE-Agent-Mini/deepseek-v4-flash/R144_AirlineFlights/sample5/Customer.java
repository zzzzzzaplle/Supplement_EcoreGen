import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Customer {
    private List<Booking> bookings;
    
    public Customer() {
        this.bookings = new ArrayList<>();
    }
    
    public List<Booking> getBookings() {
        return bookings;
    }
    
    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
    
    public boolean confirm(String reservationID, Date now) {
        if (reservationID == null || now == null) return false;
        for (Booking b : bookings) {
            if (b.getReservations() != null) {
                for (Reservation r : b.getReservations()) {
                    if (r.getId().equals(reservationID)) {
                        Flight flight = r.getFlight();
                        if (flight == null) return false;
                        if (now.after(flight.getDepartureTime()) || now.equals(flight.getDepartureTime())) return false;
                        if (!flight.isOpenForBooking()) return false;
                        r.setStatus(ReservationStatus.CONFIRMED);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean cancel(String reservationID, Date now) {
        if (reservationID == null || now == null) return false;
        for (Booking b : bookings) {
            if (b.getReservations() != null) {
                for (Reservation r : b.getReservations()) {
                    if (r.getId().equals(reservationID)) {
                        Flight flight = r.getFlight();
                        if (flight == null) return false;
                        if (now.after(flight.getDepartureTime()) || now.equals(flight.getDepartureTime())) return false;
                        if (!flight.isOpenForBooking()) return false;
                        r.setStatus(ReservationStatus.CANCELED);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (f == null || now == null || listOfPassengerNames == null) return false;
        if (listOfPassengerNames.isEmpty()) return false;
        if (!f.isOpenForBooking()) return false;
        if (now.after(f.getDepartureTime()) || now.equals(f.getDepartureTime())) return false;
        
        // Check for duplicate passengers on the flight
        List<Reservation> existingReservations = f.getReservations();
        for (String name : listOfPassengerNames) {
            if (existingReservations != null) {
                for (Reservation r : existingReservations) {
                    if (r.getPassenger() != null && r.getPassenger().getName().equals(name)) {
                        return false;
                    }
                }
            }
        }
        
        // Check for duplicate names in the input list
        for (int i = 0; i < listOfPassengerNames.size(); i++) {
            for (int j = i + 1; j < listOfPassengerNames.size(); j++) {
                if (listOfPassengerNames.get(i).equals(listOfPassengerNames.get(j))) {
                    return false;
                }
            }
        }
        
        Booking booking = new Booking(this);
        for (String passengerName : listOfPassengerNames) {
            boolean success = booking.createReservation(f, passengerName, now);
            if (!success) return false;
        }
        
        if (bookings == null) {
            bookings = new ArrayList<>();
        }
        bookings.add(booking);
        return true;
    }
}
