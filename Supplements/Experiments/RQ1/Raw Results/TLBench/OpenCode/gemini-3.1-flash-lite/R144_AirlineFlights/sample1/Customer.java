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
                    if (f.isOpenForBooking() && f.getDepartureTime().after(now)) {
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
                    if (f.isOpenForBooking() && f.getDepartureTime().after(now)) {
                        r.setStatus(ReservationStatus.CANCELED);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (!f.isOpenForBooking() || f.getDepartureTime().before(now)) return false;
        
        Booking b = new Booking();
        b.setCustomer(this);
        List<Reservation> res = new ArrayList<>();
        
        for (String name : listOfPassengerNames) {
            for (Reservation r : f.getReservations()) {
                if (r.getPassenger().getName().equals(name)) return false;
            }
            Reservation r = new Reservation();
            r.setId(java.util.UUID.randomUUID().toString());
            r.setPassenger(new Passenger(name));
            r.setFlight(f);
            r.setStatus(ReservationStatus.PENDING);
            res.add(r);
            f.getReservations().add(r);
        }
        b.setReservations(res);
        bookings.add(b);
        return true;
    }
}
