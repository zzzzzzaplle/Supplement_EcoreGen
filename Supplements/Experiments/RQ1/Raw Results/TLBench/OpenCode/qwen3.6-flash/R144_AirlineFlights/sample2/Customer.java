import java.util.*;

public class Customer {
    private List<Booking> bookings;

    public Customer() {
        this.bookings = new ArrayList<>();
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public boolean confirm(String reservationID, Date now) {
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (r.getId().equals(reservationID)) {
                    if (now.before(r.getFlight().getDepartureTime()) && r.getFlight().isOpenForBooking()) {
                        r.setStatus(ReservationStatus.CONFIRMED);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (r.getId().equals(reservationID)) {
                    if (now.before(r.getFlight().getDepartureTime()) && r.getFlight().isOpenForBooking()) {
                        r.setStatus(ReservationStatus.CANCELED);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (f.isOpenForBooking() && now.before(f.getDepartureTime())) {
            Set<String> seen = new HashSet<>();
            for (String name : listOfPassengerNames) {
                if (!seen.add(name)) {
                    return false;
                }
            }
            for (Reservation existing : f.getReservations()) {
                for (String name : listOfPassengerNames) {
                    if (existing.getPassenger().getName().equals(name)) {
                        return false;
                    }
                }
            }
            Booking booking = new Booking();
            booking.setCustomer(this);
            for (String name : listOfPassengerNames) {
                Reservation reservation = new Reservation();
                reservation.setId(UUID.randomUUID().toString());
                reservation.setStatus(ReservationStatus.PENDING);
                Passenger passenger = new Passenger();
                passenger.setName(name);
                reservation.setPassenger(passenger);
                reservation.setFlight(f);
                f.getReservations().add(reservation);
                booking.getReservations().add(reservation);
            }
            bookings.add(booking);
            return true;
        }
        return false;
    }
}
