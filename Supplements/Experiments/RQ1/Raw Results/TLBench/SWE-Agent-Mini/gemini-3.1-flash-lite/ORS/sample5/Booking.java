import java.util.Date;

public class Booking {
    private int numberOfSeats;
    private Customer customer;
    private Trip trip;
    private Date bookingDate;

    public Booking() {}

    public boolean isBookingEligible() {
        return false; // Logic to be implemented
    }

    public void updateTripSeats() {
    }

    public int getNumberOfSeats() { return numberOfSeats; }
    public void setNumberOfSeats(int numberOfSeats) { this.numberOfSeats = numberOfSeats; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }
    public Date getBookingDate() { return bookingDate; }
    public void setBookingDate(Date bookingDate) { this.bookingDate = bookingDate; }

    public boolean overlapsWith(Trip trip) {
        return false;
    }

    public boolean isInMonth(String month) {
        return false;
    }
}
