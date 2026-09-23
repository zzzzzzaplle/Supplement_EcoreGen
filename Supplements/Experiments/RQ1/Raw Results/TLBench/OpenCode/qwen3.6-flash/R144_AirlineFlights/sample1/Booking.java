import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Booking {

    private Customer customer;
    private List<Reservation> reservations = new ArrayList<>();

    public Booking() {}

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

    public boolean createReservation(Flight f, String passengerName, Timestamp now) {
        if (f == null || passengerName == null || passengerName.trim().isEmpty()) {
            return false;
        }
        Passenger p = new Passenger();
        p.setName(passengerName);
        Reservation reservation = new Reservation();
        reservation.setPassenger(p);
        reservation.setFlight(f);
        reservation.setStatus(ReservationStatus.PENDING);
        boolean added = f.addReservation(reservation);
        if (added) {
            this.reservations.add(reservation);
        }
        return added;
    }
}
