import java.util.*;

public class Booking {
    private Customer customer;
    private List<Reservation> reservations;

    public Booking() {}

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public List<Reservation> getReservations() { return reservations; }
    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }

    public boolean createReservation(Flight f, String passenger, Date now) {
        if (reservations == null) {
            reservations = new ArrayList<>();
        }
        if (!f.isOpenForBooking()) {
            return false;
        }
        if (now.after(f.getDepartureTime())) {
            return false;
        }
        Reservation r = new Reservation();
        r.setId(UUID.randomUUID().toString());
        r.setStatus(ReservationStatus.PENDING);
        Passenger p = new Passenger();
        p.setName(passenger);
        r.setPassenger(p);
        r.setFlight(f);
        f.getReservations().add(r);
        reservations.add(r);
        return true;
    }
}
