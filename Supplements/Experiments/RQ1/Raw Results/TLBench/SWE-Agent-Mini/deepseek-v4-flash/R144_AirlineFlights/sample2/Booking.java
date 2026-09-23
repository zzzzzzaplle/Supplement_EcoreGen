import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

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

    public boolean createReservation(Flight f, String passengerName, Date now) {
        if (f == null || passengerName == null || now == null) return false;
        if (!f.isOpenForBooking()) return false;
        if (f.getDepartureTime() == null || !now.before(f.getDepartureTime())) return false;
        
        Passenger passenger = new Passenger();
        passenger.setName(passengerName);
        
        Reservation reservation = new Reservation();
        reservation.setId(UUID.randomUUID().toString());
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setPassenger(passenger);
        reservation.setFlight(f);
        
        if (this.reservations == null) this.reservations = new ArrayList<>();
        this.reservations.add(reservation);
        
        if (f.getReservations() == null) {
            f.setReservations(new ArrayList<>());
        }
        f.getReservations().add(reservation);
        
        return true;
    }
}
