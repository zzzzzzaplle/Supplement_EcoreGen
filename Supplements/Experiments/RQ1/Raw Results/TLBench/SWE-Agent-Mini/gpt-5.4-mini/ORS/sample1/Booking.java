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
        return trip.getBookedSeats() + numberOfSeats <= trip.getNumberOfSeats();
    }

    public void updateTripSeats() {
        if (trip != null) {
            trip.setNumberOfSeats(trip.getNumberOfSeats() - numberOfSeats);
        }
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
        if (this.trip == null || trip == null || this.trip.getDepartureDate() == null || trip.getDepartureDate() == null) {
            return false;
        }
        return this.trip.getDepartureDate().equals(trip.getDepartureDate()) && this.trip.isTimeConflicting(trip.getDepartureTime(), trip.getArrivalTime());
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null || month.length() != 7) {
            return false;
        }
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(bookingDate);
        String m = String.format("%04d-%02d", cal.get(java.util.Calendar.YEAR), cal.get(java.util.Calendar.MONTH) + 1);
        return month.equals(m);
    }
}
