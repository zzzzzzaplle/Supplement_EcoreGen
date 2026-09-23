import java.text.SimpleDateFormat;
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
        if (numberOfSeats <= 0) {
            return false;
        }
        int bookedSoFar = trip.getBookedSeats();
        int available = trip.getNumberOfSeats() - bookedSoFar;
        if (numberOfSeats > available) {
            return false;
        }
        // Check overlapping booking on same day
        if (customer.getBookings() != null) {
            for (Booking b : customer.getBookings()) {
                if (b == null || b == this) {
                    continue;
                }
                if (b.getTrip() != null && b.overlapsWith(trip)) {
                    return false;
                }
            }
        }
        // Booking must be more than 2 hours before departure
        if (trip.getDepartureDate() == null || trip.getDepartureTime() == null) {
            return false;
        }
        try {
            String[] depParts = trip.getDepartureTime().split(":");
            int depHour = Integer.parseInt(depParts[0]);
            int depMinute = depParts.length > 1 ? Integer.parseInt(depParts[1]) : 0;
            long departureMillis = trip.getDepartureDate().getTime()
                    + (long) depHour * 60 * 60 * 1000
                    + (long) depMinute * 60 * 1000;
            long bookingMillis = bookingDate.getTime();
            long diffMillis = departureMillis - bookingMillis;
            long twoHoursMillis = 2L * 60 * 60 * 1000;
            if (diffMillis <= twoHoursMillis) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
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
        if (otherTrip == null || this.trip == null) {
            return false;
        }
        if (this.trip.getDepartureDate() == null || otherTrip.getDepartureDate() == null) {
            return false;
        }
        // Same day check
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String thisDay = sdf.format(this.trip.getDepartureDate());
        String otherDay = sdf.format(otherTrip.getDepartureDate());
        if (!thisDay.equals(otherDay)) {
            return false;
        }
        // Time overlap
        return this.trip.isTimeConflicting(otherTrip.getDepartureTime(), otherTrip.getArrivalTime());
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String bookingMonth = sdf.format(bookingDate);
        return bookingMonth.equals(month);
    }
}
