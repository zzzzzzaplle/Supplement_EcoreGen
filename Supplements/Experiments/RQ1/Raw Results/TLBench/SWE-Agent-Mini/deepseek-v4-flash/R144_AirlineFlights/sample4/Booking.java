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
        if (!now.before(f.getDepartureTime())) return false;
        
        // Check for duplicate passenger on the same flight
        List<Reservation> flightReservations = f.getReservations();
        if (flightReservations != null) {
            for (Reservation r : flightReservations) {
                if (r.getPassenger() != null && r.getPassenger().getName() != null &&
                    r.getPassenger().getName().equals(passengerName)) {
                    return false;
                }
            }
        }
        
        // Generate unique reservation ID
        String reservationId = UUID.randomUUID().toString();
        
        Passenger passenger = new Passenger();
        passenger.setName(passengerName);
        
        Reservation reservation = new Reservation();
        reservation.setId(reservationId);
        reservation.setPassenger(passenger);
        reservation.setFlight(f);
        reservation.setStatus(ReservationStatus.PENDING);
        
        if (f.getReservations() == null) {
            f.setReservations(new ArrayList<>());
        }
        f.getReservations().add(reservation);
        
        if (this.reservations == null) {
            this.reservations = new ArrayList<>();
        }
        this.reservations.add(reservation);
        
        return true;
    }
}
