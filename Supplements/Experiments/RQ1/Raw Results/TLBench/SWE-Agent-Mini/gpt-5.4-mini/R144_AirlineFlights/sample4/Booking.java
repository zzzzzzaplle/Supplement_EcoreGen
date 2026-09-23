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
        if (reservations == null) {
            reservations = new ArrayList<Reservation>();
        }
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public boolean createReservation(Flight f, String passenger, Date now) {
        if (f == null || passenger == null || now == null || !f.isOpenForBooking() || f.getDepartureTime() == null) {
            return false;
        }
        if (!now.before(f.getDepartureTime())) {
            return false;
        }
        Reservation r = new Reservation();
        r.setId("R-" + System.nanoTime());
        Passenger p = new Passenger();
        p.setName(passenger);
        r.setPassenger(p);
        r.setFlight(f);
        r.setStatus(ReservationStatus.PENDING);
        getReservations().add(r);
        f.getReservations().add(r);
        return true;
    }
}
