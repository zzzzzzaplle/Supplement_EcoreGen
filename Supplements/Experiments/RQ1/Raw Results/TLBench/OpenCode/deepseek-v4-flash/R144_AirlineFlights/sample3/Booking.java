import java.util.List;
import java.util.ArrayList;
import java.util.Date;
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
        if (f.getDepartureTime().before(now)) {
            return false;
        }
        for (Reservation r : reservations) {
            if (r.getPassenger().getName().equals(passenger)) {
                return false;
            }
        }
        Reservation res = new Reservation();
        res.setId(UUID.randomUUID().toString());
        res.setStatus(ReservationStatus.PENDING);
        Passenger p = new Passenger();
        p.setName(passenger);
        res.setPassenger(p);
        res.setFlight(f);
        reservations.add(res);
        f.getReservations().add(res);
        return true;
    }
}
