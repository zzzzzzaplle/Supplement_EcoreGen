import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Customer {
    private String id;
    private String name;
    private List<Booking> bookings;

    public Customer() {
        this.id = UUID.randomUUID().toString();
        this.bookings = new ArrayList<Booking>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public boolean confirm(String reservationID, Date now) {
        if (reservationID == null) {
            return false;
        }
        Reservation target = findReservation(reservationID);
        if (target == null) {
            return false;
        }
        Flight f = target.getFlight();
        if (f == null) {
            return false;
        }
        if (!f.isOpenForBooking()) {
            return false;
        }
        if (now != null && f.getDepartureTime() != null
                && now.compareTo(f.getDepartureTime()) >= 0) {
            return false;
        }
        if (target.getStatus() == ReservationStatus.CANCELED) {
            return false;
        }
        target.setStatus(ReservationStatus.CONFIRMED);
        return true;
    }

    public boolean cancel(String reservationID, Date now) {
        if (reservationID == null) {
            return false;
        }
        Reservation target = findReservation(reservationID);
        if (target == null) {
            return false;
        }
        Flight f = target.getFlight();
        if (f == null) {
            return false;
        }
        if (!f.isOpenForBooking()) {
            return false;
        }
        if (now != null && f.getDepartureTime() != null
                && now.compareTo(f.getDepartureTime()) >= 0) {
            return false;
        }
        target.setStatus(ReservationStatus.CANCELED);
        return true;
    }

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (f == null || listOfPassengerNames == null || listOfPassengerNames.isEmpty()) {
            return false;
        }
        if (!f.isOpenForBooking()) {
            return false;
        }
        if (now != null && f.getDepartureTime() != null
                && now.compareTo(f.getDepartureTime()) >= 0) {
            return false;
        }
        for (String name : listOfPassengerNames) {
            if (name == null || name.trim().isEmpty()) {
                return false;
            }
            for (Reservation existing : f.getReservations()) {
                if (existing == null || existing.getPassenger() == null) {
                    continue;
                }
                if (existing.getStatus() == ReservationStatus.CANCELED) {
                    continue;
                }
                if (name.equalsIgnoreCase(existing.getPassenger().getName())) {
                    return false;
                }
            }
        }
        for (int i = 0; i < listOfPassengerNames.size(); i++) {
            for (int j = i + 1; j < listOfPassengerNames.size(); j++) {
                if (listOfPassengerNames.get(i) != null
                        && listOfPassengerNames.get(i).equalsIgnoreCase(listOfPassengerNames.get(j))) {
                    return false;
                }
            }
        }
        Booking booking = new Booking();
        booking.setCustomer(this);
        for (String passengerName : listOfPassengerNames) {
            Passenger p = new Passenger(passengerName);
            Reservation r = new Reservation();
            r.setPassenger(p);
            r.setFlight(f);
            r.setStatus(ReservationStatus.PENDING);
            f.getReservations().add(r);
            booking.getReservations().add(r);
        }
        if (this.bookings == null) {
            this.bookings = new ArrayList<Booking>();
        }
        this.bookings.add(booking);
        return true;
    }

    private Reservation findReservation(String reservationID) {
        if (this.bookings == null) {
            return null;
        }
        for (Booking b : this.bookings) {
            if (b == null || b.getReservations() == null) {
                continue;
            }
            for (Reservation r : b.getReservations()) {
                if (r != null && reservationID.equals(r.getId())) {
                    return r;
                }
            }
        }
        return null;
    }
}
