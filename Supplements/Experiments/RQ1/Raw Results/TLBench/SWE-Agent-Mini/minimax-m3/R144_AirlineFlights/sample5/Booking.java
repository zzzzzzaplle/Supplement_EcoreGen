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
        if (f == null || passenger == null || now == null) {
            return false;
        }
        if (!f.isOpenForBooking()) {
            return false;
        }
        if (f.getDepartureTime() != null && !now.before(f.getDepartureTime())) {
            return false;
        }
        if (this.reservations != null) {
            for (Reservation r : this.reservations) {
                if (r != null && r.getPassenger() != null
                        && passenger.equals(r.getPassenger().getName())
                        && r.getStatus() != ReservationStatus.CANCELED) {
                    return false;
                }
            }
        }
        if (f.getReservations() != null) {
            for (Reservation existing : f.getReservations()) {
                if (existing != null && existing.getPassenger() != null
                        && passenger.equals(existing.getPassenger().getName())
                        && existing.getStatus() != ReservationStatus.CANCELED) {
                    return false;
                }
            }
        }
        Reservation r = new Reservation();
        r.setId(UUID.randomUUID().toString());
        Passenger p = new Passenger();
        p.setName(passenger);
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
