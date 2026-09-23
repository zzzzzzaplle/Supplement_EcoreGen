import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class Customer {
    private List<Booking> bookings = new ArrayList<>();

    public Customer() {}

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }

    public boolean confirm(String reservationID, Date now) {
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (r.getId() != null && r.getId().equals(reservationID)) {
                    Flight f = r.getFlight();
                    if (f.isOpenForBooking() && now.before(f.getDepartureTime())) {
                        r.setStatus(ReservationStatus.CONFIRMED);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (r.getId() != null && r.getId().equals(reservationID)) {
                    Flight f = r.getFlight();
                    if (f.isOpenForBooking() && now.before(f.getDepartureTime())) {
                        r.setStatus(ReservationStatus.CANCELED);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (!f.isOpenForBooking() || now.after(f.getDepartureTime())) {
            return false;
        }

        // Check for duplicate passengers in the provided list
        Set<String> uniquePassengers = new HashSet<>();
        for (String name : listOfPassengerNames) {
            if (!uniquePassengers.add(name)) return false; // Duplicate found in list
        }
        
        // Pass to Booking to check against flight and create
        Booking b = new Booking();
        b.setCustomer(this);
        for(String name : listOfPassengerNames) {
            if(!b.createReservation(f, name, now)) return false;
        }
        this.bookings.add(b);
        return true;
    }
}
