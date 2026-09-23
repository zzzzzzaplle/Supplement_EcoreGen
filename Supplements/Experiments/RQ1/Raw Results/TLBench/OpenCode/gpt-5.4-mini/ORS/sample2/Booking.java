class Booking {
    private int numberOfSeats;
    private Customer customer;
    private Trip trip;
    private java.util.Date bookingDate;

    public Booking() {
    }

    public boolean isBookingEligible() {
        if (customer == null || trip == null || bookingDate == null || trip.getDepartureDate() == null || trip.getDepartureTime() == null) {
            return false;
        }
        if (numberOfSeats <= 0 || numberOfSeats > trip.getNumberOfSeats()) {
            return false;
        }
        if (trip.getBookedSeats() + numberOfSeats > trip.getNumberOfSeats()) {
            return false;
        }
        if (customer.getBookings() != null) {
            for (Booking booking : customer.getBookings()) {
                if (booking != null && booking != this && overlapsWith(booking.getTrip())) {
                    return false;
                }
            }
        }
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
        format.setLenient(false);
        try {
            java.util.Date departureMoment = format.parse(format.format(trip.getDepartureDate()) + " " + trip.getDepartureTime());
            long diff = departureMoment.getTime() - bookingDate.getTime();
            return diff > 2L * 60L * 60L * 1000L;
        } catch (Exception e) {
            return false;
        }
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
    public java.util.Date getBookingDate() { return bookingDate; }
    public void setBookingDate(java.util.Date bookingDate) { this.bookingDate = bookingDate; }

    public boolean overlapsWith(Trip otherTrip) {
        if (trip == null || otherTrip == null || trip.getDepartureDate() == null || otherTrip.getDepartureDate() == null) {
            return false;
        }
        boolean sameDay = trip.getDepartureDate().equals(otherTrip.getDepartureDate());
        if (!sameDay) {
            return false;
        }
        return trip.isTimeConflicting(otherTrip.getDepartureTime(), otherTrip.getArrivalTime());
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null) {
            return false;
        }
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM");
        return month.equals(format.format(bookingDate));
    }
}
