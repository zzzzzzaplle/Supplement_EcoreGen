import java.util.Date;
import java.util.Calendar;
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
        if (trip.getDepartureDate() == null || trip.getDepartureTime() == null) {
            return false;
        }
        if (trip.getNumberOfSeats() <= 0) {
            return false;
        }
        if (numberOfSeats <= 0) {
            return false;
        }

        int available = trip.getNumberOfSeats() - trip.getBookedSeats();
        if (numberOfSeats > available) {
            return false;
        }

        Date departureDateTime = combineDateTime(trip.getDepartureDate(), trip.getDepartureTime());
        if (departureDateTime == null) {
            return false;
        }

        long diff = departureDateTime.getTime() - bookingDate.getTime();
        long twoHoursMs = 2L * 60L * 60L * 1000L;
        if (diff <= twoHoursMs) {
            return false;
        }

        List<Booking> customerBookings = customer.getBookings();
        if (customerBookings != null) {
            for (Booking existing : customerBookings) {
                if (existing == null || existing == this) {
                    continue;
                }
                if (existing.overlapsWith(trip)) {
                    return false;
                }
            }
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
        if (trip == null || otherTrip == null) {
            return false;
        }
        if (trip.getDepartureDate() == null || otherTrip.getDepartureDate() == null) {
            return false;
        }
        if (trip.getDepartureTime() == null || otherTrip.getDepartureTime() == null) {
            return false;
        }
        if (trip.getArrivalTime() == null || otherTrip.getArrivalTime() == null) {
            return false;
        }

        Calendar c1 = Calendar.getInstance();
        c1.setTime(trip.getDepartureDate());
        Calendar c2 = Calendar.getInstance();
        c2.setTime(otherTrip.getDepartureDate());

        if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR)
                || c1.get(Calendar.DAY_OF_YEAR) != c2.get(Calendar.DAY_OF_YEAR)) {
            return false;
        }

        return trip.isTimeConflicting(otherTrip.getDepartureTime(), otherTrip.getArrivalTime());
    }

    public boolean isInMonth(String month) {
        if (month == null || bookingDate == null) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String bookingMonth = sdf.format(bookingDate);
        return bookingMonth.equals(month);
    }

    private Date combineDateTime(Date date, String time) {
        if (date == null || time == null) {
            return null;
        }
        String[] parts = time.split(":");
        if (parts.length < 2) {
            return null;
        }
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0].trim()));
            cal.set(Calendar.MINUTE, Integer.parseInt(parts[1].trim()));
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTime();
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
