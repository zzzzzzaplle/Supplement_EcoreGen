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
        int availableSeats = trip.getNumberOfSeats() - trip.getBookedSeats();
        if (availableSeats <= 0 || numberOfSeats > availableSeats) {
            return false;
        }
        if (!isMoreThanTwoHoursBeforeDeparture()) {
            return false;
        }
        if (overlapsWithSameDay()) {
            return false;
        }
        return true;
    }

    private boolean isMoreThanTwoHoursBeforeDeparture() {
        if (trip.getDepartureDate() == null || trip.getDepartureTime() == null) {
            return false;
        }
        try {
            String[] depParts = trip.getDepartureTime().split(":");
            int depHour = Integer.parseInt(depParts[0]);
            int depMin = Integer.parseInt(depParts[1]);

            Calendar depCal = Calendar.getInstance();
            depCal.setTime(trip.getDepartureDate());
            depCal.set(Calendar.HOUR_OF_DAY, depHour);
            depCal.set(Calendar.MINUTE, depMin);
            depCal.set(Calendar.SECOND, 0);
            depCal.set(Calendar.MILLISECOND, 0);

            Calendar bookCal = Calendar.getInstance();
            bookCal.setTime(bookingDate);

            long diffMillis = depCal.getTimeInMillis() - bookCal.getTimeInMillis();
            long diffHours = diffMillis / (1000 * 60 * 60);
            if (diffHours <= 2) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean overlapsWithSameDay() {
        Calendar thisBookCal = Calendar.getInstance();
        thisBookCal.setTime(bookingDate);
        int thisBookDay = thisBookCal.get(Calendar.DAY_OF_YEAR);
        int thisBookYear = thisBookCal.get(Calendar.YEAR);

        for (Booking b : customer.getBookings()) {
            if (b == this || b.getTrip() == null || b.getBookingDate() == null) {
                continue;
            }
            Calendar otherBookCal = Calendar.getInstance();
            otherBookCal.setTime(b.getBookingDate());
            int otherBookDay = otherBookCal.get(Calendar.DAY_OF_YEAR);
            int otherBookYear = otherBookCal.get(Calendar.YEAR);

            if (thisBookYear == otherBookYear && thisBookDay == otherBookDay) {
                if (b.overlapsWith(trip)) {
                    return true;
                }
            }
        }
        return false;
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
        if (otherTrip == null || trip == null ||
            trip.getDepartureTime() == null || trip.getArrivalTime() == null ||
            otherTrip.getDepartureTime() == null || otherTrip.getArrivalTime() == null) {
            return false;
        }
        try {
            String[] dep1Parts = trip.getDepartureTime().split(":");
            int dep1Hour = Integer.parseInt(dep1Parts[0]);
            int dep1Min = Integer.parseInt(dep1Parts[1]);

            String[] arr1Parts = trip.getArrivalTime().split(":");
            int arr1Hour = Integer.parseInt(arr1Parts[0]);
            int arr1Min = Integer.parseInt(arr1Parts[1]);

            String[] dep2Parts = otherTrip.getDepartureTime().split(":");
            int dep2Hour = Integer.parseInt(dep2Parts[0]);
            int dep2Min = Integer.parseInt(dep2Parts[1]);

            String[] arr2Parts = otherTrip.getArrivalTime().split(":");
            int arr2Hour = Integer.parseInt(arr2Parts[0]);
            int arr2Min = Integer.parseInt(arr2Parts[1]);

            int trip1Dep = dep1Hour * 60 + dep1Min;
            int trip1Arr = arr1Hour * 60 + arr1Min;
            int trip2Dep = dep2Hour * 60 + dep2Min;
            int trip2Arr = arr2Hour * 60 + arr2Min;

            if (trip2Dep < trip1Dep) {
                if (trip2Arr <= trip1Dep) {
                    return false;
                }
                return true;
            } else if (trip2Dep > trip1Arr) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null) {
            return false;
        }
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(bookingDate);
            String monthStr = (cal.get(Calendar.MONTH) + 1) + "";
            String yearStr = cal.get(Calendar.YEAR) + "";
            String targetMonth = month.trim();
            if (targetMonth.length() == 1) {
                targetMonth = "0" + targetMonth;
            }
            String bookingMonth = monthStr.length() == 1 ? "0" + monthStr : monthStr;
            return bookingMonth.equals(targetMonth) && yearStr.equals(yearStr);
        } catch (Exception e) {
            return false;
        }
    }
}