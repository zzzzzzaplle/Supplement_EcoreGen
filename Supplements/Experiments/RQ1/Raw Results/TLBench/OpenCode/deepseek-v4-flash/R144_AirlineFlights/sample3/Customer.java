import java.util.List;
import java.util.ArrayList;
import java.util.Date;

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
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (r.getId().equals(reservationID)) {
                    Flight f = r.getFlight();
                    if (f.getDepartureTime().before(now)) {
                        return false;
                    }
                    if (!f.isOpenForBooking()) {
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
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (r.getId().equals(reservationID)) {
                    Flight f = r.getFlight();
                    if (f.getDepartureTime().before(now)) {
                        return false;
                    }
                    if (!f.isOpenForBooking()) {
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
        if (!f.isOpenForBooking()) {
            return false;
        }
        if (f.getDepartureTime().before(now)) {
            return false;
        }
        for (int i = 0; i < listOfPassengerNames.size(); i++) {
            for (int j = i + 1; j < listOfPassengerNames.size(); j++) {
                if (listOfPassengerNames.get(i).equals(listOfPassengerNames.get(j))) {
                    return false;
                }
            }
        }
        for (Reservation existing : f.getReservations()) {
            for (String name : listOfPassengerNames) {
                if (existing.getPassenger().getName().equals(name)) {
                    return false;
                }
            }
        }
        Booking b = new Booking();
        b.setCustomer(this);
        for (String name : listOfPassengerNames) {
            boolean ok = b.createReservation(f, name, now);
            if (!ok) {
                return false;
            }
        }
        bookings.add(b);
        return true;
    }
}
