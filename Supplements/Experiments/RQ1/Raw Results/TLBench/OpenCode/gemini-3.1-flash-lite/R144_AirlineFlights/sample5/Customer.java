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
                    if (r.getFlight().getDepartureTime().before(now) || !r.getFlight().isOpenForBooking()) return false;
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
                    if (r.getFlight().getDepartureTime().before(now) || !r.getFlight().isOpenForBooking()) return false;
                    r.setStatus(ReservationStatus.CANCELED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (!f.isOpenForBooking() || now.after(f.getDepartureTime())) return false;
        
        Set<String> uniquePassengers = new HashSet<>(listOfPassengerNames);
        if (uniquePassengers.size() != listOfPassengerNames.size()) return false;
        
        Booking b = new Booking();
        b.setCustomer(this);
        List<Reservation> resList = new ArrayList<>();
        for (String name : listOfPassengerNames) {
            Passenger p = new Passenger();
            p.setName(name);
            Reservation r = new Reservation();
            r.setId(UUID.randomUUID().toString());
            r.setPassenger(p);
            r.setFlight(f);
            r.setStatus(ReservationStatus.PENDING);
            resList.add(r);
            f.getReservations().add(r);
        }
        b.setReservations(resList);
        bookings.add(b);
        return true;
    }
}
