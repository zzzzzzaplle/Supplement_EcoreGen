import java.util.Date;
import java.util.Calendar;

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
        // Trip must exist
        if (trip.getDepartureDate() == null || trip.getDepartureTime() == null) {
            return false;
        }
        // Check enough seats available
        int bookedSeats = trip.getBookedSeats();
        int remaining = trip.getNumberOfSeats() - bookedSeats;
        if (numberOfSeats <= 0 || numberOfSeats > remaining) {
            return false;
        }
        // Check booking time is more than 2 hours before departure
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
            String depDateStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(trip.getDepartureDate());
            java.util.Date tripDeparture = sdf.parse(depDateStr + " " + trip.getDepartureTime());
            long diffMillis = tripDeparture.getTime() - bookingDate.getTime();
            long diffHours = diffMillis / (1000 * 60 * 60);
            // Strictly more than two hours
            if (diffHours <= 2) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        // Check no overlapping booking on same day
        if (customer.getBookings() != null) {
            for (Booking existing : customer.getBookings()) {
                if (existing == null || existing == this) {
                    continue;
                }
                if (existing.getTrip() == null) {
                    continue;
                }
                if (overlapsWith(existing.getTrip())) {
                    return false;
                }
            }
        }
        return true;
    }

    public void updateTripSeats() {
        // Seat reduction is handled via the bookings list on the trip
        // The booking will be added to the trip's bookings list
        // and getBookedSeats() will reflect the change
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
        if (trip == null || otherTrip == null) {
            return false;
        }
        if (trip.getDepartureDate() == null || otherTrip.getDepartureDate() == null) {
            return false;
        }
        // Check same day
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String thisDay = sdf.format(trip.getDepartureDate());
        String otherDay = sdf.format(otherTrip.getDepartureDate());
        if (!thisDay.equals(otherDay)) {
            return false;
        }
        // Check time overlap
        if (trip.getDepartureTime() == null || trip.getArrivalTime() == null
                || otherTrip.getDepartureTime() == null || otherTrip.getArrivalTime() == null) {
            return false;
        }
        try {
            int thisStart = timeToMinutes(trip.getDepartureTime());
            int thisEnd = timeToMinutes(trip.getArrivalTime());
            int otherStart = timeToMinutes(otherTrip.getDepartureTime());
            int otherEnd = timeToMinutes(otherTrip.getArrivalTime());
            return thisStart < otherEnd && thisEnd > otherStart;
        } catch (Exception e) {
            return false;
        }
    }

    private int timeToMinutes(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        return hours * 60 + minutes;
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null) {
            return false;
        }
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM");
        String bookingMonth = sdf.format(bookingDate);
        return bookingMonth.equals(month);
    }
}
