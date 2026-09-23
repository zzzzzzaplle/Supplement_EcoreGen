import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Booking {
    private Customer customer;
    private List<Reservation> reservations;

    public Booking() {
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
        if (f == null || passenger == null || now == null || f.getDepartureTime() == null) {
            return false;
        }
        
        // Check that there are no duplicate passengers on the flight
        for (Reservation res : f.getReservations()) {
            if (res.getPassenger() != null && res.getPassenger().getName().equals(passenger)) {
                return false;
            }
        }
        
        // Check that current time is before flight departure time
        if (now.getTime() >= f.getDepartureTime().getTime()) {
            return false;
        }
        
        // Create a new reservation
        Reservation reservation = new Reservation();
        reservation.setFlight(f);
        reservation.setStatus(ReservationStatus.PENDING);
        
        Passenger psg = new Passenger();
        psg.setName(passenger);
        reservation.setPassenger(psg);
        
        // Generate a unique ID
        String id = "RES-" + (f.getReservations().size() + 1);
        reservation.setId(id);
        
        // Add to flight's reservations
        f.getReservations().add(reservation);
        
        // Add to booking's reservations
        getReservations().add(reservation);
        
        return true;
    }
}
