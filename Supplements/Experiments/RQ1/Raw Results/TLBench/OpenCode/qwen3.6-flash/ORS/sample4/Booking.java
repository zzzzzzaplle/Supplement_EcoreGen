import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ArrayList;

class Booking {
    private int numberOfSeats;
    private Customer customer;
    private Trip trip;
    private LocalDate bookingDate;

    public Booking() {
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public boolean isBookingEligible(LocalDateTime bookingTime) {
        if (customer == null || trip == null || bookingDate == null || bookingTime == null) {
            return false;
        }
        int alreadyBooked = trip.getBookedSeats();
        if (numberOfSeats > trip.getNumberOfSeats() - alreadyBooked) {
            return false;
        }
        LocalDate tripDate = trip.getDepartureDate();
        if (tripDate != null && tripDate.isEqual(bookingDate)) {
            LocalTime tripDeparture = trip.getDepartureTime();
            LocalTime tripArrival = trip.getArrivalTime();
            for (Booking existingBooking : customer.getBookings()) {
                if (existingBooking == null || existingBooking == this) {
                    continue;
                }
                Trip existingTrip = existingBooking.getTrip();
                if (existingTrip == null) {
                    continue;
                }
                if (existingTrip.getDepartureDate() != null && existingTrip.getDepartureDate().isEqual(bookingDate)) {
                    if (overlapsWith(existingTrip)) {
                        return false;
                    }
                }
            }
        }
        LocalTime departureTime = trip.getDepartureTime();
        if (departureTime != null) {
            LocalDateTime departureDateTime = LocalDateTime.of(bookingDate, departureTime);
            long hoursBefore = ChronoUnit.HOURS.between(bookingTime, departureDateTime);
            if (hoursBefore <= 2) {
                return false;
            }
        }
        return true;
    }

    public boolean overlapsWith(Trip otherTrip) {
        LocalTime newDepart = trip.getDepartureTime();
        LocalTime newArrival = trip.getArrivalTime();
        LocalTime otherDepart = otherTrip.getDepartureTime();
        LocalTime otherArrival = otherTrip.getArrivalTime();
        return newDepart.isBefore(otherArrival) && newArrival.isAfter(otherDepart);
    }

    public void updateTripSeats() {
        trip.setNumberOfSeats(trip.getNumberOfSeats() - numberOfSeats);
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null) {
            return false;
        }
        int year = Integer.parseInt(month.split("-")[0]);
        int mon = Integer.parseInt(month.split("-")[1]);
        return bookingDate.getYear() == year && bookingDate.getMonthValue() == mon;
    }
}
