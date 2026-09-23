import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        for (Booking booking : bookings) {
            for (Reservation reservation : booking.getReservations()) {
                if (reservationID != null && reservationID.equals(reservation.getId()) && reservation.getFlight().isOpenForBooking() && now.before(reservation.getFlight().getDepartureTime())) {
                    reservation.setStatus(ReservationStatus.CONFIRMED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        for (Booking booking : bookings) {
            for (Reservation reservation : booking.getReservations()) {
                if (reservationID != null && reservationID.equals(reservation.getId()) && reservation.getFlight().isOpenForBooking() && now.before(reservation.getFlight().getDepartureTime())) {
                    reservation.setStatus(ReservationStatus.CANCELED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (f == null || now == null || listOfPassengerNames == null || !f.isOpenForBooking() || !now.before(f.getDepartureTime())) {
            return false;
        }
        Booking booking = new Booking();
        booking.setCustomer(this);
        List<Reservation> reservations = new ArrayList<>();
        for (String passengerName : listOfPassengerNames) {
            Passenger passenger = new Passenger();
            passenger.setName(passengerName);
            Reservation reservation = new Reservation();
            reservation.setId(java.util.UUID.randomUUID().toString());
            reservation.setPassenger(passenger);
            reservation.setFlight(f);
            reservation.setStatus(ReservationStatus.PENDING);
            reservations.add(reservation);
            f.getReservations().add(reservation);
        }
        booking.setReservations(reservations);
        bookings.add(booking);
        return true;
    }
}
