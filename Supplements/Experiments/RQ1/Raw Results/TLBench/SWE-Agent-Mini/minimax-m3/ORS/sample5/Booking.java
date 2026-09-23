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
        int remaining = trip.getNumberOfSeats() - trip.getBookedSeats();
        if (remaining < numberOfSeats) {
            return false;
        }
        if (customer.getBookings() != null) {
            for (Booking b : customer.getBookings()) {
                if (b == null || b == this) {
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
        String bookingTime = timeToString(bookingDate);
        if (bookingTime == null || trip.getDepartureTime() == null) {
            return false;
        }
        int bookingMinutes = toMinutes(bookingTime);
        int depMinutes = toMinutes(trip.getDepartureTime());
        if (bookingMinutes < 0 || depMinutes < 0) {
            return false;
        }
        int diff = depMinutes - bookingMinutes;
        if (diff <= 120) {
            return false;
        }
        return true;
    }

    private String timeToString(Date d) {
        if (d == null) {
            return null;
        }
        @SuppressWarnings("deprecation")
        int h = d.getHours();
        @SuppressWarnings("deprecation")
        int m = d.getMinutes();
        return String.format("%02d:%02d", h, m);
    }

    private int toMinutes(String t) {
        try {
            String[] parts = t.split(":");
            int h = Integer.parseInt(parts[0]);
            int m = Integer.parseInt(parts[1]);
            return h * 60 + m;
        } catch (Exception e) {
            return -1;
        }
    }

    public void updateTripSeats() {
        if (trip != null) {
            int newCount = trip.getNumberOfSeats() - numberOfSeats;
            trip.setNumberOfSeats(newCount);
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

    public boolean overlapsWith(Trip other) {
        if (other == null || trip == null) {
            return false;
        }
        if (trip.getDepartureDate() == null || other.getDepartureDate() == null) {
            return false;
        }
        if (!isSameDay(trip.getDepartureDate(), other.getDepartureDate())) {
            return false;
        }
        if (trip.getDepartureTime() == null || trip.getArrivalTime() == null
                || other.getDepartureTime() == null || other.getArrivalTime() == null) {
            return false;
        }
        int t1s = toMinutes(trip.getDepartureTime());
        int t1e = toMinutes(trip.getArrivalTime());
        int t2s = toMinutes(other.getDepartureTime());
        int t2e = toMinutes(other.getArrivalTime());
        if (t1s < 0 || t1e < 0 || t2s < 0 || t2e < 0) {
            return false;
        }
        return t1s < t2e && t2s < t1e;
    }

    private boolean isSameDay(java.util.Date a, java.util.Date b) {
        if (a == null || b == null) {
            return false;
        }
        @SuppressWarnings("deprecation")
        java.util.Calendar ca = java.util.Calendar.getInstance();
        @SuppressWarnings("deprecation")
        java.util.Calendar cb = java.util.Calendar.getInstance();
        ca.setTime(a);
        cb.setTime(b);
        return ca.get(java.util.Calendar.YEAR) == cb.get(java.util.Calendar.YEAR)
                && ca.get(java.util.Calendar.MONTH) == cb.get(java.util.Calendar.MONTH)
                && ca.get(java.util.Calendar.DAY_OF_MONTH) == cb.get(java.util.Calendar.DAY_OF_MONTH);
    }

    public boolean isInMonth(String month) {
        if (month == null || bookingDate == null) {
            return false;
        }
        @SuppressWarnings("deprecation")
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(bookingDate);
        @SuppressWarnings("deprecation")
        int m = c.get(java.util.Calendar.MONTH) + 1;
        return month.equals(String.format("%02d", m)) || month.contains(String.format("%02d", m));
    }
}
