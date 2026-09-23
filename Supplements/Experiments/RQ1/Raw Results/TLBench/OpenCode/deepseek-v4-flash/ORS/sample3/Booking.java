import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
        if (trip.getNumberOfSeats() <= 0 || numberOfSeats <= 0 || numberOfSeats > trip.getNumberOfSeats()) {
            return false;
        }
        if (trip.getBookings() != null) {
            for (Booking b : trip.getBookings()) {
                if (b.getCustomer() != null && b.getCustomer().equals(customer)) {
                    return false;
                }
            }
        }
        if (overlapsWith(trip)) {
            return false;
        }
        long diffMs = trip.getDepartureDate().getTime() - bookingDate.getTime();
        long twoHoursMs = 2 * 60 * 60 * 1000L;
        if (diffMs <= twoHoursMs) {
            return false;
        }
        return true;
    }

    public void updateTripSeats() {
        if (trip != null) {
            trip.setNumberOfSeats(trip.getNumberOfSeats() - numberOfSeats);
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

    public boolean overlapsWith(Trip trip) {
        if (trip == null || customer == null || customer.getBookings() == null) {
            return false;
        }
        for (Booking b : customer.getBookings()) {
            if (b.getTrip() != null && b.getTrip() != trip) {
                if (b.getTrip().isTimeConflicting(trip.getDepartureTime(), trip.getArrivalTime())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH);
        return sdf.format(bookingDate).equals(month);
    }
}
