import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Booking {
    private Customer customer;
    private List<Reservation> reservations;
    private static long counter = 1L;

    public Booking() {
        this.reservations = new ArrayList<Reservation>();
    }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public List<Reservation> getReservations() { return reservations; }
    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }

    public boolean createReservation(Flight f, String passenger, Date now) {
        if (f == null || passenger == null || now == null || !f.isOpenForBooking() || !now.before(f.getDepartureTime())) {
            return false;
        }
        if (reservations == null) {
            reservations = new ArrayList<Reservation>();
        }
        for (Reservation r : reservations) {
            if (r != null && r.getPassenger() != null && passenger.equals(r.getPassenger().getName())) {
                return false;
            }
        }
        Passenger p = new Passenger();
        p.setName(passenger);
        Reservation r = new Reservation();
        r.setId("R" + (counter++));
        r.setStatus(ReservationStatus.PENDING);
        r.setPassenger(p);
        r.setFlight(f);
        reservations.add(r);
        if (f.getReservations() != null) {
            f.getReservations().add(r);
        }
        return true;
    }
}
