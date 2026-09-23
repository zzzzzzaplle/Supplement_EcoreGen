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
        if (f == null || passenger == null || now == null || customer == null) {
            return false;
        }
        for (Reservation r : reservations) {
            if (r != null && r.getPassenger() != null && r.getPassenger().getName() != null) {
                if (r.getPassenger().getName().equals(passenger)) {
                    return false;
                }
            }
        }
        Reservation r = new Reservation();
        Long baseId = Flight.generateId();
        r.setId(baseId + "-" + reservations.size());
        Passenger p = new Passenger();
        p.setName(passenger);
        r.setPassenger(p);
        r.setFlight(f);
        r.setStatus(ReservationStatus.PENDING);
        reservations.add(r);
        return true;
    }
}
