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
        if (f == null || passenger == null || passenger.isEmpty()) {
            return false;
        }
        if (this.reservations == null) {
            this.reservations = new ArrayList<Reservation>();
        }
        if (!f.isOpenForBooking()) {
            return false;
        }
        if (now != null && f.getDepartureTime() != null && !now.before(f.getDepartureTime())) {
            return false;
        }
        for (Reservation r : this.reservations) {
            if (r != null && r.getFlight() != null && r.getFlight().equals(f)
                    && r.getPassenger() != null && passenger.equals(r.getPassenger().getName())) {
                return false;
            }
        }
        Reservation res = new Reservation();
        res.setId(UUID.randomUUID().toString());
        res.setFlight(f);
        res.setStatus(ReservationStatus.PENDING);
        Passenger p = new Passenger();
        p.setName(passenger);
        res.setPassenger(p);
        this.reservations.add(res);
        if (f.getReservations() == null) {
            f.setReservations(new ArrayList<Reservation>());
        }
        f.getReservations().add(res);
        return true;
    }
}
