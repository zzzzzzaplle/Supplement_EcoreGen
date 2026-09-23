import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Booking {
    private int numberOfSeats;
    private Customer customer;
    private Trip trip;
    private Date bookingDate;

    public Booking() {
    }

    public boolean isBookingEligible() {
        if (this.trip == null || this.customer == null || this.bookingDate == null) {
            return false;
        }
        int availableSeats = this.trip.getNumberOfSeats() - this.trip.getBookedSeats();
        if (availableSeats < this.numberOfSeats || this.numberOfSeats <= 0) {
            return false;
        }
        Date departureDate = this.trip.getDepartureDate();
        if (departureDate == null) {
            return false;
        }
        long bookingMillis = this.bookingDate.getTime();
        long departureMillis = departureDate.getTime();
        long twoHoursMilliseconds = 2L * 60 * 60 * 1000;
        if ((departureMillis - bookingMillis) <= twoHoursMilliseconds) {
            return false;
        }
        Calendar calDayStart = Calendar.getInstance();
        calDayStart.setTime(this.bookingDate);
        calDayStart.set(Calendar.HOUR_OF_DAY, 0);
        calDayStart.set(Calendar.MINUTE, 0);
        calDayStart.set(Calendar.SECOND, 0);
        calDayStart.set(Calendar.MILLISECOND, 0);
        Calendar calDayEnd = Calendar.getInstance();
        calDayEnd.setTime(this.bookingDate);
        calDayEnd.set(Calendar.HOUR_OF_DAY, 23);
        calDayEnd.set(Calendar.MINUTE, 59);
        calDayEnd.set(Calendar.SECOND, 59);
        calDayEnd.set(Calendar.MILLISECOND, 999);
        Date dayStart = calDayStart.getTime();
        Date dayEnd = calDayEnd.getTime();
        for (Booking otherBooking : this.customer.getBookings()) {
            if (otherBooking == null || otherBooking == this
                    || otherBooking.getTrip() == null) {
                continue;
            }
            Date otherDeparture = otherBooking.getTrip().getDepartureDate();
            if (otherDeparture == null) {
                continue;
            }
            if (otherDeparture.compareTo(dayStart) >= 0
                    && otherDeparture.compareTo(dayEnd) <= 0) {
                if (this.overlapsWith(otherBooking.getTrip())) {
                    return false;
                }
            }
        }
        return true;
    }

    public void updateTripSeats() {
        if (this.trip != null) {
            this.trip.setNumberOfSeats(
                this.trip.getNumberOfSeats() - this.numberOfSeats);
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
        if (this.trip == null || trip == null) {
            return false;
        }
        String myDepTimeStr = this.trip.getDepartureTime();
        String myArrTimeStr = this.trip.getArrivalTime();
        String otherDepTimeStr = trip.getDepartureTime();
        String otherArrTimeStr = trip.getArrivalTime();
        if (myDepTimeStr == null || myArrTimeStr == null
                || otherDepTimeStr == null || otherArrTimeStr == null) {
            return false;
        }
        int myDepSecs = parseTimeToSeconds(myDepTimeStr);
        int myArrSecs = parseTimeToSeconds(myArrTimeStr);
        int otherDepSecs = parseTimeToSeconds(otherDepTimeStr);
        int otherArrSecs = parseTimeToSeconds(otherArrTimeStr);
        if (myDepSecs < 0 || myArrSecs < 0
                || otherDepSecs < 0 || otherArrSecs < 0) {
            return false;
        }
        if (myDepSecs < otherArrSecs && myArrSecs > otherDepSecs) {
            return true;
        }
        return false;
    }

    public boolean isInMonth(String month) {
        if (month == null || this.bookingDate == null) {
            return false;
        }
        try {
            String prefix = month + "-";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String bookingDateStr = sdf.format(this.bookingDate);
            return bookingDateStr.startsWith(prefix);
        } catch (Exception e) {
            return false;
        }
    }

    private int parseTimeToSeconds(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) {
            return -1;
        }
        try {
            String[] parts = timeStr.split(":");
            if (parts.length != 2) {
                return -1;
            }
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59) {
                return -1;
            }
            return hours * 3600 + minutes * 60;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
