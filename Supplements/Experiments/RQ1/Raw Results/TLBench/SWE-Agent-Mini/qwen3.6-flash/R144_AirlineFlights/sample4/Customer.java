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

    public boolean confirm(String reservationId, Date now) {
        for (Booking booking : bookings) {
            for (Reservation reservation : booking.getReservations()) {
                if (reservation != null && reservation.getId().equals(reservationId)) {
                    Flight flight = reservation.getFlight();
                    if (flight == null || !flight.isOpenForBooking()) {
                        return false;
                    }
                    if (flight.getDepartureTime() != null && 
                        now != null && now.getTime() >= flight.getDepartureTime().getTime()) {
                        return false;
                    }
                    reservation.setStatus(ReservationStatus.CONFIRMED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean cancel(String reservationId, Date now) {
        for (Booking booking : bookings) {
            for (Reservation reservation : booking.getReservations()) {
                if (reservation != null && reservation.getId().equals(reservationId)) {
                    Flight flight = reservation.getFlight();
                    if (flight == null || !flight.isOpenForBooking()) {
                        return false;
                    }
                    if (flight.getDepartureTime() != null && 
                        now != null && now.getTime() >= flight.getDepartureTime().getTime()) {
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
        if (f == null || now == null || listOfPassengerNames == null || !f.isOpenForBooking()) {
            return false;
        }
        
        if (now.getTime() >= f.getDepartureTime().getTime()) {
            return false;
        }
        
        // Check for duplicate passengers in the list
        for (int i = 0; i < listOfPassengerNames.size(); i++) {
            for (int j = i + 1; j < listOfPassengerNames.size(); j++) {
                if (listOfPassengerNames.get(i).equals(listOfPassengerNames.get(j))) {
                    return false;
                }
            }
        }
        
        // Check for duplicate passengers on the flight
        for (String passengerName : listOfPassengerNames) {
            boolean duplicateOnFlight = false;
            for (Reservation res : f.getReservations()) {
                if (res.getPassenger() != null && res.getPassenger().getName().equals(passengerName)) {
                    duplicateOnFlight = true;
                    break;
                }
            }
            if (duplicateOnFlight) {
                return false;
            }
        }
        
        Booking booking = new Booking();
        booking.setCustomer(this);
        bookings.add(booking);
        
        for (String passengerName : listOfPassengerNames) {
            boolean result = booking.createReservation(f, passengerName, now);
            if (!result) {
                return false;
            }
        }
        
        return true;
    }
}
