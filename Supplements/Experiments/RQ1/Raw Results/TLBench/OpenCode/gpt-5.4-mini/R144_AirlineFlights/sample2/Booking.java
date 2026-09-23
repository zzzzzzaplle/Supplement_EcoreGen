import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Booking {
    private Customer customer;
    private List<Reservation> reservations;

    public Booking() {
        this.reservations = new ArrayList<Reservation>();
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
        if (f == null || passenger == null || now == null || f.getDepartureTime() == null || !now.before(f.getDepartureTime())) {
            return false;
        }
        Reservation reservation = new Reservation();
        reservation.setId(java.util.UUID.randomUUID().toString());
        Passenger p = new Passenger();
        p.setName(passenger);
        reservation.setPassenger(p);
        reservation.setFlight(f);
        reservation.setStatus(ReservationStatus.PENDING);
        if (reservations == null) {
            reservations = new ArrayList<Reservation>();
        }
        reservations.add(reservation);
        if (f.getReservations() != null) {
            f.getReservations().add(reservation);
        }
        return true;
    }
}
