import java.util.Date;
import java.util.List;
import java.util.ArrayList;

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
        if (f == null || passenger == null || now == null) return false;
        if (f.getDepartureTime() == null) return false;
        if (!now.before(f.getDepartureTime())) return false;
        if (!f.isOpenForBooking()) return false;
        if (reservations == null) {
            reservations = new ArrayList<Reservation>();
        }
        for (Reservation r : reservations) {
            if (r.getPassenger() != null && passenger.equals(r.getPassenger().getName())) {
                return false;
            }
        }
        for (Reservation r : f.getReservations()) {
            if (r.getPassenger() != null && passenger.equals(r.getPassenger().getName())) {
                return false;
            }
        }
        Passenger p = new Passenger();
        p.setName(passenger);
        Reservation res = new Reservation();
        res.setId(java.util.UUID.randomUUID().toString());
        res.setPassenger(p);
        res.setFlight(f);
        res.setStatus(ReservationStatus.PENDING);
        reservations.add(res);
        if (f.getReservations() == null) {
            f.setReservations(new ArrayList<Reservation>());
        }
        f.getReservations().add(res);
        return true;
    }
}
