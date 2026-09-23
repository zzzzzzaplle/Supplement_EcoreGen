import java.util.Date;

public class Booking {
    private int numberOfSeats;
    private Customer customer;
    private Trip trip;
    private Date bookingDate;

    public Booking() {
    }

    public Booking(int numberOfSeats, Customer customer, Trip trip, Date bookingDate) {
        this.numberOfSeats = numberOfSeats;
        this.customer = customer;
        this.trip = trip;
        this.bookingDate = bookingDate;
    }

    public boolean isBookingEligible() {
        if (customer == null || trip == null || bookingDate == null) {
            return false;
        }
        if (numberOfSeats <= 0) {
            return false;
        }
        if (numberOfSeats > trip.getNumberOfSeats()) {
            return false;
        }
        for (Booking existing : customer.getBookings()) {
            if (existing == null || existing == this) {
                continue;
            }
            if (existing.overlapsWith(trip)) {
                return false;
            }
        }
        Date departureDateTime = combineDateTime(trip.getDepartureDate(), trip.getDepartureTime());
        if (departureDateTime == null) {
            return false;
        }
        long diff = departureDateTime.getTime() - bookingDate.getTime();
        long twoHours = 2L * 60L * 60L * 1000L;
        if (diff <= twoHours) {
            return false;
        }
        return true;
    }

    public void updateTripSeats() {
        if (trip != null) {
            trip.setNumberOfSeats(trip.getNumberOfSeats() - numberOfSeats);
        }
    }

    public boolean overlapsWith(Trip other) {
        if (other == null || trip == null) {
            return false;
        }
        Date d1 = trip.getDepartureDate();
        Date d2 = other.getDepartureDate();
        if (d1 == null || d2 == null) {
            return false;
        }
        if (!isSameDay(d1, d2)) {
            return false;
        }
        return trip.isTimeConflicting(other.getDepartureTime(), other.getArrivalTime());
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null) {
            return false;
        }
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM");
        String m = sdf.format(bookingDate);
        return m.equals(month);
    }

    private static Date combineDateTime(Date date, String time) {
        if (date == null || time == null) {
            return null;
        }
        try {
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
            String dateStr = dateFormat.format(date);
            java.text.SimpleDateFormat dtFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
            return dtFormat.parse(dateStr + " " + time);
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean isSameDay(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            return false;
        }
        java.util.Calendar c1 = java.util.Calendar.getInstance();
        c1.setTime(d1);
        java.util.Calendar c2 = java.util.Calendar.getInstance();
        c2.setTime(d2);
        return c1.get(java.util.Calendar.YEAR) == c2.get(java.util.Calendar.YEAR)
                && c1.get(java.util.Calendar.MONTH) == c2.get(java.util.Calendar.MONTH)
                && c1.get(java.util.Calendar.DAY_OF_MONTH) == c2.get(java.util.Calendar.DAY_OF_MONTH);
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
