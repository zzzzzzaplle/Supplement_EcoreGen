import java.util.Date;
import java.util.List;

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

        // Trip must exist (not null, already checked)
        // Enough available seats
        int availableSeats = trip.getNumberOfSeats() - trip.getBookedSeats();
        if (numberOfSeats <= 0 || numberOfSeats > availableSeats) {
            return false;
        }

        // Customer must not have overlapping booking on the same day
        List<Booking> customerBookings = customer.getBookings();
        if (customerBookings != null) {
            for (Booking existing : customerBookings) {
                if (existing != this && existing.getTrip() != null && overlapsWith(existing.getTrip())) {
                    return false;
                }
            }
        }

        // Booking must be made more than 2 hours before departure
        if (trip.getDepartureDate() == null || trip.getDepartureTime() == null) {
            return false;
        }

        // Compare booking date/time with departure date/time
        // Booking must be strictly earlier than departure time by more than 2 hours
        // i.e., departure - booking > 2 hours (120 minutes)
        java.util.Calendar depCal = java.util.Calendar.getInstance();
        depCal.setTime(trip.getDepartureDate());
        String[] depTimeParts = trip.getDepartureTime().split(":");
        int depHour = Integer.parseInt(depTimeParts[0]);
        int depMinute = Integer.parseInt(depTimeParts[1]);
        depCal.set(java.util.Calendar.HOUR_OF_DAY, depHour);
        depCal.set(java.util.Calendar.MINUTE, depMinute);
        depCal.set(java.util.Calendar.SECOND, 0);
        depCal.set(java.util.Calendar.MILLISECOND, 0);

        java.util.Calendar bookCal = java.util.Calendar.getInstance();
        bookCal.setTime(bookingDate);

        long diffMs = depCal.getTimeInMillis() - bookCal.getTimeInMillis();
        long diffMinutes = diffMs / (60 * 1000);

        // Must be strictly more than 2 hours (120 minutes)
        if (diffMinutes <= 120) {
            return false;
        }

        // All conditions met, booking is eligible
        // When booking is accepted, reduce trip seat inventory
        // Actually, we just validate. The seat reduction happens when accepted.
        // But the requirement says "When the booking is accepted, the trip seat inventory must be reduced accordingly."
        // We'll do it in bookTrip method on Customer.
        return true;
    }

    public void updateTripSeats() {
        if (trip != null && numberOfSeats > 0) {
            trip.setNumberOfSeats(trip.getNumberOfSeats() - numberOfSeats);
        }
    }

    public boolean overlapsWith(Trip otherTrip) {
        if (trip == null || otherTrip == null) {
            return false;
        }
        // Check if trips are on the same date
        if (trip.getDepartureDate() == null || otherTrip.getDepartureDate() == null) {
            return false;
        }
        // Compare dates
        if (!trip.getDepartureDate().equals(otherTrip.getDepartureDate())) {
            return false;
        }
        // Check time overlap
        // Two trips overlap if one starts before the other ends and vice versa
        String t1Dep = trip.getDepartureTime();
        String t1Arr = trip.getArrivalTime();
        String t2Dep = otherTrip.getDepartureTime();
        String t2Arr = otherTrip.getArrivalTime();
        
        if (t1Dep == null || t1Arr == null || t2Dep == null || t2Arr == null) {
            return false;
        }
        
        boolean thisStartsBeforeOtherEnds = t1Dep.compareTo(t2Arr) < 0;
        boolean thisEndsAfterOtherStarts = t1Arr.compareTo(t2Dep) > 0;
        return thisStartsBeforeOtherEnds && thisEndsAfterOtherStarts;
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null) {
            return false;
        }
        // month format: "yyyy-MM"
        try {
            String[] monthParts = month.split("-");
            int targetYear = Integer.parseInt(monthParts[0]);
            int targetMonth = Integer.parseInt(monthParts[1]);

            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(bookingDate);
            int bookYear = cal.get(java.util.Calendar.YEAR);
            int bookMonth = cal.get(java.util.Calendar.MONTH) + 1;

            return bookYear == targetYear && bookMonth == targetMonth;
        } catch (Exception e) {
            return false;
        }
    }
}
