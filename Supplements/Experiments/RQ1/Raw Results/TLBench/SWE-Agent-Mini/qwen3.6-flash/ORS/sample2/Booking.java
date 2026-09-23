import java.util.*;

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
        
        if (trip.getDepartureDate() == null) {
            return false;
        }
        
        long diff = trip.getDepartureDate().getTime() - bookingDate.getTime();
        long diffHours = diff / (1000 * 60 * 60);
        
        if (diffHours <= 2) {
            return false;
        }
        
        int availableSeats = trip.getNumberOfSeats() - trip.getBookedSeats();
        if (numberOfSeats > availableSeats) {
            return false;
        }
        
        for (Booking existingBooking : customer.getBookings()) {
            if (existingBooking != null && existingBooking != this 
                    && existingBooking.overlapsWith(trip)) {
                return false;
            }
        }
        
        return true;
    }

    public void updateTripSeats() {
        if (trip != null && numberOfSeats > 0) {
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

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public boolean overlapsWith(Trip trip) {
        if (this.trip == null || trip == null) {
            return false;
        }
        
        Date thisDate = this.trip.getDepartureDate();
        Date tripDate = trip.getDepartureDate();
        
        if (!isSameDay(thisDate, tripDate)) {
            return false;
        }
        
        String time1 = this.trip.getDepartureTime();
        String time2 = this.trip.getArrivalTime();
        String time3 = trip.getDepartureTime();
        String time4 = trip.getArrivalTime();
        
        if (time1 == null || time2 == null || time3 == null || time4 == null) {
            return false;
        }
        
        try {
            String[] parts1 = time1.split(":");
            String[] parts2 = time2.split(":");
            String[] parts3 = time3.split(":");
            String[] parts4 = time4.split(":");
            
            int tripDep = Integer.parseInt(parts3[0]) * 60 + Integer.parseInt(parts3[1]);
            int tripArr = Integer.parseInt(parts4[0]) * 60 + Integer.parseInt(parts4[1]);
            int thisDep = Integer.parseInt(parts1[0]) * 60 + Integer.parseInt(parts1[1]);
            int thisArr = Integer.parseInt(parts2[0]) * 60 + Integer.parseInt(parts2[1]);
            
            return Math.max(tripDep, thisDep) < Math.min(tripArr, thisArr);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    public boolean isInMonth(String month) {
        if (month == null || month.isEmpty() || bookingDate == null) {
            return false;
        }
        
        try {
            String[] parts = month.split("-");
            if (parts.length != 2) {
                return false;
            }
            int year = Integer.parseInt(parts[0]);
            int targetMonth = Integer.parseInt(parts[1]);
            
            Calendar cal = Calendar.getInstance();
            cal.setTime(bookingDate);
            return cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) == (targetMonth - 1);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
