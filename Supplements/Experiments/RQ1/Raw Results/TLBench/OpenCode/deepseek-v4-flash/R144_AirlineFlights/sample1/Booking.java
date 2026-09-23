import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

public class Booking {
    private Customer customer;
    private List<Reservation> reservations;

    public Booking() {
        this.reservations = new ArrayList<>();
    }

    public Booking(Customer customer) {
        this.customer = customer;
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
        if (f == null || passenger == null || now == null) return false;
        if (!f.isOpenForBooking()) return false;
        if (!now.before(f.getDepartureTime())) return false;

        for (Reservation r : f.getReservations()) {
            if (r.getPassenger() != null && passenger.equals(r.getPassenger().getName())) {
                return false;
            }
        }

        String uniqueId = UUID.randomUUID().toString();
        Passenger p = new Passenger(passenger);
        Reservation res = new Reservation(uniqueId, p, f);
        res.setStatus(ReservationStatus.PENDING);
        reservations.add(res);
        f.getReservations().add(res);
        return true;
    }
}
