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
        return updateReservationStatus(reservationID, now, ReservationStatus.PENDING, ReservationStatus.CONFIRMED);
    }

    public boolean cancel(String reservationID, Date now) {
        return updateReservationStatus(reservationID, now, null, ReservationStatus.CANCELED);
    }

    private boolean updateReservationStatus(String reservationID, Date now,
                                            ReservationStatus required, ReservationStatus newStatus) {
        if (reservationID == null || now == null) {
            return false;
        }
        if (this.bookings == null) {
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
                    if (f.getDepartureTime() == null || !now.before(f.getDepartureTime())) {
                        return false;
                    }
                    if (required != null && r.getStatus() != required) {
                        return false;
                    }
                    r.setStatus(newStatus);
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
        for (int i = 0; i < listOfPassengerNames.size(); i++) {
            String a = listOfPassengerNames.get(i);
            if (a == null) {
                return false;
            }
            for (int j = i + 1; j < listOfPassengerNames.size(); j++) {
                if (a.equals(listOfPassengerNames.get(j))) {
                    return false;
                }
            }
        }
        if (f.getReservations() != null) {
            for (Reservation existing : f.getReservations()) {
                if (existing != null && existing.getPassenger() != null) {
                    String name = existing.getPassenger().getName();
                    for (String n : listOfPassengerNames) {
                        if (n.equals(name)) {
                            return false;
                        }
                    }
                }
            }
        }
        Booking b = new Booking();
        b.setCustomer(this);
        for (String name : listOfPassengerNames) {
            b.createReservation(f, name, now);
        }
        if (this.bookings == null) {
            this.bookings = new ArrayList<Booking>();
        }
        this.bookings.add(b);
        return true;
    }
}
