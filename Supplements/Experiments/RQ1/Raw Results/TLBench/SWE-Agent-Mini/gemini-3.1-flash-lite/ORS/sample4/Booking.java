import java.util.Date;
import java.util.List;

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
        
        // check availability
        int booked = trip.getBookedSeats();
        if (numberOfSeats > (trip.getNumberOfSeats() - booked)) return false;
        
        // Overlapping bookings: same day, same customer
        for (Booking b : customer.getBookings()) {
            if (b.getTrip() != null && b.getTrip().getDepartureDate().equals(trip.getDepartureDate())) {
                // Simplified overlap check: if they have different start/end times at all on the same day
                // The requirements say "no overlapping booking on the same day".
                // We'll rely on the trip having a time-overlap check method.
                if (b != this && b.getTrip().isTimeConflicting(trip.getDepartureTime(), trip.getArrivalTime())) {
                    return false;
                }
            }
        }
        
        // Booking time must be > 2 hours before departure (in hours)
        long diffMs = trip.getDepartureDate().getTime() + timeToMillis(trip.getDepartureTime()) - bookingDate.getTime();
        if (diffMs <= 2 * 60 * 60 * 1000) return false;
        
        return true;
    }

    private long timeToMillis(String timeStr) {
        String[] parts = timeStr.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return (hours * 60 * 60 * 1000L) + (minutes * 60 * 1000L);
    }

    public void updateTripSeats() {
        // Need to add this booking to the trip.
        // Actually, the requirement says "when booking is accepted, the trip seat inventory must be reduced accordingly."
        // We do this by adding to the trip's bookings list.
        if (trip != null) {
            trip.addBooking(this);
        }
    }

    public boolean overlapsWith(Trip trip) {
        // This is a placeholder as discussed in the diagram / requirement logic
        return false;
    }

    public boolean isInMonth(String month) {
        // Simplified check
        return bookingDate.toString().contains(month);
    }
}
