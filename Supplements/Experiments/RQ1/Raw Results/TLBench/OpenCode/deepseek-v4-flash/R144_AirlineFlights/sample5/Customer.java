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
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (r.getId().equals(reservationID)) {
                    Flight f = r.getFlight();
                    if (f == null) {
                        return false;
                    }
                    if (!now.before(f.getDepartureTime())) {
                        return false;
                    }
                    if (!f.isOpenForBooking()) {
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
        if (reservationID == null || now == null) {
            return false;
        }
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (r.getId().equals(reservationID)) {
                    Flight f = r.getFlight();
                    if (f == null) {
                        return false;
                    }
                    if (!now.before(f.getDepartureTime())) {
                        return false;
                    }
                    if (!f.isOpenForBooking()) {
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
        if (f == null || now == null || listOfPassengerNames == null || listOfPassengerNames.isEmpty()) {
            return false;
        }
        if (!f.isOpenForBooking()) {
            return false;
        }
        if (!now.before(f.getDepartureTime())) {
            return false;
        }
        List<String> duplicates = new ArrayList<>();
        for (String name : listOfPassengerNames) {
            for (Reservation r : f.getReservations()) {
                if (r.getPassenger().getName().equals(name)) {
                    duplicates.add(name);
                }
            }
        }
        if (!duplicates.isEmpty()) {
            return false;
        }
        Booking booking = new Booking();
        booking.setCustomer(this);
        for (String name : listOfPassengerNames) {
            boolean created = booking.createReservation(f, name, now);
            if (!created) {
                return false;
            }
        }
        bookings.add(booking);
        return true;
    }
}
