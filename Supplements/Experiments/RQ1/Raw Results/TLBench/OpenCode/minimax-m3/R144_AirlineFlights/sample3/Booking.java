import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Booking {
    private Customer customer;
    private List<Reservation> reservations;
    private static AtomicLong counter = new AtomicLong(0);

    public Booking() {
        this.reservations = new ArrayList<Reservation>();
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Reservation> getReservations() {
        if (this.reservations == null) {
            this.reservations = new ArrayList<Reservation>();
        }
        return this.reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public boolean createReservation(Flight f, String passenger, Date now) {
        if (f == null) {
            return false;
        }
        if (passenger == null) {
            return false;
        }
        Reservation r = new Reservation();
        r.setId("RES-" + counter.incrementAndGet());
        r.setFlight(f);
        r.setStatus(ReservationStatus.PENDING);
        Passenger p = new Passenger();
        p.setName(passenger);
        r.setPassenger(p);
        this.getReservations().add(r);
        f.getReservations().add(r);
        return true;
    }
}
