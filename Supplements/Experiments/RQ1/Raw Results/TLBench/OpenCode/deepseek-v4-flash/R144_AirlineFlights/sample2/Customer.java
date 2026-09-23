import java.util.Date;
import java.util.List;
import java.util.ArrayList;

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

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (f == null || now == null || listOfPassengerNames == null) return false;
        if (f.getDepartureTime() == null) return false;
        if (!now.before(f.getDepartureTime())) return false;
        if (!f.isOpenForBooking()) return false;
        if (listOfPassengerNames.isEmpty()) return false;

        for (int i = 0; i < listOfPassengerNames.size(); i++) {
            for (int j = i + 1; j < listOfPassengerNames.size(); j++) {
                if (listOfPassengerNames.get(i).equals(listOfPassengerNames.get(j))) {
                    return false;
                }
            }
        }

        if (f.getReservations() != null) {
            for (String name : listOfPassengerNames) {
                for (Reservation r : f.getReservations()) {
                    if (r.getPassenger() != null && name.equals(r.getPassenger().getName())) {
                        return false;
                    }
                }
            }
        }

        Booking booking = new Booking();
        booking.setCustomer(this);
        for (String passengerName : listOfPassengerNames) {
            boolean created = booking.createReservation(f, passengerName, now);
            if (!created) return false;
        }
        if (bookings == null) {
            bookings = new ArrayList<Booking>();
        }
        bookings.add(booking);
        return true;
    }

    public boolean confirm(String reservationID, Date now) {
        if (reservationID == null || now == null) return false;
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (reservationID.equals(r.getId())) {
                    Flight f = r.getFlight();
                    if (f == null) return false;
                    if (f.getDepartureTime() == null) return false;
                    if (!now.before(f.getDepartureTime())) return false;
                    if (!f.isOpenForBooking()) return false;
                    if (r.getStatus() != ReservationStatus.PENDING) return false;
                    r.setStatus(ReservationStatus.CONFIRMED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        if (reservationID == null || now == null) return false;
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (reservationID.equals(r.getId())) {
                    Flight f = r.getFlight();
                    if (f == null) return false;
                    if (f.getDepartureTime() == null) return false;
                    if (!now.before(f.getDepartureTime())) return false;
                    if (!f.isOpenForBooking()) return false;
                    if (r.getStatus() != ReservationStatus.PENDING) return false;
                    r.setStatus(ReservationStatus.CANCELED);
                    return true;
                }
            }
        }
        return false;
    }
}
