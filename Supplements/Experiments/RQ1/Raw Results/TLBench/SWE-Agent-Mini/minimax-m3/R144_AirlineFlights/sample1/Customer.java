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
        if (reservationID == null || now == null) return false;
        if (bookings == null) return false;
        for (Booking b : bookings) {
            if (b == null || b.getReservations() == null) continue;
            for (Reservation r : b.getReservations()) {
                if (r != null && reservationID.equals(r.getId())) {
                    Flight f = r.getFlight();
                    if (f == null) return false;
                    if (!f.isOpenForBooking()) return false;
                    if (f.getDepartureTime() == null || !now.before(f.getDepartureTime())) return false;
                    if (r.getStatus() == ReservationStatus.CANCELED) return false;
                    r.setStatus(ReservationStatus.CONFIRMED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        if (reservationID == null || now == null) return false;
        if (bookings == null) return false;
        for (Booking b : bookings) {
            if (b == null || b.getReservations() == null) continue;
            for (Reservation r : b.getReservations()) {
                if (r != null && reservationID.equals(r.getId())) {
                    Flight f = r.getFlight();
                    if (f == null) return false;
                    if (!f.isOpenForBooking()) return false;
                    if (f.getDepartureTime() == null || !now.before(f.getDepartureTime())) return false;
                    if (r.getStatus() == ReservationStatus.CANCELED) return false;
                    r.setStatus(ReservationStatus.CANCELED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (f == null || now == null || listOfPassengerNames == null) return false;
        if (!f.isOpenForBooking()) return false;
        if (f.getDepartureTime() == null || !now.before(f.getDepartureTime())) return false;
        if (listOfPassengerNames.isEmpty()) return false;
        java.util.Set<String> uniqueNames = new java.util.HashSet<String>();
        for (String name : listOfPassengerNames) {
            if (name == null) return false;
            if (!uniqueNames.add(name)) return false;
        }
        List<Reservation> existing = f.getReservations();
        if (existing != null) {
            for (Reservation r : existing) {
                if (r == null || r.getPassenger() == null) continue;
                String existingName = r.getPassenger().getName();
                if (existingName != null && listOfPassengerNames.contains(existingName)) {
                    return false;
                }
            }
        }
        Booking b = new Booking();
        b.setCustomer(this);
        for (String name : listOfPassengerNames) {
            if (!b.createReservation(f, name, now)) {
                return false;
            }
        }
        if (bookings == null) {
            bookings = new ArrayList<Booking>();
        }
        bookings.add(b);
        return true;
    }
}
