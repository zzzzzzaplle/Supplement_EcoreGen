import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        if (f == null || passenger == null || now == null) return false;
        if (!f.isOpenForBooking()) return false;
        if (now.after(f.getDepartureTime())) return false;

        Passenger p = new Passenger();
        p.setName(passenger);

        Reservation res = new Reservation();
        res.setId(java.util.UUID.randomUUID().toString());
        res.setStatus(ReservationStatus.PENDING);
        res.setPassenger(p);
        res.setFlight(f);

        reservations.add(res);
        if (f.getReservations() != null) {
            f.getReservations().add(res);
        }
        return true;
    }
}
