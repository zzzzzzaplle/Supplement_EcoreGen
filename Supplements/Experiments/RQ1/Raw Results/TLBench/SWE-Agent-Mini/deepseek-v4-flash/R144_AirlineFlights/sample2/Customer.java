import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class Customer {
    private List<Booking> bookings;

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
        if (reservationID == null || now == null) return false;
        for (Booking b : bookings) {
            if (b.getReservations() != null) {
                for (Reservation r : b.getReservations()) {
                    if (r.getId() != null && r.getId().equals(reservationID)) {
                        Flight f = r.getFlight();
                        if (f == null) return false;
                        if (!f.isOpenForBooking()) return false;
                        if (f.getDepartureTime() == null || !now.before(f.getDepartureTime())) return false;
                        if (r.getStatus() == ReservationStatus.PENDING) {
                            r.setStatus(ReservationStatus.CONFIRMED);
                            return true;
                        }
                        return false;
                    }
                }
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        if (reservationID == null || now == null) return false;
        for (Booking b : bookings) {
            if (b.getReservations() != null) {
                for (Reservation r : b.getReservations()) {
                    if (r.getId() != null && r.getId().equals(reservationID)) {
                        Flight f = r.getFlight();
                        if (f == null) return false;
                        if (!f.isOpenForBooking()) return false;
                        if (f.getDepartureTime() == null || !now.before(f.getDepartureTime())) return false;
                        if (r.getStatus() == ReservationStatus.PENDING || r.getStatus() == ReservationStatus.CONFIRMED) {
                            r.setStatus(ReservationStatus.CANCELED);
                            return true;
                        }
                        return false;
                    }
                }
            }
        }
        return false;
    }

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (f == null || now == null || listOfPassengerNames == null) return false;
        if (!f.isOpenForBooking()) return false;
        if (f.getDepartureTime() == null || !now.before(f.getDepartureTime())) return false;
        
        if (listOfPassengerNames.isEmpty()) return false;
        
        for (int i = 0; i < listOfPassengerNames.size(); i++) {
            for (int j = i + 1; j < listOfPassengerNames.size(); j++) {
                if (listOfPassengerNames.get(i).equals(listOfPassengerNames.get(j))) {
                    return false;
                }
            }
        }
        
        Booking booking = new Booking();
        booking.setCustomer(this);
        
        for (String passengerName : listOfPassengerNames) {
            boolean success = booking.createReservation(f, passengerName, now);
            if (!success) return false;
        }
        
        if (this.bookings == null) this.bookings = new ArrayList<>();
        this.bookings.add(booking);
        
        return true;
    }
}
