import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class Booking {
    private Customer customer;
    private List<Reservation> reservations;

    private static long reservationCounter = 0;

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
        if (f == null || passenger == null || now == null) {
            return false;
        }
        if (!f.isOpenForBooking()) {
            return false;
        }
        Date departureTime = f.getDepartureTime();
        if (departureTime == null) {
            return false;
        }
        if (now.after(departureTime)) {
            return false;
        }
        Passenger p = new Passenger();
        p.setName(passenger);
        Reservation r = new Reservation();
        reservationCounter++;
        r.setId("RES-" + reservationCounter);
        r.setPassenger(p);
        r.setFlight(f);
        r.setStatus(ReservationStatus.PENDING);
        reservations.add(r);
        f.getReservations().add(r);
        return true;
    }
}
