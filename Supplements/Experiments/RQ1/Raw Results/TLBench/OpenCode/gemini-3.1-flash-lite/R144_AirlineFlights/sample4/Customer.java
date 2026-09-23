import java.util.*;

public class Customer {
    private List<Booking> bookings = new ArrayList<>();

    public Customer() {}

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }

    public boolean confirm(String reservationID, Date now) {
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (r.getId().equals(reservationID) && r.getFlight().isOpenForBooking() && r.getFlight().getDepartureTime().after(now)) {
                    r.setStatus(ReservationStatus.CONFIRMED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (r.getId().equals(reservationID) && r.getFlight().isOpenForBooking() && r.getFlight().getDepartureTime().after(now)) {
                    r.setStatus(ReservationStatus.CANCELED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (!f.isOpenForBooking() || f.getDepartureTime().before(now)) return false;
        
        Set<String> uniqueNames = new HashSet<>(listOfPassengerNames);
        if (uniqueNames.size() != listOfPassengerNames.size()) return false;
        
        Booking b = new Booking();
        b.setCustomer(this);
        for (String name : listOfPassengerNames) {
            b.createReservation(f, name, now);
        }
        bookings.add(b);
        return true;
    }
}
