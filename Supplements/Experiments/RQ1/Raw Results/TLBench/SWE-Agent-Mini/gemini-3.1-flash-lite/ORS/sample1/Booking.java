import java.util.Date;

public class Booking {
    private int numberOfSeats;
    private Customer customer;
    private Trip trip;
    private Date bookingDate;

    public Booking() {}

    public int getNumberOfSeats() { return numberOfSeats; }
    public void setNumberOfSeats(int numberOfSeats) { this.numberOfSeats = numberOfSeats; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }
    public Date getBookingDate() { return bookingDate; }
    public void setBookingDate(Date bookingDate) { this.bookingDate = bookingDate; }

    public boolean isBookingEligible() {
        if (customer == null || trip == null || bookingDate == null) return false;
        if (numberOfSeats <= 0 || numberOfSeats > (trip.getNumberOfSeats() - trip.getBookedSeats())) return false;
        
        for (Booking b : customer.getBookings()) {
            if (b != this && b.getTrip() != null && 
                b.getTrip().getDepartureDate().equals(trip.getDepartureDate()) &&
                b.getTrip().isTimeConflicting(trip.getDepartureTime(), trip.getArrivalTime())) {
                return false;
            }
        }
        
        long diff = trip.getDepartureDate().getTime() - bookingDate.getTime();
        long hoursBefore = diff / (1000 * 60 * 60);
        return hoursBefore > 2;
    }

    public void updateTripSeats() {
        // Implementation logic handled via Trip.addBooking(this)
    }

    public boolean overlapsWith(Trip t) {
        if (trip == null) return false;
        return trip.getDepartureDate().equals(t.getDepartureDate()) &&
               trip.isTimeConflicting(t.getDepartureTime(), t.getArrivalTime());
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null) return false;
        // Basic month comparison: Assuming format YYYY-MM
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM");
        return sdf.format(bookingDate).equals(month);
    }
}
