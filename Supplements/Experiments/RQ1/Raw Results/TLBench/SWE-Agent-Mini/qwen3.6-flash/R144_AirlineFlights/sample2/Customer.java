import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        if (reservationID == null || now == null) {
            return false;
        }
        for (Booking b : bookings) {
            if (b != null) {
                for (Reservation r : b.getReservations()) {
                    if (r != null && reservationID.equals(r.getId())) {
                        Flight f = r.getFlight();
                        if (f == null) {
                            return false;
                        }
                        if (!f.isOpenForBooking()) {
                            return false;
                        }
                        Date depTime = f.getDepartureTime();
                        if (depTime != null && now.getTime() >= depTime.getTime()) {
                            return false;
                        }
                        r.setStatus(ReservationStatus.CONFIRMED);
                        return true;
                    }
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
            if (b != null) {
                for (Reservation r : b.getReservations()) {
                    if (r != null && reservationID.equals(r.getId())) {
                        Flight f = r.getFlight();
                        if (f == null) {
                            return false;
                        }
                        if (!f.isOpenForBooking()) {
                            return false;
                        }
                        Date depTime = f.getDepartureTime();
                        if (depTime != null && now.getTime() >= depTime.getTime()) {
                            return false;
                        }
                        r.setStatus(ReservationStatus.CANCELED);
                        return true;
                    }
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
        Date depTime = f.getDepartureTime();
        if (depTime != null && now.getTime() >= depTime.getTime()) {
            return false;
        }
        Booking booking = new Booking();
        booking.setCustomer(this);
        Long baseId = Flight.generateId();
        for (int i = 0; i < listOfPassengerNames.size(); i++) {
            String name = listOfPassengerNames.get(i);
            if (name == null || name.trim().isEmpty()) {
                return false;
            }
            for (int j = 0; j < i; j++) {
                String prev = listOfPassengerNames.get(j);
                if (prev != null && prev.equals(name)) {
                    return false;
                }
            }
            Passenger p = new Passenger();
            p.setName(name);
            Reservation r = new Reservation();
            String resid = baseId + "-" + i;
            r.setId(resid);
            r.setPassenger(p);
            r.setFlight(f);
            r.setStatus(ReservationStatus.PENDING);
            booking.createReservation(f, name, now);
            f.getReservations().add(r);
            booking.getReservations().add(r);
        }
        this.bookings.add(booking);
        return true;
    }
}
