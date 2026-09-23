import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (f == null || now == null || listOfPassengerNames == null) return false;
        if (!f.isOpenForBooking()) return false;
        if (!now.before(f.getDepartureTime())) return false;

        Set<String> passengerSet = new HashSet<>();
        for (String name : listOfPassengerNames) {
            if (name == null || !passengerSet.add(name)) return false;
        }

        for (Reservation r : f.getReservations()) {
            if (r.getPassenger() != null && passengerSet.contains(r.getPassenger().getName())) {
                return false;
            }
        }

        Booking booking = new Booking(this);
        for (String name : listOfPassengerNames) {
            boolean success = booking.createReservation(f, name, now);
            if (!success) return false;
        }

        bookings.add(booking);
        return true;
    }

    public boolean confirm(String reservationID, Date now) {
        if (reservationID == null || now == null) return false;
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (reservationID.equals(r.getId())) {
                    Flight f = r.getFlight();
                    if (!f.isOpenForBooking()) return false;
                    if (!now.before(f.getDepartureTime())) return false;
                    if (r.getStatus() != ReservationStatus.PENDING) return false;
                    r.setStatus(ReservationStatus.CONFIRMED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        if (reservationID == null || now == null) return false;
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (reservationID.equals(r.getId())) {
                    Flight f = r.getFlight();
                    if (!f.isOpenForBooking()) return false;
                    if (!now.before(f.getDepartureTime())) return false;
                    if (r.getStatus() != ReservationStatus.PENDING && r.getStatus() != ReservationStatus.CONFIRMED) return false;
                    r.setStatus(ReservationStatus.CANCELED);
                    return true;
                }
            }
        }
        return false;
    }
}
