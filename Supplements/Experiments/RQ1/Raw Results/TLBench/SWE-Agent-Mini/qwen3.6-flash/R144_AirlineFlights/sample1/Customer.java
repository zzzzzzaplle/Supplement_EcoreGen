import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class Customer {
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
        if (reservationID == null || now == null) {
            return false;
        }
        for (Booking b : bookings) {
            if (b == null) continue;
            List<Reservation> reservations = b.getReservations();
            for (Reservation r : reservations) {
                if (r == null) continue;
                if (reservationID.equals(r.getId())) {
                    Flight flight = r.getFlight();
                    if (flight == null) {
                        return false;
                    }
                    Date departureTime = flight.getDepartureTime();
                    if (departureTime == null) {
                        return false;
                    }
                    if (now.after(departureTime)) {
                        return false;
                    }
                    if (!flight.isOpenForBooking()) {
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
            if (b == null) continue;
            List<Reservation> reservations = b.getReservations();
            for (Reservation r : reservations) {
                if (r == null) continue;
                if (reservationID.equals(r.getId())) {
                    Flight flight = r.getFlight();
                    if (flight == null) {
                        return false;
                    }
                    Date departureTime = flight.getDepartureTime();
                    if (departureTime == null) {
                        return false;
                    }
                    if (now.after(departureTime)) {
                        return false;
                    }
                    if (!flight.isOpenForBooking()) {
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
        if (f == null || listOfPassengerNames == null || now == null) {
            return false;
        }
        if (!f.isOpenForBooking()) {
            return false;
        }
        Date departureTime = f.getDepartureTime();
        if (departureTime == null) {
            return false;
        }
        if (now.after(departureTime)) {
            return false;
        }
        for (String name : listOfPassengerNames) {
            if (name == null || name.trim().isEmpty()) {
                return false;
            }
        }
        for (int i = 0; i < listOfPassengerNames.size(); i++) {
            for (int j = i + 1; j < listOfPassengerNames.size(); j++) {
                if (listOfPassengerNames.get(i).equals(listOfPassengerNames.get(j))) {
                    return false;
                }
            }
        }
        Booking booking = new Booking();
        booking.setCustomer(this);
        int count = 0;
        List<Reservation> tempReservations = new ArrayList<>();
        for (String name : listOfPassengerNames) {
            boolean created = booking.createReservation(f, name, now);
            if (!created) {
                return false;
            }
            count++;
        }
        bookings.add(booking);
        return true;
    }
}
