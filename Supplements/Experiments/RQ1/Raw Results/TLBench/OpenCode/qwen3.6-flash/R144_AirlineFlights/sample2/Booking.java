import java.util.*;

public class Booking {
    private Customer customer;
    private List<Reservation> reservations;

    public Booking() {
        this.reservations = new ArrayList<>();
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public boolean createReservation(Flight f, String passenger, Date now) {
        if (f.isOpenForBooking() && f.getReservations() != null && now.before(f.getDepartureTime())) {
            Reservation res = new Reservation();
            res.setId(UUID.randomUUID().toString());
            ReservationStatus initialStatus = ReservationStatus.PENDING;
            res.setStatus(initialStatus);
            Passenger p = new Passenger();
            p.setName(passenger);
            res.setPassenger(p);
            res.setFlight(f);
            reservations.add(res);
            return true;
        }
        return false;
    }
}
