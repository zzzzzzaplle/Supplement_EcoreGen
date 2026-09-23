import java.util.Calendar;
import java.util.Date;

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
        if (numberOfSeats <= 0) {
            return false;
        }
        if (trip.getDepartureDate() == null) {
            return false;
        }
        if (trip.getDepartureTime() == null || trip.getArrivalTime() == null) {
            return false;
        }

        if (!isMoreThanTwoHoursBeforeDeparture()) {
            return false;
        }

        int bookedSeats = trip.getBookedSeats();
        if (bookedSeats + numberOfSeats > trip.getNumberOfSeats()) {
            return false;
        }

        if (customer.getBookings() != null) {
            for (Booking b : customer.getBookings()) {
                if (b == this) {
                    continue;
                }
                if (b.getTrip() == null) {
                    continue;
                }
                if (b.overlapsWith(trip)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isMoreThanTwoHoursBeforeDeparture() {
        Calendar dep = Calendar.getInstance();
        dep.setTime(trip.getDepartureDate());
        String[] parts = trip.getDepartureTime().split(":");
        dep.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0].trim()));
        dep.set(Calendar.MINUTE, Integer.parseInt(parts[1].trim()));
        dep.set(Calendar.SECOND, 0);
        dep.set(Calendar.MILLISECOND, 0);

        long diff = dep.getTimeInMillis() - bookingDate.getTime();
        return diff > 2L * 60L * 60L * 1000L;
    }

    public void updateTripSeats() {
        if (trip != null && isBookingEligible()) {
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
        if (otherTrip == null || trip == null) {
            return false;
        }
        if (trip.getDepartureDate() == null || otherTrip.getDepartureDate() == null) {
            return false;
        }
        if (trip.getDepartureTime() == null || trip.getArrivalTime() == null) {
            return false;
        }
        if (otherTrip.getDepartureTime() == null || otherTrip.getArrivalTime() == null) {
            return false;
        }

        Calendar c1 = Calendar.getInstance();
        c1.setTime(trip.getDepartureDate());
        Calendar c2 = Calendar.getInstance();
        c2.setTime(otherTrip.getDepartureDate());

        if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR)) {
            return false;
        }
        if (c1.get(Calendar.DAY_OF_YEAR) != c2.get(Calendar.DAY_OF_YEAR)) {
            return false;
        }

        int t1Start = parseTime(trip.getDepartureTime());
        int t1End = parseTime(trip.getArrivalTime());
        int t2Start = parseTime(otherTrip.getDepartureTime());
        int t2End = parseTime(otherTrip.getArrivalTime());

        return t1Start < t2End && t2Start < t1End;
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null) {
            return false;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(bookingDate);

        if (month.contains("-")) {
            String[] parts = month.split("-");
            if (parts.length == 2) {
                try {
                    int year;
                    int mon;
                    if (parts[0].trim().length() == 4) {
                        year = Integer.parseInt(parts[0].trim());
                        mon = Integer.parseInt(parts[1].trim());
                    } else {
                        mon = Integer.parseInt(parts[0].trim());
                        year = Integer.parseInt(parts[1].trim());
                    }
                    return c.get(Calendar.YEAR) == year && (c.get(Calendar.MONTH) + 1) == mon;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }
        return false;
    }

    private int parseTime(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0].trim()) * 60 + Integer.parseInt(parts[1].trim());
    }
}
