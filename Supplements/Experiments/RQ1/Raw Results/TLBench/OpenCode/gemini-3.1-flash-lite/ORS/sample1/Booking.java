import java.util.Date;

public class Booking {
    private int numberOfSeats;
    private Customer customer;
    private Trip trip;
    private Date bookingDate;

    public Booking() {}

    public boolean isBookingEligible() {
        if (customer == null || trip == null || bookingDate == null) return false;
        if (trip.getNumberOfSeats() - trip.getBookedSeats() < numberOfSeats) return false;
        // Check for overlapping bookings and 2h rule here.
        return true;
    }

    public void updateTripSeats() {}

    public int getNumberOfSeats() { return numberOfSeats; }
    public void setNumberOfSeats(int numberOfSeats) { this.numberOfSeats = numberOfSeats; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }
    public Date getBookingDate() { return bookingDate; }
    public void setBookingDate(Date bookingDate) { this.bookingDate = bookingDate; }

    public boolean overlapsWith(Trip trip) { return false; }
    public boolean isInMonth(String month) { return true; }
}
