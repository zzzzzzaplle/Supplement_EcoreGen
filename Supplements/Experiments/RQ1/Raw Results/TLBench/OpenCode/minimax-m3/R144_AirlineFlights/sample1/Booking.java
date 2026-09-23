import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Booking {
    private String id;
    private Customer customer;
    private List<Reservation> reservations;

    public Booking() {
        this.id = UUID.randomUUID().toString();
        this.reservations = new ArrayList<Reservation>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        if (f == null || passenger == null || passenger.trim().isEmpty()) {
            return false;
        }
        if (now != null && f.getDepartureTime() != null
                && now.compareTo(f.getDepartureTime()) >= 0) {
            return false;
        }
        if (!f.isOpenForBooking()) {
            return false;
        }
        for (Reservation existing : f.getReservations()) {
            if (existing == null || existing.getPassenger() == null) {
                continue;
            }
            if (existing.getStatus() == ReservationStatus.CANCELED) {
                continue;
            }
            if (passenger.equalsIgnoreCase(existing.getPassenger().getName())) {
                return false;
            }
        }
        Passenger p = new Passenger(passenger);
        Reservation r = new Reservation();
        r.setPassenger(p);
        r.setFlight(f);
        r.setStatus(ReservationStatus.PENDING);
        f.getReservations().add(r);
        if (this.reservations == null) {
            this.reservations = new ArrayList<Reservation>();
        }
        this.reservations.add(r);
        return true;
    }
}
