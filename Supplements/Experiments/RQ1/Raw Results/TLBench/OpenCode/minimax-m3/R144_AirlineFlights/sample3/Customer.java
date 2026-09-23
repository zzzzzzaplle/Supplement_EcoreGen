import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Customer {
    private List<Booking> bookings;

    public Customer() {
        this.bookings = new ArrayList<Booking>();
    }

    public List<Booking> getBookings() {
        if (this.bookings == null) {
            this.bookings = new ArrayList<Booking>();
        }
        return this.bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public boolean confirm(String reservationID, Date now) {
        if (reservationID == null) {
            return false;
        }
        for (Booking b : this.getBookings()) {
            if (b == null) {
                continue;
            }
            for (Reservation r : b.getReservations()) {
                if (r == null) {
                    continue;
                }
                if (reservationID.equals(r.getId())) {
                    Flight f = r.getFlight();
                    if (f == null) {
                        return false;
                    }
                    if (!f.isOpenForBooking()) {
                        return false;
                    }
                    if (f.getDepartureTime() != null && now != null
                            && !now.before(f.getDepartureTime())) {
                        return false;
                    }
                    r.setStatus(ReservationStatus.CONFIRMED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        if (reservationID == null) {
            return false;
        }
        for (Booking b : this.getBookings()) {
            if (b == null) {
                continue;
            }
            for (Reservation r : b.getReservations()) {
                if (r == null) {
                    continue;
                }
                if (reservationID.equals(r.getId())) {
                    Flight f = r.getFlight();
                    if (f == null) {
                        return false;
                    }
                    if (!f.isOpenForBooking()) {
                        return false;
                    }
                    if (f.getDepartureTime() != null && now != null
                            && !now.before(f.getDepartureTime())) {
                        return false;
                    }
                    r.setStatus(ReservationStatus.CANCELED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (f == null) {
            return false;
        }
        if (!f.isOpenForBooking()) {
            return false;
        }
        if (f.getDepartureTime() != null && now != null
                && !now.before(f.getDepartureTime())) {
            return false;
        }
        if (listOfPassengerNames == null || listOfPassengerNames.isEmpty()) {
            return false;
        }
        Set<String> seen = new HashSet<String>();
        for (String name : listOfPassengerNames) {
            if (name == null) {
                return false;
            }
            if (!seen.add(name)) {
                return false;
            }
        }
        for (Reservation r : f.getReservations()) {
            if (r == null || r.getPassenger() == null) {
                continue;
            }
            String existing = r.getPassenger().getName();
            if (existing != null && seen.contains(existing)) {
                return false;
            }
        }
        Booking booking = new Booking();
        booking.setCustomer(this);
        for (String name : listOfPassengerNames) {
            booking.createReservation(f, name, now);
        }
        this.getBookings().add(booking);
        return true;
    }
}
