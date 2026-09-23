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

    public boolean confirm(String reservationID, Date now) {
        if (reservationID == null || now == null) return false;
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (r.getId().equals(reservationID)) {
                    Flight f = r.getFlight();
                    if (!f.isOpenForBooking()) return false;
                    if (now.after(f.getDepartureTime()) || now.equals(f.getDepartureTime())) return false;
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
                if (r.getId().equals(reservationID)) {
                    Flight f = r.getFlight();
                    if (!f.isOpenForBooking()) return false;
                    if (now.after(f.getDepartureTime()) || now.equals(f.getDepartureTime())) return false;
                    r.setStatus(ReservationStatus.CANCELED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (f == null || now == null || listOfPassengerNames == null || listOfPassengerNames.isEmpty()) {
            return false;
        }
        if (!f.isOpenForBooking()) return false;
        if (now.after(f.getDepartureTime()) || now.equals(f.getDepartureTime())) return false;

        if (hasDuplicatePassengerOnFlight(f, listOfPassengerNames)) return false;

        Booking booking = new Booking();
        booking.setCustomer(this);
        bookings.add(booking);

        for (String name : listOfPassengerNames) {
            if (!booking.createReservation(f, name, now)) {
                return false;
            }
        }

        return !booking.getReservations().isEmpty();
    }

    private boolean hasDuplicatePassengerOnFlight(Flight f, List<String> passengerNames) {
        for (String name : passengerNames) {
            int count = 0;
            for (String n : passengerNames) {
                if (n.equals(name)) count++;
            }
            if (count > 1) return true;

            for (Reservation r : f.getReservations()) {
                if (r.getPassenger().getName().equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }
}
