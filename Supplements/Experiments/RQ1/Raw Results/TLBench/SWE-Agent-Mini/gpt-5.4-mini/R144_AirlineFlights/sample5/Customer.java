import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Customer {
    private List<Booking> bookings;

    public Customer() {
        this.bookings = new ArrayList<Booking>();
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public boolean confirm(String reservationID, Date now) {
        if (reservationID == null || now == null) {
            return false;
        }
        for (Booking b : bookings) {
            if (b != null) {
                for (Reservation r : b.getReservations()) {
                    if (r != null && reservationID.equals(r.getId()) && r.getFlight() != null && r.getFlight().isOpenForBooking() && now.before(r.getFlight().getDepartureTime())) {
                        r.setStatus(ReservationStatus.CONFIRMED);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        if (reservationID == null || now == null) {
            return false;
        }
        for (Booking b : bookings) {
            if (b != null) {
                for (Reservation r : b.getReservations()) {
                    if (r != null && reservationID.equals(r.getId()) && r.getFlight() != null && r.getFlight().isOpenForBooking() && now.before(r.getFlight().getDepartureTime())) {
                        r.setStatus(ReservationStatus.CANCELED);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (f == null || now == null || listOfPassengerNames == null || !f.isOpenForBooking() || !now.before(f.getDepartureTime())) {
            return false;
        }
        Booking booking = new Booking();
        booking.setCustomer(this);
        for (String name : listOfPassengerNames) {
            if (!booking.createReservation(f, name, now)) {
                return false;
            }
        }
        bookings.add(booking);
        return true;
    }
}
