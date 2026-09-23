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
        return updateReservationStatus(reservationID, now, ReservationStatus.CONFIRMED);
    }

    public boolean cancel(String reservationID, Date now) {
        return updateReservationStatus(reservationID, now, ReservationStatus.CANCELED);
    }

    private boolean updateReservationStatus(String reservationID, Date now, ReservationStatus target) {
        if (reservationID == null || this.bookings == null) {
            return false;
        }
        for (Booking b : this.bookings) {
            if (b == null || b.getReservations() == null) {
                continue;
            }
            for (Reservation r : b.getReservations()) {
                if (r == null) {
                    continue;
                }
                if (reservationID.equals(r.getId())) {
                    Flight f = r.getFlight();
                    if (f == null) {
                        return false;
                    }
                    if (!f.isOpenForBooking()) {
                        return false;
                    }
                    if (f.getDepartureTime() != null && now != null
                            && !now.before(f.getDepartureTime())) {
                        return false;
                    }
                    r.setStatus(target);
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
        if (f.getDepartureTime() != null && now != null && !now.before(f.getDepartureTime())) {
            return false;
        }
        for (int i = 0; i < listOfPassengerNames.size(); i++) {
            String nameI = listOfPassengerNames.get(i);
            if (nameI == null) {
                return false;
            }
            for (int j = i + 1; j < listOfPassengerNames.size(); j++) {
                if (nameI.equals(listOfPassengerNames.get(j))) {
                    return false;
                }
            }
            if (f.getReservations() != null) {
                for (Reservation r : f.getReservations()) {
                    if (r != null && r.getPassenger() != null
                            && nameI.equals(r.getPassenger().getName())) {
                        return false;
                    }
                }
            }
        }
        Booking booking = new Booking(this);
        for (String name : listOfPassengerNames) {
            boolean ok = booking.createReservation(f, name, now);
            if (!ok) {
                return false;
            }
        }
        if (this.bookings == null) {
            this.bookings = new ArrayList<Booking>();
        }
        this.bookings.add(booking);
        return true;
    }
}
