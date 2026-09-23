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

        int availableSeats = trip.getNumberOfSeats();
        int bookedSeats = trip.getBookedSeats();
        if (availableSeats - bookedSeats < numberOfSeats) {
            return false;
        }

        if (numberOfSeats <= 0) {
            return false;
        }

        List<Booking> existingBookings = customer.getBookings();
        if (existingBookings != null) {
            for (Booking existing : existingBookings) {
                if (existing != null && existing.overlapsWith(trip)) {
                    return false;
                }
            }
        }

        Date departureDate = trip.getDepartureDate();
        String departureTime = trip.getDepartureTime();
        if (departureDate == null || departureTime == null) {
            return false;
        }

        long twoHoursInMillis = 2L * 60 * 60 * 1000;
        long bookingTime = bookingDate.getTime();
        long departureTimeMs = parseTimeToMillis(departureDate, departureTime);
        if (departureTimeMs - bookingTime <= twoHoursInMillis) {
            return false;
        }

        return true;
    }

    public void updateTripSeats() {
        if (trip != null) {
            int remaining = trip.getNumberOfSeats() - trip.getBookedSeats();
            if (remaining >= numberOfSeats) {
                trip.setNumberOfSeats(trip.getNumberOfSeats() - numberOfSeats);
            }
        }
    }

    public boolean overlapsWith(Trip otherTrip) {
        if (otherTrip == null || trip == null) {
            return false;
        }
        Date thisDate = trip.getDepartureDate();
        Date otherDate = otherTrip.getDepartureDate();
        if (thisDate == null || otherDate == null) {
            return false;
        }
        if (!thisDate.equals(otherDate)) {
            return false;
        }
        String thisDep = trip.getDepartureTime();
        String thisArr = trip.getArrivalTime();
        String otherDep = otherTrip.getDepartureTime();
        String otherArr = otherTrip.getArrivalTime();
        if (thisDep == null || thisArr == null || otherDep == null || otherArr == null) {
            return false;
        }
        long thisDepMs = timeStringToMillis(thisDep);
        long thisArrMs = timeStringToMillis(thisArr);
        long otherDepMs = timeStringToMillis(otherDep);
        long otherArrMs = timeStringToMillis(otherArr);

        if (thisDepMs < otherArrMs && otherDepMs < thisArrMs) {
            return true;
        }
        return false;
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null) {
            return false;
        }
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM");
        String bookingMonth = sdf.format(bookingDate);
        return bookingMonth.equals(month);
    }

    private long parseTimeToMillis(Date date, String time) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);
        try {
            java.util.Date parsed = sdf.parse(dateStr + " " + time);
            return parsed.getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    private long timeStringToMillis(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return (long) hours * 3600000 + (long) minutes * 60000;
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
