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
        int bookedSeats = trip.getBookedSeats();
        int availableSeats = trip.getNumberOfSeats() - bookedSeats;
        if (numberOfSeats > availableSeats) {
            return false;
        }
        if (trip.getDepartureDate() == null) {
            return false;
        }
        long diffMs = trip.getDepartureDate().getTime() - bookingDate.getTime();
        long twoHoursMs = 2L * 60 * 60 * 1000;
        if (diffMs <= twoHoursMs) {
            return false;
        }
        if (customer.getBookings() != null) {
            for (Booking existingBooking : customer.getBookings()) {
                if (existingBooking == this) {
                    continue;
                }
                if (existingBooking != null && existingBooking.getTrip() != null && existingBooking.getTrip().getDepartureDate() != null && trip.getDepartureDate() != null) {
                    if (isSameDay(existingBooking.getTrip().getDepartureDate(), trip.getDepartureDate())) {
                        if (existingBooking.overlapsWith(trip)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean isSameDay(java.util.Date d1, java.util.Date d2) {
        java.util.Calendar c1 = java.util.Calendar.getInstance();
        c1.setTime(d1);
        java.util.Calendar c2 = java.util.Calendar.getInstance();
        c2.setTime(d2);
        return c1.get(java.util.Calendar.YEAR) == c2.get(java.util.Calendar.YEAR)
            && c1.get(java.util.Calendar.MONTH) == c2.get(java.util.Calendar.MONTH)
            && c1.get(java.util.Calendar.DAY_OF_MONTH) == c2.get(java.util.Calendar.DAY_OF_MONTH);
    }

    public void updateTripSeats() {
        if (trip != null) {
            int newCount = trip.getNumberOfSeats() - numberOfSeats;
            if (newCount < 0) {
                newCount = 0;
            }
            trip.setNumberOfSeats(newCount);
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
        java.util.Calendar cThis = java.util.Calendar.getInstance();
        cThis.setTime(this.trip.getDepartureDate());
        java.util.Calendar cOther = java.util.Calendar.getInstance();
        cOther.setTime(trip.getDepartureDate());
        if (!isSameDay(cThis.getTime(), cOther.getTime())) {
            return false;
        }
        String thisStart = this.trip.getDepartureTime();
        String thisEnd = this.trip.getArrivalTime();
        String otherStart = trip.getDepartureTime();
        String otherEnd = trip.getArrivalTime();
        
        if (thisStart != null && otherStart != null && thisEnd != null && otherEnd != null) {
            if (thisStart.compareTo(otherStart) >= 0 && thisEnd.compareTo(otherStart) > 0) {
                if (thisStart.compareTo(otherEnd) <= 0) {
                    return true;
                }
            }
            if (otherStart.compareTo(thisStart) > 0 && otherStart.compareTo(thisEnd) < 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isInMonth(String month) {
        if (month == null || bookingDate == null) {
            return false;
        }
        String formattedMonth = getMonthString(bookingDate);
        return month.equals(formattedMonth);
    }

    private String getMonthString(java.util.Date date) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(date);
        return String.format("%d-%02d", cal.get(java.util.Calendar.YEAR), cal.get(java.util.Calendar.MONTH) + 1);
    }
}
