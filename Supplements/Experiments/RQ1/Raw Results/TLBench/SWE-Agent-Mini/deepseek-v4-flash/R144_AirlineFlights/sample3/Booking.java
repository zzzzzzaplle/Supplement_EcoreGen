import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

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

    public boolean createReservation(Flight f, String passengerName, Date now) {
        if (f == null || passengerName == null || now == null) {
            return false;
        }
        if (!f.isOpenForBooking()) {
            return false;
        }
        if (f.getDepartureTime() != null && !now.before(f.getDepartureTime())) {
            return false;
        }
        // Check for duplicate passenger name on this flight
        List<Reservation> existingReservations = f.getReservations();
        if (existingReservations != null) {
            for (Reservation r : existingReservations) {
                if (r.getPassenger() != null && passengerName.equals(r.getPassenger().getName())) {
                    return false;
                }
            }
        }
        // Check in this booking's reservations for same flight
        if (reservations != null) {
            for (Reservation r : reservations) {
                if (r.getFlight() != null && r.getFlight().getId() != null && r.getFlight().getId().equals(f.getId())) {
                    if (r.getPassenger() != null && passengerName.equals(r.getPassenger().getName())) {
                        return false;
                    }
                }
            }
        }
        Passenger passenger = new Passenger();
        passenger.setName(passengerName);
        
        Reservation reservation = new Reservation();
        reservation.setId(UUID.randomUUID().toString());
        reservation.setPassenger(passenger);
        reservation.setFlight(f);
        reservation.setStatus(ReservationStatus.PENDING);
        
        if (reservations == null) {
            reservations = new ArrayList<Reservation>();
        }
        reservations.add(reservation);
        
        if (f.getReservations() == null) {
            f.setReservations(new ArrayList<Reservation>());
        }
        f.getReservations().add(reservation);
        
        return true;
    }
}
