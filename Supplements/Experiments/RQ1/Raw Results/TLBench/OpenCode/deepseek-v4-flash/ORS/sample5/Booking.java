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
        int availableSeats = trip.getNumberOfSeats() - trip.getBookedSeats();
        if (availableSeats < numberOfSeats) {
            return false;
        }
        if (numberOfSeats <= 0) {
            return false;
        }
        if (!isMoreThanTwoHoursBefore()) {
            return false;
        }
        if (hasOverlappingBooking()) {
            return false;
        }
        return true;
    }

    private boolean isMoreThanTwoHoursBefore() {
        Date departureDateTime = combineDateAndTime(trip.getDepartureDate(), trip.getDepartureTime());
        long diffMs = departureDateTime.getTime() - bookingDate.getTime();
        long diffHours = diffMs / (60 * 60 * 1000);
        return diffHours > 2;
    }

    private boolean hasOverlappingBooking() {
        for (Booking existing : customer.getBookings()) {
            if (existing == this) {
                continue;
            }
            if (existing.getTrip() == null) {
                continue;
            }
            if (trip.getDepartureDate().equals(existing.getTrip().getDepartureDate()) && tripOverlaps(existing.getTrip())) {
                return true;
            }
        }
        return false;
    }

    private boolean tripOverlaps(Trip otherTrip) {
        if (otherTrip.getDepartureTime() == null || otherTrip.getArrivalTime() == null || trip.getDepartureTime() == null || trip.getArrivalTime() == null) {
            return false;
        }
        String t1dep = trip.getDepartureTime();
        String t1arr = trip.getArrivalTime();
        String t2dep = otherTrip.getDepartureTime();
        String t2arr = otherTrip.getArrivalTime();
        if (isEarlierOrEqual(t1arr, t1dep) || isEarlierOrEqual(t2arr, t2dep)) {
            return false;
        }
        if (isEarlierOrEqual(t1arr, t2dep) || isEarlierOrEqual(t2arr, t1dep)) {
            return false;
        }
        return true;
    }

    private boolean isEarlierOrEqual(String time1, String time2) {
        String[] parts1 = time1.split(":");
        String[] parts2 = time2.split(":");
        int h1 = Integer.parseInt(parts1[0]);
        int m1 = Integer.parseInt(parts1[1]);
        int h2 = Integer.parseInt(parts2[0]);
        int m2 = Integer.parseInt(parts2[1]);
        if (h1 != h2) {
            return h1 < h2;
        }
        return m1 <= m2;
    }

    private Date combineDateAndTime(Date date, String time) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(date);
        String[] parts = time.split(":");
        cal.set(java.util.Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
        cal.set(java.util.Calendar.MINUTE, Integer.parseInt(parts[1]));
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public void updateTripSeats() {
        int currentSeats = trip.getNumberOfSeats();
        trip.setNumberOfSeats(currentSeats - numberOfSeats);
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
        if (!trip.getDepartureDate().equals(otherTrip.getDepartureDate())) {
            return false;
        }
        return tripOverlaps(otherTrip);
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null) {
            return false;
        }
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM");
        String bookingMonth = sdf.format(bookingDate);
        return bookingMonth.equals(month);
    }
}
