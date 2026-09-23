import java.util.List;
import java.util.ArrayList;
import java.util.Date;

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
                    if (r.getId() != null && r.getId().equals(reservationID)) {
                        Flight f = r.getFlight();
                        if (f == null) return false;
                        if (!f.isOpenForBooking()) return false;
                        if (!now.before(f.getDepartureTime())) return false;
                        
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
                    if (r.getId() != null && r.getId().equals(reservationID)) {
                        Flight f = r.getFlight();
                        if (f == null) return false;
                        if (!f.isOpenForBooking()) return false;
                        if (!now.before(f.getDepartureTime())) return false;
                        
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
        if (!now.before(f.getDepartureTime())) return false;
        
        // Check for duplicate passenger names within the list
        for (int i = 0; i < listOfPassengerNames.size(); i++) {
            for (int j = i + 1; j < listOfPassengerNames.size(); j++) {
                if (listOfPassengerNames.get(i).equals(listOfPassengerNames.get(j))) {
                    return false;
                }
            }
        }
        
        // Check for duplicate passengers on the flight
        List<Reservation> existingReservations = f.getReservations();
        if (existingReservations != null) {
            for (String name : listOfPassengerNames) {
                for (Reservation r : existingReservations) {
                    if (r.getPassenger() != null && r.getPassenger().getName() != null &&
                        r.getPassenger().getName().equals(name)) {
                        return false;
                    }
                }
            }
        }
        
        Booking booking = new Booking();
        booking.setCustomer(this);
        
        for (String passengerName : listOfPassengerNames) {
            boolean success = booking.createReservation(f, passengerName, now);
            if (!success) return false;
        }
        
        if (this.bookings == null) {
            this.bookings = new ArrayList<>();
        }
        this.bookings.add(booking);
        
        return true;
    }
}
