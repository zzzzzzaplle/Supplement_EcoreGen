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
        if (trip.getDepartureTime() == null) {
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
        if (!isMoreThanTwoHoursBeforeDeparture()) {
            return false;
        }
        return true;
    }

    private boolean isMoreThanTwoHoursBeforeDeparture() {
        try {
            String[] parts = trip.getDepartureTime().split(":");
            int depHour = Integer.parseInt(parts[0]);
            int depMinute = 0;
            if (parts.length > 1) {
                depMinute = Integer.parseInt(parts[1]);
            }
            Calendar depCal = Calendar.getInstance();
            depCal.setTime(trip.getDepartureDate());
            depCal.set(Calendar.HOUR_OF_DAY, depHour);
            depCal.set(Calendar.MINUTE, depMinute);
            depCal.set(Calendar.SECOND, 0);
            depCal.set(Calendar.MILLISECOND, 0);
            long depMillis = depCal.getTimeInMillis();
            long bookingMillis = bookingDate.getTime();
            long diffMillis = depMillis - bookingMillis;
            return diffMillis > 2L * 60L * 60L * 1000L;
        } catch (Exception e) {
            return false;
        }
    }

    public void updateTripSeats() {
        if (trip != null) {
            int current = trip.getBookedSeats();
            trip.setNumberOfSeats(trip.getNumberOfSeats() - numberOfSeats);
            if (trip.getBookings() == null) {
                trip.setBookings(new java.util.ArrayList<Booking>());
            }
            trip.getBookings().add(this);
        }
    }

    public boolean overlapsWith(Trip otherTrip) {
        if (otherTrip == null || this.trip == null) {
            return false;
        }
        if (this.trip.getDepartureDate() == null || otherTrip.getDepartureDate() == null) {
            return false;
        }
        Calendar c1 = Calendar.getInstance();
        c1.setTime(this.trip.getDepartureDate());
        Calendar c2 = Calendar.getInstance();
        c2.setTime(otherTrip.getDepartureDate());
        if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR)
                || c1.get(Calendar.DAY_OF_YEAR) != c2.get(Calendar.DAY_OF_YEAR)) {
            return false;
        }
        if (this.trip.getDepartureTime() == null || this.trip.getArrivalTime() == null
                || otherTrip.getDepartureTime() == null || otherTrip.getArrivalTime() == null) {
            return false;
        }
        String a1 = this.trip.getDepartureTime();
        String a2 = this.trip.getArrivalTime();
        String b1 = otherTrip.getDepartureTime();
        String b2 = otherTrip.getArrivalTime();
        return a1.compareTo(b2) < 0 && b1.compareTo(a2) < 0;
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null) {
            return false;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(bookingDate);
        int m = c.get(Calendar.MONTH) + 1;
        String ms = m < 10 ? "0" + m : "" + m;
        return ms.equals(month) || ("" + m).equals(month);
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
