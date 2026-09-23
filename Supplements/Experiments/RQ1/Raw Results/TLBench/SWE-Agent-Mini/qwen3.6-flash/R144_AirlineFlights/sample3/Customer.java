import java.util.*;

public class Customer {
    private List<Booking> bookings;
    private static final int[] reservationCounter = {1};

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
        if (reservationID == null || reservationID.isEmpty() || now == null) {
            return false;
        }
        // Find the reservation across all bookings
        for (Booking b : bookings) {
            if (b == null) {
                continue;
            }
            for (Reservation res : b.getReservations()) {
                if (res == null) {
                    continue;
                }
                if (reservationID.equals(res.getId())) {
                    // Check that the flight has not yet departed
                    Flight f = res.getFlight();
                    if (f == null || now.after(f.getDepartureTime())) {
                        return false;
                    }
                    // Check that the flight is still open for booking
                    if (!f.isOpenForBooking()) {
                        return false;
                    }
                    res.setStatus(ReservationStatus.CONFIRMED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        if (reservationID == null || reservationID.isEmpty() || now == null) {
            return false;
        }
        for (Booking b : bookings) {
            if (b == null) {
                continue;
            }
            for (Reservation res : b.getReservations()) {
                if (res == null) {
                    continue;
                }
                if (reservationID.equals(res.getId())) {
                    Flight f = res.getFlight();
                    if (f == null || now.after(f.getDepartureTime())) {
                        return false;
                    }
                    if (!f.isOpenForBooking()) {
                        return false;
                    }
                    res.setStatus(ReservationStatus.CANCELED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (f == null || now == null || listOfPassengerNames == null) {
            return false;
        }
        // Check that the flight is open for booking
        if (!f.isOpenForBooking()) {
            return false;
        }
        // Check that current time is before the flight departure time
        if (!now.before(f.getDepartureTime())) {
            return false;
        }
        // Check for duplicate passengers
        Set<String> seenNames = new HashSet<>();
        for (String name : listOfPassengerNames) {
            if (name == null || name.isEmpty()) {
                return false;
            }
            if (seenNames.contains(name)) {
                return false;
            }
            seenNames.add(name);
        }
        // Create a booking
        Booking booking = new Booking();
        booking.setCustomer(this);
        // Create a reservation for each passenger
        for (String passengerName : listOfPassengerNames) {
            if (booking.createReservation(f, passengerName, now)) {
                Reservation newRes = booking.getReservations().get(booking.getReservations().size() - 1);
                if (newRes != null) {
                    f.getReservations().add(newRes);
                }
            } else {
                return false;
            }
        }
        bookings.add(booking);
        return true;
    }
}
