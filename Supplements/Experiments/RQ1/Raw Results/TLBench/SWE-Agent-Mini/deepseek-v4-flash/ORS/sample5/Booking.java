import java.util.Date;
import java.util.List;
import java.util.Calendar;

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
        if (customer == null || trip == null || bookingDate == null) {
            return false;
        }
        if (trip.getNumberOfSeats() - trip.getBookedSeats() < numberOfSeats) {
            return false;
        }
        if (numberOfSeats <= 0) {
            return false;
        }
        if (numberOfSeats > trip.getAvailableSeats()) {
            return false;
        }

        List<Booking> customerBookings = customer.getBookings();
        for (Booking b : customerBookings) {
            if (b != this && b.getTrip() != null && b.getBookingDate() != null) {
                if (overlapsWith(b.getTrip())) {
                    return false;
                }
            }
        }

        long bookingMillis = bookingDate.getTime();
        Calendar depCal = Calendar.getInstance();
        depCal.setTime(trip.getDepartureDate());
        String[] timeParts = trip.getDepartureTime().split(":");
        depCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
        depCal.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
        depCal.set(Calendar.SECOND, 0);
        depCal.set(Calendar.MILLISECOND, 0);
        long depMillis = depCal.getTimeInMillis();

        long diff = depMillis - bookingMillis;
        long twoHours = 2 * 60 * 60 * 1000L;

        if (diff <= twoHours) {
            return false;
        }

        updateTripSeats();
        return true;
    }

    public void updateTripSeats() {
        if (trip != null) {
            trip.addBooking(this);
        }
    }

    public boolean overlapsWith(Trip otherTrip) {
        if (trip == null || otherTrip == null) {
            return false;
        }
        if (!trip.getDepartureDate().equals(otherTrip.getDepartureDate())) {
            return false;
        }
        String dt1 = trip.getDepartureTime();
        String at1 = trip.getArrivalTime();
        String dt2 = otherTrip.getDepartureTime();
        String at2 = otherTrip.getArrivalTime();

        if (dt1 == null || at1 == null || dt2 == null || at2 == null) {
            return false;
        }
        int d1 = Integer.parseInt(dt1.replace(":", ""));
        int a1 = Integer.parseInt(at1.replace(":", ""));
        int d2 = Integer.parseInt(dt2.replace(":", ""));
        int a2 = Integer.parseInt(at2.replace(":", ""));

        if (d1 < a2 && d2 < a1) {
            return true;
        }
        return false;
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null) {
            return false;
        }
        String[] parts = month.split("-");
        if (parts.length < 2) {
            return false;
        }
        int monthYear = Integer.parseInt(parts[0]);
        int monthMonth = Integer.parseInt(parts[1]);
        Calendar cal = Calendar.getInstance();
        cal.setTime(bookingDate);
        int calYear = cal.get(Calendar.YEAR);
        int calMonth = cal.get(Calendar.MONTH) + 1;
        return calYear == monthYear && calMonth == monthMonth;
    }
}
