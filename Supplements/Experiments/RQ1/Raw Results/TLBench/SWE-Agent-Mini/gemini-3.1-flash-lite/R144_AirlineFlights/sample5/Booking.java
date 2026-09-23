import java.util.*;

public class Booking {
    private Customer customer;
    private List<Reservation> reservations = new ArrayList<>();

    public Booking() {}

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public List<Reservation> getReservations() { return reservations; }
    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }

    public boolean createReservation(Flight f, String passengerName, Date now) {
        Reservation r = new Reservation();
        r.setId(UUID.randomUUID().toString());
        r.setStatus(ReservationStatus.PENDING);
        r.setFlight(f);
        Passenger p = new Passenger();
        p.setName(passengerName);
        r.setPassenger(p);
        
        this.reservations.add(r);
        f.getReservations().add(r);
        return true;
    }
}
