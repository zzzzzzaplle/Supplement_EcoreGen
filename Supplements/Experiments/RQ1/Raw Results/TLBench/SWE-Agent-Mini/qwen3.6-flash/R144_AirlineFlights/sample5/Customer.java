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
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (r.getId().equals(reservationID)) {
                    r.setStatus(ReservationStatus.CONFIRMED);
                    
                    Flight f = r.getFlight();
                    if (f != null && now != null) {
                        if (now.getTime() >= f.getDepartureTime().getTime()) {
                            return false;
                        }
                        if (!f.isOpenForBooking()) {
                            return false;
                        }
                    }
                    
                    return true;
                }
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (r.getId().equals(reservationID)) {
                    r.setStatus(ReservationStatus.CANCELED);
                    
                    Flight f = r.getFlight();
                    if (f != null && now != null) {
                        if (now.getTime() >= f.getDepartureTime().getTime()) {
                            return false;
                        }
                        if (!f.isOpenForBooking()) {
                            return false;
                        }
                    }
                    
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (f == null || !f.isOpenForBooking()) {
            return false;
        }

        if (now != null && now.getTime() >= f.getDepartureTime().getTime()) {
            return false;
        }

        List<String> duplicates = new ArrayList<>();
        for (String name : listOfPassengerNames) {
            if (duplicates.contains(name)) {
                return false;
            }
            duplicates.add(name);
        }

        Booking booking = new Booking();
        booking.setCustomer(this);
        bookings.add(booking);

        for (String passName : listOfPassengerNames) {
            booking.createReservation(f, passName, now);
        }

        return true;
    }
}
