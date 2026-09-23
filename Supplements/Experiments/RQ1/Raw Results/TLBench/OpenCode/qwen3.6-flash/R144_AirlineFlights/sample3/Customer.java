import java.util.Date;
import java.util.List;

public class Customer {
    private List<Booking> bookings;

    public Customer() {
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public boolean confirm(String reservationID, Date now) {
        for (Booking booking : bookings) {
            for (Reservation reservation : booking.getReservations()) {
                if (reservationID.equals(reservation.getId())) {
                    Flight f = reservation.getFlight();
                    if (f == null || f.getDepartureTime().before(now) || f.isOpenForBooking() == false) {
                        return false;
                    }
                    reservation.setStatus(ReservationStatus.CONFIRMED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        for (Booking booking : bookings) {
            for (Reservation reservation : booking.getReservations()) {
                if (reservationID.equals(reservation.getId())) {
                    Flight f = reservation.getFlight();
                    if (f == null || f.getDepartureTime().before(now) || f.isOpenForBooking() == false) {
                        return false;
                    }
                    reservation.setStatus(ReservationStatus.CANCELED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (f == null || f.isOpenForBooking() == false) {
            return false;
        }
        if (f.getDepartureTime().before(now)) {
            return false;
        }
        if (listOfPassengerNames == null || listOfPassengerNames.isEmpty()) {
            return false;
        }
        // Check for duplicate passengers on the flight
        for (String name : listOfPassengerNames) {
            for (Reservation existingReservation : f.getReservations()) {
                if (name.equals(existingReservation.getPassenger().getName())) {
                    return false;
                }
            }
        }
        // Check for duplicate passengers within the list
        for (int i = 0; i < listOfPassengerNames.size(); i++) {
            for (int j = i + 1; j < listOfPassengerNames.size(); j++) {
                if (listOfPassengerNames.get(i).equals(listOfPassengerNames.get(j))) {
                    return false;
                }
            }
        }
        Booking booking = new Booking();
        booking.setCustomer(this);
        bookings.add(booking);
        for (String name : listOfPassengerNames) {
            booking.createReservation(f, name, now);
        }
        return true;
    }
}
