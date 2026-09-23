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
        if (numberOfSeats <= 0) {
            return false;
        }
        if (trip.getNumberOfSeats() < numberOfSeats) {
            return false;
        }
        if (trip.getDepartureDate() == null || trip.getDepartureTime() == null) {
            return false;
        }
        Date departureDateTime = DateUtil.combineDateAndTime(trip.getDepartureDate(), trip.getDepartureTime());
        if (departureDateTime == null) {
            return false;
        }
        long diff = departureDateTime.getTime() - bookingDate.getTime();
        if (diff <= 2L * 60L * 60L * 1000L) {
            return false;
        }
        if (customer.getBookings() != null) {
            for (Booking booking : customer.getBookings()) {
                if (booking != null && booking != this && booking.getTrip() != null && booking.getBookingDate() != null && booking.getTrip().getDepartureDate() != null) {
                    if (DateUtil.isSameDay(bookingDate, booking.getBookingDate()) && booking.overlapsWith(trip)) {
                        return false;
                    }
                }
            }
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
        if (trip == null || otherTrip == null || trip.getDepartureDate() == null || otherTrip.getDepartureDate() == null) {
            return false;
        }
        return DateUtil.isSameDay(trip.getDepartureDate(), otherTrip.getDepartureDate()) && trip.isTimeConflicting(otherTrip.getDepartureTime(), otherTrip.getArrivalTime());
    }

    public boolean isInMonth(String month) {
        return DateUtil.isInMonth(bookingDate, month);
    }
}
