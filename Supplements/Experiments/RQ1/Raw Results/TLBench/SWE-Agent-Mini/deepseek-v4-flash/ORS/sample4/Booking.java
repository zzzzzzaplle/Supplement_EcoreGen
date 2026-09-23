import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

public class Booking {
    private int numberOfSeats;
    private Customer customer;
    private Trip trip;
    private Date bookingDate;

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

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public boolean isBookingEligible() {
        // Invalid inputs
        if (customer == null || trip == null || bookingDate == null) {
            return false;
        }
        // Trip must exist (not null, checked above)
        // Enough available seats
        int availableSeats = trip.getNumberOfSeats() - trip.getBookedSeats();
        if (availableSeats <= 0 || numberOfSeats > availableSeats) {
            return false;
        }
        // Customer must not have overlapping booking on the same day
        List<Booking> customerBookings = customer.getBookings();
        if (customerBookings != null) {
            for (Booking b : customerBookings) {
                if (b != this && b.getTrip() != null) {
                    if (overlapsWith(b.getTrip())) {
                        return false;
                    }
                }
            }
        }
        // Booking made more than 2 hours before departure (strictly earlier)
        if (trip.getDepartureDate() == null || trip.getDepartureTime() == null) {
            return false;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String depDateTimeStr = new SimpleDateFormat("yyyy-MM-dd").format(trip.getDepartureDate()) + " " + trip.getDepartureTime();
            Date depDateTime = sdf.parse(depDateTimeStr);
            
            // Use bookingDate at midnight as the booking time
            String bookDateTimeStr = new SimpleDateFormat("yyyy-MM-dd").format(bookingDate) + " 00:00";
            Date bookDateTime = sdf.parse(bookDateTimeStr);
            
            long diffMillis = depDateTime.getTime() - bookDateTime.getTime();
            long diffHours = diffMillis / (1000 * 60 * 60);
            if (diffHours <= 2) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void updateTripSeats() {
        if (trip != null && isBookingEligible()) {
            trip.setNumberOfSeats(trip.getNumberOfSeats() - numberOfSeats);
        }
    }

    public boolean overlapsWith(Trip otherTrip) {
        if (trip == null || otherTrip == null) {
            return false;
        }
        // Check if on the same day (same departure date)
        if (trip.getDepartureDate() == null || otherTrip.getDepartureDate() == null) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String thisDate = sdf.format(trip.getDepartureDate());
        String otherDate = sdf.format(otherTrip.getDepartureDate());
        if (!thisDate.equals(otherDate)) {
            return false;
        }
        // Check time overlap
        return trip.isTimeConflicting(otherTrip.getDepartureTime(), otherTrip.getArrivalTime());
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
