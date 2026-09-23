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
        if (f == null || passenger == null) {
            return false;
        }
        if (!f.isOpenForBooking()) {
            return false;
        }
        if (now != null && f.getDepartureTime() != null && !now.before(f.getDepartureTime())) {
            return false;
        }
        if (this.reservations == null) {
            this.reservations = new ArrayList<Reservation>();
        }
        for (Reservation r : this.reservations) {
            if (r != null && r.getFlight() != null && r.getFlight().getId() != null
                    && r.getFlight().getId().equals(f.getId())) {
                if (r.getPassenger() != null && passenger.equals(r.getPassenger().getName())) {
                    return false;
                }
            }
        }
        Reservation r = new Reservation();
        r.setId(java.util.UUID.randomUUID().toString());
        r.setStatus(ReservationStatus.PENDING);
        Passenger p = new Passenger();
        p.setName(passenger);
        r.setPassenger(p);
        r.setFlight(f);
        this.reservations.add(r);
        if (f.getReservations() == null) {
            f.setReservations(new ArrayList<Reservation>());
        }
        if (!f.getReservations().contains(r)) {
            f.getReservations().add(r);
        }
        return true;
    }
}
