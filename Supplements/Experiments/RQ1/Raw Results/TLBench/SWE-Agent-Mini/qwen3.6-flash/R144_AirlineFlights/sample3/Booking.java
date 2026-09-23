import java.util.*;

public class Booking {
    private Customer customer;
    private List<Reservation> reservations;
    private static int bookingCounter = 0;

    public Booking() {
        this.reservations = new ArrayList<>();
        Booking.bookingCounter++;
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
        if (f == null || passenger == null || passenger.isEmpty() || now == null) {
            return false;
        }
        // Check that the flight is open for booking
        if (!f.isOpenForBooking()) {
            return false;
        }
        // Check current time is before flight departure time
        if (!now.before(f.getDepartureTime())) {
            return false;
        }
        // Check there are no duplicate passengers already on this flight from this booking
        for (Reservation existing : reservations) {
            if (existing != null && existing.getPassenger() != null) {
                if (passenger.equals(existing.getPassenger().getName())) {
                    return false;
                }
            }
        }
        // Create a reservation with a unique ID
        Reservation res = new Reservation();
        res.setId("RES-" + System.nanoTime() + "-" + Booking.bookingCounter);
        res.setStatus(ReservationStatus.PENDING);
        Passenger p = new Passenger();
        p.setName(passenger);
        res.setPassenger(p);
        res.setFlight(f);
        reservations.add(res);
        return true;
    }
}
