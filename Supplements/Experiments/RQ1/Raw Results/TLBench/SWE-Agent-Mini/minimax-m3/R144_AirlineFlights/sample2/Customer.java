import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        for (Booking b : bookings) {
            if (b == null || b.getReservations() == null) {
                continue;
            }
            for (Reservation r : b.getReservations()) {
                if (r != null && reservationID.equals(r.getId())) {
                    Flight f = r.getFlight();
                    if (f == null) {
                        return false;
                    }
                    if (!f.isOpenForBooking()) {
                        return false;
                    }
                    if (f.getDepartureTime() == null || !now.before(f.getDepartureTime())) {
                        return false;
                    }
                    if (r.getStatus() == ReservationStatus.CONFIRMED
                            || r.getStatus() == ReservationStatus.CANCELED) {
                        return false;
                    }
                    r.setStatus(ReservationStatus.CONFIRMED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        if (reservationID == null || now == null) {
            return false;
        }
        for (Booking b : bookings) {
            if (b == null || b.getReservations() == null) {
                continue;
            }
            for (Reservation r : b.getReservations()) {
                if (r != null && reservationID.equals(r.getId())) {
                    Flight f = r.getFlight();
                    if (f == null) {
                        return false;
                    }
                    if (!f.isOpenForBooking()) {
                        return false;
                    }
                    if (f.getDepartureTime() == null || !now.before(f.getDepartureTime())) {
                        return false;
                    }
                    if (r.getStatus() == ReservationStatus.CANCELED) {
                        return false;
                    }
                    r.setStatus(ReservationStatus.CANCELED);
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
        if (!f.isOpenForBooking()) {
            return false;
        }
        if (f.getDepartureTime() == null || !now.before(f.getDepartureTime())) {
            return false;
        }
        // Check for duplicate passengers
        for (int i = 0; i < listOfPassengerNames.size(); i++) {
            for (int j = i + 1; j < listOfPassengerNames.size(); j++) {
                String n1 = listOfPassengerNames.get(i);
                String n2 = listOfPassengerNames.get(j);
                if (n1 != null && n1.equals(n2)) {
                    return false;
                }
            }
        }
        // Check for duplicate passengers on the flight
        if (f.getReservations() != null) {
            for (Reservation existing : f.getReservations()) {
                if (existing == null || existing.getPassenger() == null) {
                    continue;
                }
                String existingName = existing.getPassenger().getName();
                for (String newName : listOfPassengerNames) {
                    if (newName != null && newName.equals(existingName)) {
                        return false;
                    }
                }
            }
        }

        Booking booking = new Booking();
        booking.setCustomer(this);
        booking.setReservations(new ArrayList<Reservation>());

        for (String passengerName : listOfPassengerNames) {
            Reservation r = new Reservation();
            r.setId(java.util.UUID.randomUUID().toString());
            r.setStatus(ReservationStatus.PENDING);
            Passenger p = new Passenger();
            p.setName(passengerName);
            r.setPassenger(p);
            r.setFlight(f);
            booking.getReservations().add(r);
            if (f.getReservations() == null) {
                f.setReservations(new ArrayList<Reservation>());
            }
            f.getReservations().add(r);
        }

        if (bookings == null) {
            bookings = new ArrayList<Booking>();
        }
        bookings.add(booking);
        return true;
    }
}
