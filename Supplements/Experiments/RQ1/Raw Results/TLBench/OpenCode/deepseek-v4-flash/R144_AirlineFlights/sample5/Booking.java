import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
        if (f == null || passenger == null || now == null) {
            return false;
        }
        for (Reservation r : reservations) {
            if (r.getPassenger().getName().equals(passenger)) {
                return false;
            }
        }
        if (!now.before(f.getDepartureTime())) {
            return false;
        }
        if (!f.isOpenForBooking()) {
            return false;
        }
        Passenger p = new Passenger();
        p.setName(passenger);
        Reservation r = new Reservation();
        r.setId(UUID.randomUUID().toString());
        r.setPassenger(p);
        r.setFlight(f);
        r.setStatus(ReservationStatus.PENDING);
        reservations.add(r);
        return true;
    }
}
