import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Customer {

    private List<Booking> bookings = new ArrayList<>();

    public Customer() {}

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public boolean confirm(String reservationId, Timestamp now) {
        if (reservationId == null || reservationId.trim().isEmpty()) {
            return false;
        }
        for (Booking booking : bookings) {
            if (booking == null) {
                continue;
            }
            for (Reservation reservation : booking.getReservations()) {
                if (reservation != null && reservationId.equals(reservation.getId())) {
                    Flight flight = reservation.getFlight();
                    if (flight == null) {
                        return false;
                    }
                    if (!flight.isOpenForBooking()) {
                        return false;
                    }
                    if (flight.getDepartureTime() != null && now != null) {
                        if (now.after(flight.getDepartureTime())) {
                            return false;
                        }
                    }
                    reservation.setStatus(ReservationStatus.CONFIRMED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean cancel(String reservationId, Timestamp now) {
        if (reservationId == null || reservationId.trim().isEmpty()) {
            return false;
        }
        for (Booking booking : bookings) {
            if (booking == null) {
                continue;
            }
            for (Reservation reservation : booking.getReservations()) {
                if (reservation != null && reservationId.equals(reservation.getId())) {
                    Flight flight = reservation.getFlight();
                    if (flight == null) {
                        return false;
                    }
                    if (!flight.isOpenForBooking()) {
                        return false;
                    }
                    if (flight.getDepartureTime() != null && now != null) {
                        if (now.after(flight.getDepartureTime())) {
                            return false;
                        }
                    }
                    reservation.setStatus(ReservationStatus.CANCELED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addBooking(Flight f, Timestamp now, List<String> listOfPassengerNames) {
        if (f == null || listOfPassengerNames == null) {
            return false;
        }
        if (!f.isOpenForBooking()) {
            return false;
        }
        if (now != null && f.getDepartureTime() != null) {
            if (now.after(f.getDepartureTime())) {
                return false;
            }
        }
        Set<String> seenNames = new HashSet<>();
        for (String name : listOfPassengerNames) {
            if (name == null || name.trim().isEmpty()) {
                return false;
            }
            if (seenNames.contains(name)) {
                return false;
            }
            seenNames.add(name);
        }
        Booking booking = new Booking();
        booking.setCustomer(this);
        for (String name : listOfPassengerNames) {
            Passenger p = new Passenger();
            p.setName(name);
            Reservation reservation = new Reservation();
            reservation.setPassenger(p);
            reservation.setFlight(f);
            reservation.setStatus(ReservationStatus.PENDING);
            boolean addedToFlight = f.addReservation(reservation);
            if (addedToFlight) {
                booking.getReservations().add(reservation);
            }
        }
        this.bookings.add(booking);
        return true;
    }
}
