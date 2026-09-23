import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Booking {
    private int numberOfSeats;
    private Customer customer;
    private Trip trip;
    private Date bookingDate;

    public Booking() {
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

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public boolean isBookingEligible() {
        // Invalid inputs
        if (customer == null || trip == null || bookingDate == null) {
            return false;
        }
        
        // Trip must exist (implicitly checked by not null)
        // Check enough available seats
        int availableSeats = trip.getNumberOfSeats() - trip.getBookedSeats();
        if (numberOfSeats <= 0 || numberOfSeats > availableSeats) {
            return false;
        }
        
        // Check no overlapping booking on same day
        Date tripDate = trip.getDepartureDate();
        if (tripDate == null) {
            return false;
        }
        
        // Get all bookings of this customer
        java.util.List<Booking> customerBookings = customer.getBookings();
        if (customerBookings != null) {
            for (Booking existingBooking : customerBookings) {
                if (existingBooking == null || existingBooking.getTrip() == null) {
                    continue;
                }
                // Only check bookings on the same day
                if (isSameDay(existingBooking.getTrip().getDepartureDate(), tripDate)) {
                    if (overlapsWith(existingBooking.getTrip())) {
                        return false;
                    }
                }
            }
        }
        
        // Booking must be made more than 2 hours before departure
        // Check bookingDate + 2 hours < departure (date + time)
        Calendar bookingCal = Calendar.getInstance();
        bookingCal.setTime(bookingDate);
        
        Calendar depCal = Calendar.getInstance();
        depCal.setTime(trip.getDepartureDate());
        // Set departure time
        String depTime = trip.getDepartureTime();
        if (depTime == null || !depTime.contains(":")) {
            return false;
        }
        String[] parts = depTime.split(":");
        depCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
        depCal.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
        depCal.set(Calendar.SECOND, 0);
        depCal.set(Calendar.MILLISECOND, 0);
        
        // Add 2 hours to booking time
        bookingCal.add(Calendar.HOUR_OF_DAY, 2);
        
        // Booking must be strictly more than 2 hours before (booking + 2h < departure)
        if (!bookingCal.before(depCal)) {
            return false;
        }
        
        return true;
    }

    private boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public boolean overlapsWith(Trip otherTrip) {
        if (trip == null || otherTrip == null) {
            return false;
        }
        if (!isSameDay(trip.getDepartureDate(), otherTrip.getDepartureDate())) {
            return false;
        }
        // Check if time ranges overlap (adjacent boundaries allowed)
        String thisDep = trip.getDepartureTime();
        String thisArr = trip.getArrivalTime();
        String otherDep = otherTrip.getDepartureTime();
        String otherArr = otherTrip.getArrivalTime();
        
        if (thisDep == null || thisArr == null || otherDep == null || otherArr == null) {
            return false;
        }
        
        int thisStart = Trip.timeToMinutes(thisDep);
        int thisEnd = Trip.timeToMinutes(thisArr);
        int otherStart = Trip.timeToMinutes(otherDep);
        int otherEnd = Trip.timeToMinutes(otherArr);
        
        // Overlap if NOT (otherEnd <= thisStart OR otherStart >= thisEnd)
        return !(otherEnd <= thisStart || otherStart >= thisEnd);
    }

    public void updateTripSeats() {
        if (trip != null) {
            // The trip seat inventory is reduced when booking is accepted
            // This is handled by the booking logic
            // The numberOfSeats in trip represents total seats, booked seats tracked via bookings list
        }
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String bookingMonth = sdf.format(bookingDate);
        return bookingMonth.equals(month);
    }
}
