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
        return changeReservationStatus(reservationID, now, ReservationStatus.CONFIRMED);
    }

    public boolean cancel(String reservationID, Date now) {
        return changeReservationStatus(reservationID, now, ReservationStatus.CANCELED);
    }

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (f == null || now == null || listOfPassengerNames == null || listOfPassengerNames.isEmpty()) {
            return false;
        }
        if (!f.isOpenForBooking() || f.getDepartureTime() == null || !now.before(f.getDepartureTime())) {
            return false;
        }
        Booking b = new Booking();
        b.setCustomer(this);
        for (String name : listOfPassengerNames) {
            if (!b.createReservation(f, name, now)) {
                return false;
            }
        }
        bookings.add(b);
        return true;
    }

    private boolean changeReservationStatus(String reservationID, Date now, ReservationStatus status) {
        if (reservationID == null || now == null) {
            return false;
        }
        for (Booking b : bookings) {
            if (b == null || b.getReservations() == null) {
                continue;
            }
            for (Reservation r : b.getReservations()) {
                if (r != null && reservationID.equals(r.getId())) {
                    Flight f = r.getFlight();
                    if (f == null || f.getDepartureTime() == null || !now.before(f.getDepartureTime()) || !f.isOpenForBooking()) {
                        return false;
                    }
                    r.setStatus(status);
                    return true;
                }
            }
        }
        return false;
    }
}
