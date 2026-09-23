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
        if (bookings == null || reservationID == null || now == null) {
            return false;
        }
        for (Booking booking : bookings) {
            if (booking != null) {
                for (Reservation reservation : booking.getReservations()) {
                    if (reservation != null && reservationID.equals(reservation.getId()) && reservation.getFlight() != null && reservation.getFlight().isOpenForBooking() && now.before(reservation.getFlight().getDepartureTime())) {
                        reservation.setStatus(ReservationStatus.CONFIRMED);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        if (bookings == null || reservationID == null || now == null) {
            return false;
        }
        for (Booking booking : bookings) {
            if (booking != null) {
                for (Reservation reservation : booking.getReservations()) {
                    if (reservation != null && reservationID.equals(reservation.getId()) && reservation.getFlight() != null && reservation.getFlight().isOpenForBooking() && now.before(reservation.getFlight().getDepartureTime())) {
                        reservation.setStatus(ReservationStatus.CANCELED);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (f == null || now == null || listOfPassengerNames == null || !f.isOpenForBooking() || f.getDepartureTime() == null || !now.before(f.getDepartureTime())) {
            return false;
        }
        Booking booking = new Booking();
        booking.setCustomer(this);
        for (String name : listOfPassengerNames) {
            if (name == null) {
                return false;
            }
            boolean created = booking.createReservation(f, name, now);
            if (!created) {
                return false;
            }
        }
        if (bookings == null) {
            bookings = new ArrayList<Booking>();
        }
        bookings.add(booking);
        return true;
    }
}
