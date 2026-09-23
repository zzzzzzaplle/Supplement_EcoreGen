import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class Customer {
    private List<Booking> bookings;

    public Customer() {
        this.bookings = new ArrayList<Booking>();
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public boolean confirm(String reservationID, Date now) {
        if (reservationID == null || now == null) {
            return false;
        }
        Reservation found = findReservation(reservationID);
        if (found == null) {
            return false;
        }
        Flight flight = found.getFlight();
        if (flight == null) {
            return false;
        }
        // Check flight has not yet departed and is still open for booking
        if (flight.getDepartureTime() != null && !now.before(flight.getDepartureTime())) {
            return false;
        }
        if (!flight.isOpenForBooking()) {
            return false;
        }
        found.setStatus(ReservationStatus.CONFIRMED);
        return true;
    }

    public boolean cancel(String reservationID, Date now) {
        if (reservationID == null || now == null) {
            return false;
        }
        Reservation found = findReservation(reservationID);
        if (found == null) {
            return false;
        }
        Flight flight = found.getFlight();
        if (flight == null) {
            return false;
        }
        // Check flight has not yet departed and is still open for booking
        if (flight.getDepartureTime() != null && !now.before(flight.getDepartureTime())) {
            return false;
        }
        if (!flight.isOpenForBooking()) {
            return false;
        }
        found.setStatus(ReservationStatus.CANCELED);
        return true;
    }

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (f == null || now == null || listOfPassengerNames == null) {
            return false;
        }
        if (listOfPassengerNames.isEmpty()) {
            return false;
        }
        // Check flight is open
        if (!f.isOpenForBooking()) {
            return false;
        }
        // Check current time is before flight departure time
        if (f.getDepartureTime() != null && !now.before(f.getDepartureTime())) {
            return false;
        }
        // Check no duplicate passenger names
        for (int i = 0; i < listOfPassengerNames.size(); i++) {
            for (int j = i + 1; j < listOfPassengerNames.size(); j++) {
                if (listOfPassengerNames.get(i).equals(listOfPassengerNames.get(j))) {
                    return false;
                }
            }
        }
        // Check no duplicate passengers already on this flight
        List<Reservation> existingReservations = f.getReservations();
        if (existingReservations != null) {
            for (String name : listOfPassengerNames) {
                for (Reservation r : existingReservations) {
                    if (r.getPassenger() != null && name.equals(r.getPassenger().getName())) {
                        return false;
                    }
                }
            }
        }
        // Also check this customer's existing bookings for same flight/passenger
        if (bookings != null) {
            for (Booking b : bookings) {
                if (b.getReservations() != null) {
                    for (Reservation r : b.getReservations()) {
                        if (r.getFlight() != null && r.getFlight().getId() != null && r.getFlight().getId().equals(f.getId())) {
                            for (String name : listOfPassengerNames) {
                                if (r.getPassenger() != null && name.equals(r.getPassenger().getName())) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }

        Booking booking = new Booking();
        booking.setCustomer(this);
        
        for (String passengerName : listOfPassengerNames) {
            boolean created = booking.createReservation(f, passengerName, now);
            if (!created) {
                return false;
            }
        }
        
        if (bookings == null) {
            bookings = new ArrayList<Booking>();
        }
        bookings.add(booking);
        return true;
    }

    private Reservation findReservation(String reservationID) {
        if (bookings != null) {
            for (Booking b : bookings) {
                if (b.getReservations() != null) {
                    for (Reservation r : b.getReservations()) {
                        if (reservationID.equals(r.getId())) {
                            return r;
                        }
                    }
                }
            }
        }
        return null;
    }
}
