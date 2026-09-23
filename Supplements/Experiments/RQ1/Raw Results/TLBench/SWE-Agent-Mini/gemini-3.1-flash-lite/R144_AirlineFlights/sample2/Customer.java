import java.util.*;

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

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (!f.isOpenForBooking() || now.after(f.getDepartureTime())) return false;
        Set<String> unique = new HashSet<>();
        for (String p : listOfPassengerNames) {
            if (!unique.add(p)) return false;
        }
        for (Reservation r : f.getReservations()) {
            if (unique.contains(r.getPassenger().getName())) return false;
        }

        Booking b = new Booking();
        b.setCustomer(this);
        for (String name : listOfPassengerNames) {
            Reservation r = new Reservation();
            Passenger p = new Passenger();
            p.setName(name);
            r.setPassenger(p);
            r.setFlight(f);
            r.setStatus(ReservationStatus.PENDING);
            r.setId(UUID.randomUUID().toString());
            b.getReservations().add(r);
        }
        bookings.add(b);
        return true;
    }
}
