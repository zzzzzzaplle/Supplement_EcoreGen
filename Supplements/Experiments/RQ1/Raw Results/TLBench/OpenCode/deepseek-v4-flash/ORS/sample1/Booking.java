import java.util.Date;
import java.util.Calendar;

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
        if (numberOfSeats <= 0 || numberOfSeats > remainingSeats) {
            return false;
        }
        if (overlapsWith(trip)) {
            return false;
        }
        Date depDate = trip.getDepartureDate();
        if (depDate == null) {
            return false;
        }
        Calendar bookingCal = Calendar.getInstance();
        bookingCal.setTime(bookingDate);
        Calendar depCal = Calendar.getInstance();
        depCal.setTime(depDate);

        String[] depTimeParts = trip.getDepartureTime() != null ? trip.getDepartureTime().split(":") : new String[0];
        if (depTimeParts.length >= 2) {
            try {
                depCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(depTimeParts[0]));
                depCal.set(Calendar.MINUTE, Integer.parseInt(depTimeParts[1]));
                depCal.set(Calendar.SECOND, 0);
                depCal.set(Calendar.MILLISECOND, 0);
            } catch (NumberFormatException e) {
                return false;
            }
        }

        long diffMillis = depCal.getTimeInMillis() - bookingCal.getTimeInMillis();
        long twoHoursMillis = 2 * 60 * 60 * 1000L;
        if (diffMillis <= twoHoursMillis) {
            return false;
        }
        return true;
    }

    public void updateTripSeats() {
        if (trip != null) {
            trip.setNumberOfSeats(trip.getNumberOfSeats() - numberOfSeats);
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
        if (otherTrip == null || otherTrip.getDepartureDate() == null || otherTrip.getDepartureTime() == null || otherTrip.getArrivalTime() == null) {
            return false;
        }
        if (trip == null || trip.getDepartureDate() == null || trip.getDepartureTime() == null || trip.getArrivalTime() == null) {
            return false;
        }
        Calendar calOtherDep = Calendar.getInstance();
        calOtherDep.setTime(otherTrip.getDepartureDate());
        String[] otherDepParts = otherTrip.getDepartureTime().split(":");
        String[] otherArrParts = otherTrip.getArrivalTime().split(":");
        if (otherDepParts.length < 2 || otherArrParts.length < 2) return false;
        try {
            calOtherDep.set(Calendar.HOUR_OF_DAY, Integer.parseInt(otherDepParts[0]));
            calOtherDep.set(Calendar.MINUTE, Integer.parseInt(otherDepParts[1]));
        } catch (NumberFormatException e) {
            return false;
        }
        Calendar calOtherArr = Calendar.getInstance();
        calOtherArr.setTime(otherTrip.getDepartureDate());
        try {
            calOtherArr.set(Calendar.HOUR_OF_DAY, Integer.parseInt(otherArrParts[0]));
            calOtherArr.set(Calendar.MINUTE, Integer.parseInt(otherArrParts[1]));
        } catch (NumberFormatException e) {
            return false;
        }

        if (calOtherArr.compareTo(calOtherDep) < 0) {
            calOtherArr.add(Calendar.DAY_OF_MONTH, 1);
        }

        Calendar calThisDep = Calendar.getInstance();
        calThisDep.setTime(trip.getDepartureDate());
        String[] thisDepParts = trip.getDepartureTime().split(":");
        String[] thisArrParts = trip.getArrivalTime().split(":");
        if (thisDepParts.length < 2 || thisArrParts.length < 2) return false;
        try {
            calThisDep.set(Calendar.HOUR_OF_DAY, Integer.parseInt(thisDepParts[0]));
            calThisDep.set(Calendar.MINUTE, Integer.parseInt(thisDepParts[1]));
        } catch (NumberFormatException e) {
            return false;
        }
        Calendar calThisArr = Calendar.getInstance();
        calThisArr.setTime(trip.getDepartureDate());
        try {
            calThisArr.set(Calendar.HOUR_OF_DAY, Integer.parseInt(thisArrParts[0]));
            calThisArr.set(Calendar.MINUTE, Integer.parseInt(thisArrParts[1]));
        } catch (NumberFormatException e) {
            return false;
        }

        if (calThisArr.compareTo(calThisDep) < 0) {
            calThisArr.add(Calendar.DAY_OF_MONTH, 1);
        }

        return calThisDep.compareTo(calOtherArr) < 0 && calThisArr.compareTo(calOtherDep) > 0;
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null) return false;
        Calendar cal = Calendar.getInstance();
        cal.setTime(bookingDate);
        String[] parts = month.split("-");
        if (parts.length == 2) {
            try {
                int m = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                return (cal.get(Calendar.MONTH) + 1) == m && cal.get(Calendar.YEAR) == y;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }
}
