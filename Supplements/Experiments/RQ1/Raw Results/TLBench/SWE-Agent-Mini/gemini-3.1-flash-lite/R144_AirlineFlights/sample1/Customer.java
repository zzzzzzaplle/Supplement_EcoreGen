import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Customer {
    private List<Booking> bookings = new ArrayList<>();

    public Customer() {}
    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }

    public boolean confirm(String reservationID, Date now) {
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (r.getId().equals(reservationID)) {
                    Flight f = r.getFlight();
                    if (f.isOpenForBooking() && now.before(f.getDepartureTime())) {
                        r.setStatus(ReservationStatus.CONFIRMED);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (r.getId().equals(reservationID)) {
                    Flight f = r.getFlight();
                    if (f.isOpenForBooking() && now.before(f.getDepartureTime())) {
                        r.setStatus(ReservationStatus.CANCELED);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean addBooking(Flight f, Date now, List<String> passengerNames) {
        if (!f.isOpenForBooking() || !now.before(f.getDepartureTime())) return false;
        
        List<String> seen = new ArrayList<>();
        for (String name : passengerNames) {
            if (seen.contains(name)) return false;
            seen.add(name);
        }

        Booking b = new Booking();
        b.setCustomer(this);
        for (String name : passengerNames) {
            if (!b.createReservation(f, name, now)) return false;
        }
        bookings.add(b);
        return true;
    }
}
