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
        if (reservationID == null || now == null) {
            return false;
        }
        for (Booking booking : bookings) {
            for (Reservation reservation : booking.getReservations()) {
                if (reservationID.equals(reservation.getId()) && reservation.getFlight().isOpenForBooking() && now.before(reservation.getFlight().getDepartureTime())) {
                    reservation.setStatus(ReservationStatus.CONFIRMED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        if (reservationID == null || now == null) {
            return false;
        }
        for (Booking booking : bookings) {
            for (Reservation reservation : booking.getReservations()) {
                if (reservationID.equals(reservation.getId()) && reservation.getFlight().isOpenForBooking() && now.before(reservation.getFlight().getDepartureTime())) {
                    reservation.setStatus(ReservationStatus.CANCELED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (f == null || now == null || listOfPassengerNames == null) {
            return false;
        }
        Booking booking = new Booking();
        booking.setCustomer(this);
        for (String passengerName : listOfPassengerNames) {
            if (!booking.createReservation(f, passengerName, now)) {
                return false;
            }
        }
        bookings.add(booking);
        return true;
    }
}
