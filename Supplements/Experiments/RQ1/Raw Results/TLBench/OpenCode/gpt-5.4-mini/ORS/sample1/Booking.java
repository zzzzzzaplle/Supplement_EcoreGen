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
        if (numberOfSeats <= 0) {
            return false;
        }
        if (trip.getNumberOfSeats() < numberOfSeats) {
            return false;
        }
        java.util.Date departure = DateUtil.combineDateAndTime(trip.getDepartureDate(), trip.getDepartureTime());
        if (departure == null) {
            return false;
        }
        long diff = departure.getTime() - bookingDate.getTime();
        if (diff <= 2L * 60L * 60L * 1000L) {
            return false;
        }
        if (customer.getBookings() != null) {
            for (Booking existing : customer.getBookings()) {
                if (existing != null && existing != this && overlapsWith(existing.getTrip())) {
                    return false;
                }
            }
        }
        return true;
    }

    public void updateTripSeats() {
        if (trip == null) {
            return;
        }
        trip.setNumberOfSeats(trip.getNumberOfSeats() - numberOfSeats);
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

    public boolean overlapsWith(Trip otherTrip) {
        if (trip == null || otherTrip == null) {
            return false;
        }
        if (trip.getDepartureDate() == null || otherTrip.getDepartureDate() == null) {
            return false;
        }
        if (!trip.getDepartureDate().equals(otherTrip.getDepartureDate())) {
            return false;
        }
        java.util.Date thisStart = DateUtil.combineDateAndTime(trip.getDepartureDate(), trip.getDepartureTime());
        java.util.Date thisEnd = DateUtil.combineDateAndTime(trip.getDepartureDate(), trip.getArrivalTime());
        java.util.Date otherStart = DateUtil.combineDateAndTime(otherTrip.getDepartureDate(), otherTrip.getDepartureTime());
        java.util.Date otherEnd = DateUtil.combineDateAndTime(otherTrip.getDepartureDate(), otherTrip.getArrivalTime());
        if (thisStart == null || thisEnd == null || otherStart == null || otherEnd == null) {
            return false;
        }
        return thisStart.before(otherEnd) && thisEnd.after(otherStart);
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null) {
            return false;
        }
        String bookingMonth = DateUtil.formatMonth(bookingDate);
        return month.equals(bookingMonth);
    }
}
