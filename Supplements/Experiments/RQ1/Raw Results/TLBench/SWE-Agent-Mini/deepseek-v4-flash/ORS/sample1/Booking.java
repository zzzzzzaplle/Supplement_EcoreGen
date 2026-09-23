import java.text.SimpleDateFormat;
import java.util.Date;

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
        // Invalid inputs check
        if (customer == null || trip == null || bookingDate == null) {
            return false;
        }

        // Trip must exist (trip is not null, already checked)
        // Enough available seats
        int bookedSeats = trip.getBookedSeats();
        int availableSeats = trip.getNumberOfSeats() - bookedSeats;
        if (numberOfSeats <= 0 || numberOfSeats > availableSeats) {
            return false;
        }

        // Customer must not have overlapping booking on same day
        for (Booking existingBooking : customer.getBookings()) {
            if (existingBooking.getTrip() != null && existingBooking.overlapsWith(trip)) {
                return false;
            }
        }

        // Booking made more than 2 hours before departure
        if (trip.getDepartureTime() == null || trip.getDepartureDate() == null) {
            return false;
        }

        try {
            // Compare times using minutes since midnight
            String[] depParts = trip.getDepartureTime().split(":");
            int depHour = Integer.parseInt(depParts[0]);
            int depMin = Integer.parseInt(depParts[1]);
            int depTotalMin = depHour * 60 + depMin;

            // Booking time is bookingDate hours and minutes
            // We need to compare booking time vs departure time
            // Since bookingDate is a Date, we'll extract hours and minutes
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String bookingTimeStr = sdf.format(bookingDate);
            String[] bookParts = bookingTimeStr.split(":");
            int bookHour = Integer.parseInt(bookParts[0]);
            int bookMin = Integer.parseInt(bookParts[1]);
            int bookTotalMin = bookHour * 60 + bookMin;

            // Also need to check if same day - booking must be on same day as departure or earlier
            // For simplicity, compare the dates
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            int depDateInt = Integer.parseInt(dateFormat.format(trip.getDepartureDate()));
            int bookDateInt = Integer.parseInt(dateFormat.format(bookingDate));

            if (bookDateInt > depDateInt) {
                return false; // booking after departure
            }

            int totalMinutesBeforeDep = 0;
            if (bookDateInt < depDateInt) {
                // Booking on a different (earlier) day, definitely more than 2 hours
                totalMinutesBeforeDep = 24 * 60 + (depTotalMin - bookTotalMin); // at least 24h
            } else {
                // Same day
                totalMinutesBeforeDep = depTotalMin - bookTotalMin;
            }

            // Strictly more than 2 hours (120 minutes)
            if (totalMinutesBeforeDep <= 120) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public void updateTripSeats() {
        if (trip != null) {
            // The trip seat inventory is reduced when booking is accepted
            // This is handled by the booking eligibility + booking process
            // The numberOfSeats field reflects the requested seats
            // The available seats = trip.numberOfSeats - trip.getBookedSeats()
            // So we just need to add this booking to the trip's bookings list
            // which is done when booking is created
        }
    }

    public boolean overlapsWith(Trip otherTrip) {
        if (this.trip == null || otherTrip == null) {
            return false;
        }
        // Check if same day
        if (this.trip.getDepartureDate() != null && otherTrip.getDepartureDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String thisDate = sdf.format(this.trip.getDepartureDate());
            String otherDate = sdf.format(otherTrip.getDepartureDate());
            if (!thisDate.equals(otherDate)) {
                return false;
            }
        } else {
            return false;
        }

        // Check time overlap
        try {
            String[] thisDepParts = this.trip.getDepartureTime().split(":");
            String[] thisArrParts = this.trip.getArrivalTime().split(":");
            String[] otherDepParts = otherTrip.getDepartureTime().split(":");
            String[] otherArrParts = otherTrip.getArrivalTime().split(":");

            int thisDep = Integer.parseInt(thisDepParts[0]) * 60 + Integer.parseInt(thisDepParts[1]);
            int thisArr = Integer.parseInt(thisArrParts[0]) * 60 + Integer.parseInt(thisArrParts[1]);
            int otherDep = Integer.parseInt(otherDepParts[0]) * 60 + Integer.parseInt(otherDepParts[1]);
            int otherArr = Integer.parseInt(otherArrParts[0]) * 60 + Integer.parseInt(otherArrParts[1]);

            // Overlap if time ranges intersect (not just adjacent)
            return thisDep < otherArr && otherDep < thisArr;
        } catch (Exception e) {
            return false;
        }
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
