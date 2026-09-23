import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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

    public boolean confirm(String reservationID, Date now) {
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (reservationID.equals(r.getId())) {
                    Flight flight = r.getFlight();
                    if (flight == null || !flight.isOpenForBooking()) {
                        return false;
                    }
                    if (now.after(flight.getDepartureTime())) {
                        return false;
                    }
                    if (r.getStatus() == ReservationStatus.CANCELED) {
                        return false;
                    }
                    if (r.getStatus() == ReservationStatus.CONFIRMED) {
                        return true;
                    }
                    r.setStatus(ReservationStatus.CONFIRMED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (reservationID.equals(r.getId())) {
                    Flight flight = r.getFlight();
                    if (flight == null || !flight.isOpenForBooking()) {
                        return false;
                    }
                    if (now.after(flight.getDepartureTime())) {
                        return false;
                    }
                    if (r.getStatus() == ReservationStatus.CANCELED) {
                        return true;
                    }
                    r.setStatus(ReservationStatus.CANCELED);
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
        if (now.after(f.getDepartureTime())) {
            return false;
        }
        Set<String> seen = new HashSet<>();
        for (String name : listOfPassengerNames) {
            if (!seen.add(name)) {
                return false;
            }
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
