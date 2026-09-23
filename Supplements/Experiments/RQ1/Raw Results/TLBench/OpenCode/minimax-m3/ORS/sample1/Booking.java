import java.util.Date;
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
        if (customer == null || trip == null || bookingDate == null) {
            return false;
        }
        if (numberOfSeats <= 0) {
            return false;
        }
        int bookedSeats = trip.getBookedSeats();
        int available = trip.getNumberOfSeats() - bookedSeats;
        if (numberOfSeats > available) {
            return false;
        }
        if (customer.getBookings() != null) {
            for (Booking b : customer.getBookings()) {
                if (b == null || b == this) {
                    continue;
                }
                if (b.getTrip() != null && b.overlapsWith(trip)) {
                    return false;
                }
            }
        }
        Date tripDeparture = combineDateTime(trip.getDepartureDate(), trip.getDepartureTime());
        if (tripDeparture == null) {
            return false;
        }
        long diff = tripDeparture.getTime() - bookingDate.getTime();
        if (diff <= 2L * 60 * 60 * 1000) {
            return false;
        }
        trip.addBooking(this);
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
        if (!isSameDay(trip.getDepartureDate(), otherTrip.getDepartureDate())) {
            return false;
        }
        return trip.isTimeConflicting(otherTrip.getDepartureTime(), otherTrip.getArrivalTime());
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        return month.equals(sdf.format(bookingDate));
    }

    private boolean isSameDay(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(d1).equals(sdf.format(d2));
    }

    private Date combineDateTime(Date date, String time) {
        if (date == null || time == null) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(dateFmt.format(date) + " " + time);
        } catch (Exception e) {
            return null;
        }
    }
}
