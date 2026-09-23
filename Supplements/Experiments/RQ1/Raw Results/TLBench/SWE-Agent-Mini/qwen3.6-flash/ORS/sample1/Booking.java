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

    public boolean isBookingEligible() {
        if (customer == null || trip == null || bookingDate == null) {
            return false;
        }
        if (!trip.isSeatAvailable(numberOfSeats)) {
            return false;
        }
        if (!bookingMadeInTime()) {
            return false;
        }
        if (!isNoOverlappingBooking()) {
            return false;
        }
        return true;
    }

    private boolean bookingMadeInTime() {
        String depTime = trip.getDepartureTime();
        String depDate = new SimpleDateFormat("yyyy-MM-dd").format(trip.getDepartureDate());
        String bktTime = new SimpleDateFormat("HH:mm").format(bookingDate);
        String bktDate = new SimpleDateFormat("yyyy-MM-dd").format(bookingDate);

        if (!depDate.equals(bktDate)) {
            return true;
        }

        try {
            java.time.LocalTime depLocal = java.time.LocalTime.parse(depTime);
            java.time.LocalTime bktLocal = java.time.LocalTime.parse(bktTime);

            java.time.LocalDateTime depDt = java.time.LocalDateTime.of(
                java.time.LocalDate.parse(depDate), depLocal);
            java.time.LocalDateTime bktDt = java.time.LocalDateTime.of(
                java.time.LocalDate.parse(bktDate), bktLocal);

            long diffSeconds = java.time.Duration.between(bktDt, depDt).getSeconds();
            return diffSeconds > 7200;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isNoOverlappingBooking() {
        for (Booking b : customer.getBookings()) {
            if (b == this) continue;
            if (b.getTrip() == null) continue;
            if (b.overlapsWith(trip)) {
                return false;
            }
        }
        return true;
    }

    public void updateTripSeats() {
        if (trip != null) {
            trip.reduceBookedSeats(numberOfSeats);
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
        if (this.trip == null || this.trip.getDepartureDate() == null ||
            trip == null || trip.getDepartureDate() == null) {
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date t1Start = sdf.parse(new SimpleDateFormat("yyyy-MM-dd").format(this.trip.getDepartureDate()) + " " + this.trip.getDepartureTime());
            Date t1End = sdf.parse(new SimpleDateFormat("yyyy-MM-dd").format(this.trip.getDepartureDate()) + " " + this.trip.getArrivalTime());
            Date t2Start = sdf.parse(new SimpleDateFormat("yyyy-MM-dd").format(trip.getDepartureDate()) + " " + trip.getDepartureTime());
            Date t2End = sdf.parse(new SimpleDateFormat("yyyy-MM-dd").format(trip.getDepartureDate()) + " " + trip.getArrivalTime());

            return t1Start.before(t2End) && t2Start.before(t1End);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null || month.isEmpty()) {
            return false;
        }
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        String bktMonth = monthFormat.format(bookingDate);
        return bktMonth.equals(month);
    }
}
