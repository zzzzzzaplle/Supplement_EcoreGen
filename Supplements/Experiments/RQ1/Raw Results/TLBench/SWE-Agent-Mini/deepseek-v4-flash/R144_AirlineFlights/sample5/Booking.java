import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Booking {
    private Customer customer;
    private List<Reservation> reservations;
    
    public Booking() {
        this.reservations = new ArrayList<>();
    }
    
    public Booking(Customer customer) {
        this.customer = customer;
        this.reservations = new ArrayList<>();
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
        if (!f.isOpenForBooking()) return false;
        if (now.after(f.getDepartureTime()) || now.equals(f.getDepartureTime())) return false;
        
        // Check for duplicate passenger on the flight
        List<Reservation> flightReservations = f.getReservations();
        if (flightReservations != null) {
            for (Reservation r : flightReservations) {
                if (r.getPassenger() != null && r.getPassenger().getName().equals(passenger)) {
                    return false;
                }
            }
        }
        
        Passenger p = new Passenger(passenger);
        String uniqueId = UUID.randomUUID().toString();
        Reservation reservation = new Reservation(uniqueId, p, f);
        reservation.setStatus(ReservationStatus.PENDING);
        
        if (reservations == null) {
            reservations = new ArrayList<>();
        }
        reservations.add(reservation);
        
        if (f.getReservations() == null) {
            f.setReservations(new ArrayList<>());
        }
        f.getReservations().add(reservation);
        
        return true;
    }
}
