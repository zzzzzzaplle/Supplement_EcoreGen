import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Locale;

class Booking {
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

        // Check trip exists and has enough seats
        if (trip.getNumberOfSeats() - trip.getBookedSeats() < numberOfSeats) {
            return false;
        }

        // Check booking is more than 2 hours before departure
        if (!isMoreThanTwoHoursBeforeDeparture()) {
            return false;
        }

        // Check no overlapping booking on the same day
        if (hasOverlappingBooking()) {
            return false;
        }

        return true;
    }

    private boolean isMoreThanTwoHoursBeforeDeparture() {
        if (trip.getDepartureDate() == null || trip.getDepartureTime() == null) {
            return false;
        }

        try {
            Calendar departureCal = Calendar.getInstance();
            departureCal.setTime(trip.getDepartureDate());
            String[] parts = trip.getDepartureTime().split(":");
            departureCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
            if (parts.length >= 2) {
                departureCal.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
            } else {
                departureCal.set(Calendar.MINUTE, 0);
            }
            departureCal.set(Calendar.SECOND, 0);
            departureCal.set(Calendar.MILLISECOND, 0);

            Calendar bookingCal = Calendar.getInstance();
            bookingCal.setTime(bookingDate);

            Calendar cutoffCal = (Calendar) departureCal.clone();
            cutoffCal.add(Calendar.HOUR_OF_DAY, -2);

            // Booking must be strictly before cutoff (strictly earlier by more than 2 hours)
            return bookingCal.getTimeInMillis() < cutoffCal.getTimeInMillis();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean hasOverlappingBooking() {
        if (customer == null || customer.getBookings() == null) {
            return false;
        }

        for (Booking existing : customer.getBookings()) {
            if (existing == null || existing == this) {
                continue;
            }
            if (existing.getTrip() != null && existing.getTrip().getDepartureDate() != null &&
                trip.getDepartureDate() != null) {
                // Same day check first
                if (!isSameDay(existing.getTrip().getDepartureDate(), trip.getDepartureDate())) {
                    continue;
                }
                // Check time overlap
                if (overlapsWith(existing.getTrip())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
               cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    public void updateTripSeats() {
        if (trip != null) {
            // Seats are already reserved when booking is eligible
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

    public boolean overlapsWith(Trip trip) {
        if (this.trip == null || this.trip.getDepartureTime() == null ||
            this.trip.getArrivalTime() == null ||
            trip == null || trip.getDepartureTime() == null ||
            trip.getArrivalTime() == null) {
            return false;
        }

        String thisStart = toComparableTime(this.trip.getDepartureDate(), this.trip.getDepartureTime());
        String thisEnd = toComparableTime(this.trip.getDepartureDate(), this.trip.getArrivalTime());
        String otherStart = toComparableTime(trip.getDepartureDate(), trip.getDepartureTime());
        String otherEnd = toComparableTime(trip.getDepartureDate(), trip.getArrivalTime());

        boolean thisStartBeforeOtherEnd = thisStart.compareTo(otherEnd) < 0;
        boolean thisEndAfterOtherStart = thisEnd.compareTo(otherStart) > 0;

        return thisStartBeforeOtherEnd && thisEndAfterOtherStart;
    }

    private String toComparableTime(Date date, String time) {
        if (date == null || time == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm", Locale.US);
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            String[] parts = time.split(":");
            if (parts.length >= 2) {
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
                if (parts.length >= 3) {
                    cal.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
                }
            }
            return sdf.format(cal.getTime());
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null || month.isEmpty()) {
            return false;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM", Locale.US);
            String bookingMonthStr = sdf.format(bookingDate);
            return bookingMonthStr.equals(month);
        } catch (Exception e) {
            return false;
        }
    }
}
