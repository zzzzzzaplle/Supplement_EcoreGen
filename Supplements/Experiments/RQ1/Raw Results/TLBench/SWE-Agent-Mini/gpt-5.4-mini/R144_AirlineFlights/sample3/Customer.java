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
        if (reservationID == null || now == null || bookings == null) {
            return false;
        }
        for (Booking b : bookings) {
            if (b != null && b.getReservations() != null) {
                for (Reservation r : b.getReservations()) {
                    if (r != null && reservationID.equals(r.getId())) {
                        r.setStatus(ReservationStatus.CONFIRMED);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        if (reservationID == null || now == null || bookings == null) {
            return false;
        }
        for (Booking b : bookings) {
            if (b != null && b.getReservations() != null) {
                for (Reservation r : b.getReservations()) {
                    if (r != null && reservationID.equals(r.getId())) {
                        r.setStatus(ReservationStatus.CANCELED);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (f == null || now == null || listOfPassengerNames == null || !f.isOpenForBooking()) {
            return false;
        }
        Booking booking = new Booking();
        booking.setCustomer(this);
        for (String name : listOfPassengerNames) {
            booking.createReservation(f, name, now);
        }
        if (bookings == null) {
            bookings = new ArrayList<>();
        }
        bookings.add(booking);
        return true;
    }
}
