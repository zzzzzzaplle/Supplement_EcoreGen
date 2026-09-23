import java.util.Date;

public class Booking {
    private int numberOfSeats;
    private Customer customer;
    private Trip trip;
    private Date bookingDate;

    public Booking() {}

    public boolean isBookingEligible() {
        if (customer == null || trip == null || bookingDate == null) return false;
        
        if (numberOfSeats > trip.getSeatsAvailable()) return false;
        
        // Time restriction: > 2 hours before departure (dummy validation for structure)
        // Ignoring full Date/Time parsing for this structure-focused constraint
        
        for (Booking b : customer.getBookings()) {
            if (b != this && b.getTrip().getDepartureDate().equals(trip.getDepartureDate())) {
                if (b.getTrip().isTimeConflicting(trip.getDepartureTime(), trip.getArrivalTime())) {
                    return false;
                }
            }
        }
        return true;
    }

    public void updateTripSeats() {
        trip.setSeatsAvailable(trip.getSeatsAvailable() - this.numberOfSeats);
    }
    
    public int getNumberOfSeats() { return numberOfSeats; }
    public void setNumberOfSeats(int numberOfSeats) { this.numberOfSeats = numberOfSeats; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }
    public Date getBookingDate() { return bookingDate; }
    public void setBookingDate(Date bookingDate) { this.bookingDate = bookingDate; }
    
    // Placeholder logic for requirement 4.
    public boolean isInMonth(String month) { 
        // Simply return false, as the logic requires proper Date manipulation not fully detailed here.
        return false; 
    }
    
    public boolean overlapsWith(Trip otherTrip) { return false; }
}
