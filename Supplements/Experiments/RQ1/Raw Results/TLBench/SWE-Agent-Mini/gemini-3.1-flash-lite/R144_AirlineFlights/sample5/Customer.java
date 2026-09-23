import java.util.*;

public class Customer {
    private List<Booking> bookings = new ArrayList<>();

    public Customer() {}

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }

    public boolean confirm(String reservationID, Date now) {
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (r.getId().equals(reservationID)) {
                    if (r.getFlight().isOpenForBooking() && now.before(r.getFlight().getDepartureTime())) {
                        r.setStatus(ReservationStatus.CONFIRMED);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (r.getId().equals(reservationID)) {
                    if (r.getFlight().isOpenForBooking() && now.before(r.getFlight().getDepartureTime())) {
                        r.setStatus(ReservationStatus.CANCELED);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (!f.isOpenForBooking() || !now.before(f.getDepartureTime())) return false;
        
        Set<String> passengerSet = new HashSet<>(listOfPassengerNames);
        if (passengerSet.size() != listOfPassengerNames.size()) return false;

        Booking booking = new Booking();
        booking.setCustomer(this);
        
        for (String name : listOfPassengerNames) {
            if (!booking.createReservation(f, name, now)) return false;
        }
        
        this.bookings.add(booking);
        return true;
    }
}
