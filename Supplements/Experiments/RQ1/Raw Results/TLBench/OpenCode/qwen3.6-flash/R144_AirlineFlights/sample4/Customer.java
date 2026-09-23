import java.util.*;

public class Customer {
    private List<Booking> bookings;

    public Customer() {
        this.bookings = new ArrayList<>();
    }

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }

    public boolean confirm(String reservationID, Date now) {
        if (bookings == null) return false;
        for (Booking b : bookings) {
            if (b.getReservations() != null) {
                for (Reservation r : b.getReservations()) {
                    if (r.getId() != null && r.getId().equals(reservationID)) {
                        Flight f = r.getFlight();
                        if (f == null || !f.isOpenForBooking() || now.after(f.getDepartureTime())) {
                            return false;
                        }
                        r.setStatus(ReservationStatus.CONFIRMED);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        if (bookings == null) return false;
        for (Booking b : bookings) {
            if (b.getReservations() != null) {
                for (Reservation r : b.getReservations()) {
                    if (r.getId() != null && r.getId().equals(reservationID)) {
                        Flight f = r.getFlight();
                        if (f == null || !f.isOpenForBooking() || now.after(f.getDepartureTime())) {
                            return false;
                        }
                        r.setStatus(ReservationStatus.CANCELED);
                        return true;
                    }
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
        if (listOfPassengerNames == null || listOfPassengerNames.isEmpty()) {
            return false;
        }
        Set<String> seen = new HashSet<>();
        for (String name : listOfPassengerNames) {
            if (seen.contains(name)) {
                return false;
            }
            seen.add(name);
        }
        for (String name : listOfPassengerNames) {
            for (Reservation existing : f.getReservations()) {
                if (existing.getPassenger() != null && existing.getPassenger().getName() != null
                        && existing.getPassenger().getName().equals(name)) {
                    return false;
                }
            }
        }
        Booking b = new Booking();
        b.setCustomer(this);
        if (b.getReservations() == null) {
            b.setReservations(new ArrayList<>());
        }
        for (String name : listOfPassengerNames) {
            b.createReservation(f, name, now);
        }
        if (bookings == null) {
            bookings = new ArrayList<>();
        }
        bookings.add(b);
        return true;
    }
}
