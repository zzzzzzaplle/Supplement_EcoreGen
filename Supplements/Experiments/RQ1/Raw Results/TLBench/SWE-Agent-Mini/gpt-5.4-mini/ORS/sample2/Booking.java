import java.util.Date;

public class Booking {
    private int numberOfSeats;
    private Customer customer;
    private Trip trip;
    private Date bookingDate;

    public Booking() {
    }

    public boolean isBookingEligible() {
        if (customer == null || trip == null || bookingDate == null || numberOfSeats <= 0) return false;
        if (trip.getNumberOfSeats() < numberOfSeats) return false;
        if (trip.getDepartureDate() == null) return false;
        return true;
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
        if (this.trip == null || trip == null || this.trip.getDepartureDate() == null || trip.getDepartureDate() == null) return false;
        return this.trip.getDepartureDate().equals(trip.getDepartureDate());
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null || month.length() != 7) return false;
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM");
        sdf.setLenient(false);
        return month.equals(sdf.format(bookingDate));
    }
}
