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
        if (!f.isOpenForBooking() || now.after(f.getDepartureTime())) {
            return false;
        }
        for (Reservation r : f.getReservations()) {
            if (r.getPassenger().getName().equals(passengerName)) {
                return false;
            }
        }
        
        Reservation res = new Reservation();
        res.setFlight(f);
        Passenger p = new Passenger();
        p.setName(passengerName);
        res.setPassenger(p);
        res.setStatus(ReservationStatus.PENDING);
        // Reservation unique ID generation is required but not specified how, using hashcode/timestamp for now
        res.setId(System.nanoTime() + ""); 
        
        this.reservations.add(res);
        f.getReservations().add(res);
        return true;
    }
}
