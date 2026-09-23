import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Booking {
    private int numberOfSeats;
    private Customer customer;
    private Trip trip;
    private Date bookingDate;

    public Booking() { }

    public boolean isBookingEligible() {
        if (trip == null || customer == null || bookingDate == null) {
            return false;
        }

        if (numberOfSeats <= 0 || trip.getNumberOfSeats() <= 0) {
            return false;
        }

        if (numberOfSeats > trip.getNumberOfSeats()) {
            return false;
        }

        List<Booking> customerBookings = customer.getBookings();
        if (customerBookings != null) {
            for (Booking other : customerBookings) {
                if (other != null && other != this && other.getTrip() != null) {
                    if (overlapsWith(other.getTrip())) {
                        return false;
                    }
                }
            }
        }

        if (!isMoreThanTwoHoursBeforeDeparture()) {
            return false;
        }

        return true;
    }

    public void updateTripSeats() {
        if (trip != null) {
            trip.setNumberOfSeats(trip.getNumberOfSeats() - numberOfSeats);
        }
    }

    public boolean overlapsWith(Trip trip) {
        if (this.trip == null || trip == null) {
            return false;
        }

        Date thisDate = this.trip.getDepartureDate();
        Date otherDate = trip.getDepartureDate();

        if (thisDate == null || otherDate == null || !thisDate.equals(otherDate)) {
            return false;
        }

        String thisDepTime = this.trip.getDepartureTime();
        String thisArrTime = this.trip.getArrivalTime();
        String otherDepTime = trip.getDepartureTime();
        String otherArrTime = trip.getArrivalTime();

        if (thisDepTime == null || thisArrTime == null || otherDepTime == null || otherArrTime == null) {
            return false;
        }

        int thisDep = parseTimeToMinutes(thisDepTime);
        int thisArr = parseTimeToMinutes(thisArrTime);
        int otherDep = parseTimeToMinutes(otherDepTime);
        int otherArr = parseTimeToMinutes(otherArrTime);

        return thisDep < otherArr && otherDep < thisArr;
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null) {
            return false;
        }
        try {
            String[] monthParts = month.split("-");
            int year = Integer.parseInt(monthParts[0]);
            int monthNum = Integer.parseInt(monthParts[1]);

            Calendar cal = Calendar.getInstance();
            cal.setTime(bookingDate);
            return cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) + 1 == monthNum;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isMoreThanTwoHoursBeforeDeparture() {
        if (trip == null || trip.getDepartureDate() == null || trip.getDepartureTime() == null) {
            return false;
        }

        Calendar depCal = Calendar.getInstance();
        depCal.setTime(trip.getDepartureDate());
        String[] parts = trip.getDepartureTime().split(":");
        if (parts.length < 2) {
            return false;
        }
        depCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
        depCal.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
        depCal.set(Calendar.SECOND, 0);
        depCal.set(Calendar.MILLISECOND, 0);

        long depMs = depCal.getTimeInMillis();
        long bookMs = bookingDate.getTime();
        long twoHoursMs = 2L * 60 * 60 * 1000L;

        return depMs - bookMs > twoHoursMs;
    }

    private int parseTimeToMinutes(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) {
            return -1;
        }
        try {
            String[] parts = timeStr.split(":");
            return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
        } catch (Exception e) {
            return -1;
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
}
