import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Booking {
    private Customer customer;
    private List<Reservation> reservations;

    public Booking() {
        this.customer = null;
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
        if (f == null || !f.isOpenForBooking()) {
            return false;
        }

        if (now != null && now.getTime() >= f.getDepartureTime().getTime()) {
            return false;
        }

        Reservation res = new Reservation();
        res.setId(UUID.randomUUID().toString());
        res.setStatus(ReservationStatus.PENDING);

        Passenger p = new Passenger();
        p.setName(passenger);
        res.setPassenger(p);
        res.setFlight(f);

        this.reservations.add(res);
        res.getFlight().getReservations().add(res);

        if (customer != null) {
            customer.getBookings().add(this);
        }

        return true;
    }
}
