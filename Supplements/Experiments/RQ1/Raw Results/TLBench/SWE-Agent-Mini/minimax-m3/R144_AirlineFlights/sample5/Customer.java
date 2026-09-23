import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
        if (reservationID == null || this.bookings == null) {
            return false;
        }
        for (Booking b : this.bookings) {
            if (b == null || b.getReservations() == null) {
                continue;
            }
            for (Reservation r : b.getReservations()) {
                if (r != null && reservationID.equals(r.getId())) {
                    if (!r.getStatus().equals(ReservationStatus.PENDING)) {
                        return false;
                    }
                    Flight f = r.getFlight();
                    if (f == null || !f.isOpenForBooking()) {
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
        if (reservationID == null || this.bookings == null) {
            return false;
        }
        for (Booking b : this.bookings) {
            if (b == null || b.getReservations() == null) {
                continue;
            }
            for (Reservation r : b.getReservations()) {
                if (r != null && reservationID.equals(r.getId())) {
                    Flight f = r.getFlight();
                    if (f == null || !f.isOpenForBooking()) {
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
        if (f == null || now == null || listOfPassengerNames == null) {
            return false;
        }
        if (!f.isOpenForBooking()) {
            return false;
        }
        if (f.getDepartureTime() != null && !now.before(f.getDepartureTime())) {
            return false;
        }
        for (String name : listOfPassengerNames) {
            if (name == null) {
                continue;
            }
            if (f.getReservations() != null) {
                for (Reservation existing : f.getReservations()) {
                    if (existing != null && existing.getPassenger() != null
                            && name.equals(existing.getPassenger().getName())
                            && existing.getStatus() != ReservationStatus.CANCELED) {
                        return false;
                    }
                }
            }
        }
        Booking booking = new Booking();
        booking.setCustomer(this);
        for (String name : listOfPassengerNames) {
            if (name == null) {
                continue;
            }
            booking.createReservation(f, name, now);
        }
        if (this.bookings == null) {
            this.bookings = new ArrayList<Booking>();
        }
        this.bookings.add(booking);
        return true;
    }
}
