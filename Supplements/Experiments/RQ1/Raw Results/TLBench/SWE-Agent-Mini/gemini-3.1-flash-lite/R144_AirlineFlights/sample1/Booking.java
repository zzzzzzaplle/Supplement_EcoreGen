import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Booking {
    private Customer customer;
    private List<Reservation> reservations = new ArrayList<>();

    public Booking() {}
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public List<Reservation> getReservations() { return reservations; }
    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }

    public boolean createReservation(Flight f, String passengerName, Date now) {
        if (f.getDepartureTime() == null || !now.before(f.getDepartureTime())) return false;
        for (Reservation r : reservations) {
            if (r.getPassenger().getName().equals(passengerName)) return false;
        }
        Reservation r = new Reservation();
        r.setId(java.util.UUID.randomUUID().toString());
        r.setFlight(f);
        Passenger p = new Passenger();
        p.setName(passengerName);
        r.setPassenger(p);
        r.setStatus(ReservationStatus.PENDING);
        reservations.add(r);
        f.getReservations().add(r);
        return true;
    }
}
