public class Booking {
    private int numberOfSeats;
    private Customer customer;
    private Trip trip;
    private java.util.Date bookingDate;

    public Booking() {
    }

    public boolean isBookingEligible() {
        if (customer == null || trip == null || bookingDate == null) {
            return false;
        }
        if (numberOfSeats <= 0 || trip.getNumberOfSeats() < numberOfSeats) {
            return false;
        }
        if (trip.getDepartureDate() == null || trip.getDepartureTime() == null) {
            return false;
        }
        java.util.Date tripDateTime = DateUtils.parseDateTime(DateUtils.formatDate(trip.getDepartureDate()) + " " + trip.getDepartureTime());
        if (tripDateTime == null) {
            return false;
        }
        long diff = tripDateTime.getTime() - bookingDate.getTime();
        if (diff <= 2L * 60L * 60L * 1000L) {
            return false;
        }
        if (customer.getBookings() != null) {
            for (Booking existing : customer.getBookings()) {
                if (existing != null && existing != this && existing.getBookingDate() != null && existing.getTrip() != null) {
                    java.util.Date existingTripDateTime = DateUtils.parseDateTime(DateUtils.formatDate(existing.getTrip().getDepartureDate()) + " " + existing.getTrip().getDepartureTime());
                    if (existingTripDateTime != null && bookingDate.equals(existing.getBookingDate()) && tripDateTime.equals(existingTripDateTime)) {
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

    public java.util.Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(java.util.Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public boolean overlapsWith(Trip trip) {
        if (this.trip == null || trip == null) {
            return false;
        }
        if (this.trip.getDepartureDate() == null || trip.getDepartureDate() == null) {
            return false;
        }
        return DateUtils.formatDate(this.trip.getDepartureDate()).equals(DateUtils.formatDate(trip.getDepartureDate())) && this.trip.isTimeConflicting(trip.getDepartureTime(), trip.getArrivalTime());
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null) {
            return false;
        }
        String current = new java.text.SimpleDateFormat("yyyy-MM").format(bookingDate);
        return current.equals(month);
    }
}
