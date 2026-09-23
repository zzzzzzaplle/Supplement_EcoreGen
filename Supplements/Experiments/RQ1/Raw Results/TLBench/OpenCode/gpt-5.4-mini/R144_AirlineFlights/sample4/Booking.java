import java.util.ArrayList;
import java.util.List;
import java.util.Date;

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
        if (f == null || passenger == null || now == null || !f.isOpenForBooking() || !now.before(f.getDepartureTime())) {
            return false;
        }
        Passenger p = new Passenger();
        p.setName(passenger);
        Reservation reservation = new Reservation();
        reservation.setId(java.util.UUID.randomUUID().toString());
        reservation.setPassenger(p);
        reservation.setFlight(f);
        reservation.setStatus(ReservationStatus.PENDING);
        reservations.add(reservation);
        f.getReservations().add(reservation);
        return true;
    }
}
