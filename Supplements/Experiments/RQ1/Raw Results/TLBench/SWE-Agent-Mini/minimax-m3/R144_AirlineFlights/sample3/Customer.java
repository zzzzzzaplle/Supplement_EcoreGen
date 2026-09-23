import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
        if (reservationID == null || this.bookings == null) {
            return false;
        }
        for (Booking b : this.bookings) {
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
                    if (f.getDepartureTime() != null && !now.before(f.getDepartureTime())) {
                        return false;
                    }
                    if (r.getStatus() == ReservationStatus.CANCELED) {
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
        if (reservationID == null || this.bookings == null) {
            return false;
        }
        for (Booking b : this.bookings) {
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
                    if (f.getDepartureTime() != null && !now.before(f.getDepartureTime())) {
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
        if (f == null || listOfPassengerNames == null || listOfPassengerNames.isEmpty()) {
            return false;
        }
        if (!f.isOpenForBooking()) {
            return false;
        }
        if (f.getDepartureTime() != null && !now.before(f.getDepartureTime())) {
            return false;
        }
        // check duplicates among requested passenger names
        for (int i = 0; i < listOfPassengerNames.size(); i++) {
            for (int j = i + 1; j < listOfPassengerNames.size(); j++) {
                String a = listOfPassengerNames.get(i);
                String b = listOfPassengerNames.get(j);
                if (a != null && a.equals(b)) {
                    return false;
                }
            }
        }
        // check no duplicate passenger on the flight
        if (f.getReservations() != null) {
            for (Reservation existing : f.getReservations()) {
                if (existing == null || existing.getPassenger() == null) {
                    continue;
                }
                String existingName = existing.getPassenger().getName();
                for (String name : listOfPassengerNames) {
                    if (name != null && name.equals(existingName)) {
                        return false;
                    }
                }
            }
        }
        Booking booking = new Booking();
        booking.setCustomer(this);
        List<Reservation> reservations = new ArrayList<Reservation>();
        for (String name : listOfPassengerNames) {
            Passenger p = new Passenger();
            p.setName(name);
            Reservation r = new Reservation();
            r.setId(UUID.randomUUID().toString());
            r.setPassenger(p);
            r.setFlight(f);
            r.setStatus(ReservationStatus.PENDING);
            reservations.add(r);
            if (f.getReservations() == null) {
                f.setReservations(new ArrayList<Reservation>());
            }
            f.getReservations().add(r);
        }
        booking.setReservations(reservations);
        if (this.bookings == null) {
            this.bookings = new ArrayList<Booking>();
        }
        this.bookings.add(booking);
        return true;
    }
}
