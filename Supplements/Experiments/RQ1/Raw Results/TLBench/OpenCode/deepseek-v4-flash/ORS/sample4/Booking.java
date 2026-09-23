import java.util.Date;
import java.util.List;

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
        int remainingSeats = trip.getNumberOfSeats() - trip.getBookedSeats();
        if (remainingSeats <= 0 || numberOfSeats > remainingSeats) {
            return false;
        }
        List<Booking> existingBookings = customer.getBookings();
        if (existingBookings != null) {
            for (Booking b : existingBookings) {
                if (b.overlapsWith(trip)) {
                    return false;
                }
            }
        }
        Date now = new Date();
        long diffMs = trip.getDepartureDate().getTime() - now.getTime();
        long diffHours = diffMs / (60 * 60 * 1000);
        if (diffHours <= 2) {
            return false;
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
        if (otherTrip == null || this.trip == null) {
            return false;
        }
        Date thisDate = this.trip.getDepartureDate();
        Date otherDate = otherTrip.getDepartureDate();
        if (thisDate == null || otherDate == null) {
            return false;
        }
        if (!isSameDay(thisDate, otherDate)) {
            return false;
        }
        String thisDepart = this.trip.getDepartureTime();
        String thisArrive = this.trip.getArrivalTime();
        String otherDepart = otherTrip.getDepartureTime();
        String otherArrive = otherTrip.getArrivalTime();

        return timesOverlap(thisDepart, thisArrive, otherDepart, otherArrive);
    }

    private boolean isSameDay(Date d1, Date d2) {
        java.util.Calendar cal1 = java.util.Calendar.getInstance();
        cal1.setTime(d1);
        java.util.Calendar cal2 = java.util.Calendar.getInstance();
        cal2.setTime(d2);
        return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR)
                && cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR);
    }

    private boolean timesOverlap(String d1, String a1, String d2, String a2) {
        if (d1 == null || a1 == null || d2 == null || a2 == null) {
            return false;
        }
        String[] d1Parts = d1.split(":");
        String[] a1Parts = a1.split(":");
        String[] d2Parts = d2.split(":");
        String[] a2Parts = a2.split(":");
        if (d1Parts.length != 2 || a1Parts.length != 2 || d2Parts.length != 2 || a2Parts.length != 2) {
            return false;
        }
        try {
            int d1Min = Integer.parseInt(d1Parts[0]) * 60 + Integer.parseInt(d1Parts[1]);
            int a1Min = Integer.parseInt(a1Parts[0]) * 60 + Integer.parseInt(a1Parts[1]);
            int d2Min = Integer.parseInt(d2Parts[0]) * 60 + Integer.parseInt(d2Parts[1]);
            int a2Min = Integer.parseInt(a2Parts[0]) * 60 + Integer.parseInt(a2Parts[1]);

            return d1Min < a2Min && d2Min < a1Min;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null) {
            return false;
        }
        String[] parts = month.split("-");
        if (parts.length != 2) {
            return false;
        }
        try {
            int targetYear = Integer.parseInt(parts[0]);
            int targetMonth = Integer.parseInt(parts[1]);
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(bookingDate);
            int bookingYear = cal.get(java.util.Calendar.YEAR);
            int bookingMonth = cal.get(java.util.Calendar.MONTH) + 1;
            return targetYear == bookingYear && targetMonth == bookingMonth;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
