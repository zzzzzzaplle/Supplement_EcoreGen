import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Booking {
    private int numberOfSeats;
    private Customer customer;
    private Trip trip;
    private Date bookingDate;

    public Booking() {
    }

    public boolean isBookingEligible() {
        if (customer == null || trip == null || bookingDate == null) {
            return false;
        }
        int bookedSeats = trip.getBookedSeats();
        if (bookedSeats + numberOfSeats > trip.getNumberOfSeats()) {
            return false;
        }
        for (Booking existing : customer.getBookings()) {
            if (existing == this) {
                continue;
            }
            if (existing.getBookingDate() == null) {
                continue;
            }
            if (existing.getTrip() == null) {
                continue;
            }
            if (overlapsWith(existing.getTrip())) {
                return false;
            }
        }

        try {
            LocalDateTime departureDateTime = parseDepartureLocalDateTime();
            if (departureDateTime == null) {
                return false;
            }
            LocalDateTime bookingDateTime = new Date(bookingDate.getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            if (bookingDateTime.isBefore(departureDateTime.minusHours(2))) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public void updateTripSeats() {
        if (trip != null) {
            trip.addBooking(this);
        }
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

    public boolean overlapsWith(Trip otherTrip) {
        if (this.trip == null || otherTrip == null) {
            return false;
        }
        if (this.trip.getDepartureDate() == null || otherTrip.getDepartureDate() == null) {
            return false;
        }
        if (!this.trip.getDepartureDate().equals(otherTrip.getDepartureDate())) {
            return false;
        }
        return this.trip.isTimeConflicting(otherTrip.getDepartureTime(), otherTrip.getArrivalTime());
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null) {
            return false;
        }
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
            String formatted = fmt.format(new Date(bookingDate.getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            return formatted.equals(month);
        } catch (Exception e) {
            return false;
        }
    }

    private LocalDateTime parseDepartureLocalDateTime() {
        try {
            LocalDateTime depDatePart = this.trip.getDepartureDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            String[] parts = this.trip.getDepartureTime().split(":");
            return depDatePart.withHour(Integer.parseInt(parts[0])).withMinute(Integer.parseInt(parts[1]));
        } catch (Exception e) {
            return null;
        }
    }
}
