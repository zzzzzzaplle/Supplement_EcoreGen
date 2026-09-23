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

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        try {
            String bookingMonth = sdf.format(bookingDate);
            return bookingMonth.equals(month);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean overlapsWith(Trip otherTrip) {
        if (otherTrip == null || this.trip == null) {
            return false;
        }
        if (this.trip.getDepartureDate() == null || otherTrip.getDepartureDate() == null) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String d1 = sdf.format(this.trip.getDepartureDate());
            String d2 = sdf.format(otherTrip.getDepartureDate());
            if (!d1.equals(d2)) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        int t1Dep = toMinutes(this.trip.getDepartureTime());
        int t1Arr = toMinutes(this.trip.getArrivalTime());
        int t2Dep = toMinutes(otherTrip.getDepartureTime());
        int t2Arr = toMinutes(otherTrip.getArrivalTime());
        return t1Dep < t2Arr && t2Dep < t1Arr;
    }

    private int toMinutes(String time) {
        if (time == null) {
            return 0;
        }
        try {
            String[] parts = time.split(":");
            return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean isBookingEligible() {
        if (customer == null || trip == null || bookingDate == null) {
            return false;
        }
        if (trip.getDepartureDate() == null || trip.getDepartureTime() == null) {
            return false;
        }

        int booked = trip.getBookedSeats();
        int remaining = trip.getNumberOfSeats() - booked;
        if (numberOfSeats <= 0 || numberOfSeats > remaining) {
            return false;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String dateStr = dateFormat.format(trip.getDepartureDate());
            String depTime = trip.getDepartureTime();
            String depDateTimeStr = dateStr + " " + depTime;
            SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date departureDateTime = dtFormat.parse(depDateTimeStr);

            long diffMs = departureDateTime.getTime() - bookingDate.getTime();
            long diffMinutes = diffMs / (60 * 1000);
            if (diffMinutes <= 120) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        if (customer.getBookings() != null) {
            for (Booking existing : customer.getBookings()) {
                if (existing != null && existing != this && existing.getTrip() != null) {
                    if (existing.overlapsWith(this.trip)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void updateTripSeats() {
        if (trip != null && isBookingEligible()) {
            trip.setNumberOfSeats(trip.getNumberOfSeats() - numberOfSeats);
        }
    }
}
