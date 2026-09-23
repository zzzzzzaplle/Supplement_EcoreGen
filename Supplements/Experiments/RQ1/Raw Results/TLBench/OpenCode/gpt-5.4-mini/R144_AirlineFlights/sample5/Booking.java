import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public boolean createReservation(Flight f, String passenger, Date now) {
        if (f == null || passenger == null || now == null || !f.isOpenForBooking() || f.getDepartureTime() == null || !now.before(f.getDepartureTime())) {
            return false;
        }
        for (Reservation reservation : reservations) {
            if (reservation != null && reservation.getPassenger() != null && passenger.equals(reservation.getPassenger().getName())) {
                return false;
            }
        }
        Passenger p = new Passenger();
        p.setName(passenger);
        Reservation reservation = new Reservation();
        reservation.setId(UniqueIdGenerator.nextId());
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setPassenger(p);
        reservation.setFlight(f);
        reservations.add(reservation);
        f.getReservations().add(reservation);
        return true;
    }
}
