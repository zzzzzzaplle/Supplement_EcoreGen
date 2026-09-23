import java.util.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.ZoneId;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Booking {
    private int numberOfSeats;
    private Customer customer;
    private Trip trip;
    private Date bookingDate;

    public Booking() {
    }

    private LocalDate getDateOnly(Date date) {
        if (date == null) return null;
        Instant instant = date.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public boolean isBookingEligible() {
        if (customer == null || trip == null || bookingDate == null) {
            return false;
        }
        Date departureDate = trip.getDepartureDate();
        String departureTime = trip.getDepartureTime();
        if (departureDate == null || departureTime == null) {
            return false;
        }
        int availableSeats = trip.getNumberOfSeats() - trip.getBookedSeats();
        if (numberOfSeats <= 0 || numberOfSeats > availableSeats) {
            return false;
        }
        try {
            LocalDateTime bookingDateTime = bookingDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDate tripDate = getDateOnly(departureDate);
            LocalTime depTime = LocalTime.parse(departureTime);
            LocalDateTime tripDateTime = tripDate.atTime(depTime);
            long hoursDiff = Duration.between(bookingDateTime, tripDateTime).toHours();
            if (hoursDiff <= 2) {
                return false;
            }
        } catch (DateTimeParseException e) {
            return false;
        }
        for (Booking existing : customer.getBookings()) {
            if (existing == this) continue;
            if (existing != null && existing.overlapsWith(trip)) {
                return false;
            }
        }
        return true;
    }

    public boolean overlapsWith(Trip trip) {
        if (trip == null || this.trip == null) {
            return false;
        }
        Date tripDate = this.trip.getDepartureDate();
        Date otherDate = trip.getDepartureDate();
        if (tripDate == null || otherDate == null) {
            return false;
        }
        LocalDate myDate = getDateOnly(tripDate);
        LocalDate otherDateObj = getDateOnly(otherDate);
        if (!myDate.equals(otherDateObj)) {
            return false;
        }
        String myDepTime = this.trip.getDepartureTime();
        String myArrTime = this.trip.getArrivalTime();
        String otherDepTime = trip.getDepartureTime();
        String otherArrTime = trip.getArrivalTime();
        if (myDepTime == null || myArrTime == null || otherDepTime == null || otherArrTime == null) {
            return false;
        }
        try {
            LocalTime myDep = LocalTime.parse(myDepTime);
            LocalTime myArr = LocalTime.parse(myArrTime);
            LocalTime otherDep = LocalTime.parse(otherDepTime);
            LocalTime otherArr = LocalTime.parse(otherArrTime);
            if (otherDep.isAfter(myArr) || myDep.isAfter(otherArr)) {
                return false;
            }
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    public void updateTripSeats() {
        if (trip != null) {
            trip.setNumberOfSeats(trip.getNumberOfSeats() - numberOfSeats);
        }
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null || month.isEmpty()) {
            return false;
        }
        LocalDate bd = getDateOnly(bookingDate);
        String monthStr = DateTimeFormatter.ofPattern("yyyy-MM").format(bd);
        return monthStr.equals(month);
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

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }
}
