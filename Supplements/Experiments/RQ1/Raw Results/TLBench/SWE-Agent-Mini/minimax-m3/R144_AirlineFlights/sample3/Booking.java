import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
        if (f == null || passenger == null) {
            return false;
        }
        if (!f.isOpenForBooking()) {
            return false;
        }
        if (f.getDepartureTime() != null && !now.before(f.getDepartureTime())) {
            return false;
        }
        if (f.getReservations() != null) {
            for (Reservation r : f.getReservations()) {
                if (r != null && r.getPassenger() != null
                        && passenger.equals(r.getPassenger().getName())) {
                    return false;
                }
            }
        }
        Passenger p = new Passenger();
        p.setName(passenger);
        Reservation r = new Reservation();
        r.setId(UUID.randomUUID().toString());
        r.setPassenger(p);
        r.setFlight(f);
        r.setStatus(ReservationStatus.PENDING);
        if (this.reservations == null) {
            this.reservations = new ArrayList<Reservation>();
        }
        this.reservations.add(r);
        if (f.getReservations() == null) {
            f.setReservations(new ArrayList<Reservation>());
        }
        f.getReservations().add(r);
        return true;
    }
}
