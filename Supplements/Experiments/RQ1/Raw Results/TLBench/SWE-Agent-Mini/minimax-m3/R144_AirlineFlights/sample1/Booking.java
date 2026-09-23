import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        if (f == null || passenger == null) return false;
        if (!f.isOpenForBooking()) return false;
        if (now == null || f.getDepartureTime() == null) return false;
        if (!now.before(f.getDepartureTime())) return false;
        List<Reservation> existing = f.getReservations();
        if (existing != null) {
            for (Reservation r : existing) {
                if (r != null && r.getPassenger() != null
                        && passenger.equals(r.getPassenger().getName())) {
                    return false;
                }
            }
        }
        Passenger p = new Passenger();
        p.setName(passenger);
        Reservation r = new Reservation();
        r.setPassenger(p);
        r.setFlight(f);
        r.setStatus(ReservationStatus.PENDING);
        if (f.getReservations() == null) {
            f.setReservations(new ArrayList<Reservation>());
        }
        f.getReservations().add(r);
        if (this.reservations == null) {
            this.reservations = new ArrayList<Reservation>();
        }
        this.reservations.add(r);
        return true;
    }
}
