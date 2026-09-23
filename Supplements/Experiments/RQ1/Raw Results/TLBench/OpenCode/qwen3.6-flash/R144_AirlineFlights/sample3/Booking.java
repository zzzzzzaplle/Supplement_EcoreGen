import java.util.Date;
import java.util.List;

public class Booking {
    private Customer customer;
    private List<Reservation> reservations;

    public Booking() {
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
        if (f == null || f.isOpenForBooking() == false) {
            return false;
        }
        if (f.getDepartureTime().before(now)) {
            return false;
        }
        Reservation reservation = new Reservation();
        reservation.setId("R-" + System.currentTimeMillis() + "-" + passenger);
        reservation.setStatus(ReservationStatus.PENDING);
        Passenger p = new Passenger();
        p.setName(passenger);
        reservation.setPassenger(p);
        reservation.setFlight(f);
        reservations.add(reservation);
        f.getReservations().add(reservation);
        return true;
    }
}
