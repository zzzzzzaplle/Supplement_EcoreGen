import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Trip {
    private String departureStation;
    private String arrivalStation;
    private int numberOfSeats;
    private Date departureDate;
    private String departureTime;
    private String arrivalTime;
    private double price;
    private List<Booking> bookings;
    private List<Stop> stops;

    public Trip() {
        this.bookings = new ArrayList<Booking>();
        this.stops = new ArrayList<Stop>();
    }

    public String getDepartureStation() {
        return departureStation;
    }

    public void setDepartureStation(String departureStation) {
        this.departureStation = departureStation;
    }

    public String getArrivalStation() {
        return arrivalStation;
    }

    public void setArrivalStation(String arrivalStation) {
        this.arrivalStation = arrivalStation;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void addBooking(Booking booking) {
        this.bookings.add(booking);
    }

    public List<Stop> getStops() {
        return stops;
    }

    public void addStop(Stop stop) {
        this.stops.add(stop);
    }

    public int getBookedSeats() {
        int booked = 0;
        for (Booking b : bookings) {
            booked += b.getNumberOfSeats();
        }
        return booked;
    }

    public Set<String> getStopStations() {
        Set<String> stations = new HashSet<String>();
        if (stops != null) {
            for (Stop s : stops) {
                stations.add(s.getStopStation());
            }
        }
        return stations;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (departureTime == null || arrivalTime == null || newDepartureTime == null || newArrivalTime == null) {
            return false;
        }
        // Compare times as strings in format HH:MM (24h)
        // Conflict if new trip starts before this trip ends AND new trip ends after this trip starts
        // Adjacent boundaries (one ends exactly when another starts) are allowed, so use strict inequalities
        boolean newStartsBeforeThisEnds = newDepartureTime.compareTo(arrivalTime) < 0;
        boolean newEndsAfterThisStarts = newArrivalTime.compareTo(departureTime) > 0;
        return newStartsBeforeThisEnds && newEndsAfterThisStarts;
    }

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null || bookingTime == null || departureTime == null || departureDate == null) {
            return price;
        }
        MembershipPackage mp = customer.getMembershipPackage();
        if (mp == null || !mp.hasAward(Award.DISCOUNTS)) {
            return price;
        }
        // Check if booking is made at least 24 hours before departure
        // bookingTime format: "yyyy-MM-dd HH:mm"
        // departureDate + departureTime gives departure datetime
        String departureDateTime = departureDate.toString() + " " + departureTime;
        // For simplicity, compare the booking time with departure time
        // Booking must be at least 24 hours before departure
        // Compare as strings: "yyyy-MM-dd HH:mm" format
        // We need to check if bookingTime + 24h <= departureDateTime
        // Simple approach: parse and compare
        try {
            String[] bookingParts = bookingTime.split(" ");
            String[] bookingDateParts = bookingParts[0].split("-");
            String[] bookingTimeParts = bookingParts[1].split(":");
            int bookingYear = Integer.parseInt(bookingDateParts[0]);
            int bookingMonth = Integer.parseInt(bookingDateParts[1]);
            int bookingDay = Integer.parseInt(bookingDateParts[2]);
            int bookingHour = Integer.parseInt(bookingTimeParts[0]);
            int bookingMinute = Integer.parseInt(bookingTimeParts[1]);

            // Get departure date and time
            // departureDate is java.util.Date, we need to extract its components
            // Use java.util.Calendar
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(departureDate);
            int depYear = cal.get(java.util.Calendar.YEAR);
            int depMonth = cal.get(java.util.Calendar.MONTH) + 1;
            int depDay = cal.get(java.util.Calendar.DAY_OF_MONTH);

            String[] depTimeParts = departureTime.split(":");
            int depHour = Integer.parseInt(depTimeParts[0]);
            int depMinute = Integer.parseInt(depTimeParts[1]);

            // Calculate booking datetime in minutes since epoch (relative)
            long bookingMinutes = bookingYear * 525600L + bookingMonth * 43200L + bookingDay * 1440L + bookingHour * 60L + bookingMinute;
            long depMinutes = depYear * 525600L + depMonth * 43200L + depDay * 1440L + depHour * 60L + depMinute;

            // At least 24 hours = 1440 minutes before departure
            if (depMinutes - bookingMinutes >= 1440) {
                double discountedPrice = price * 0.8;
                // Keep one decimal place
                return Math.round(discountedPrice * 10.0) / 10.0;
            }
        } catch (Exception e) {
            return price;
        }
        return price;
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || currentMonth == null) {
            return 0;
        }
        MembershipPackage mp = customer.getMembershipPackage();
        if (mp == null || !mp.hasAward(Award.POINTS)) {
            return 0;
        }
        int totalPoints = 0;
        List<Booking> customerBookings = customer.getBookings();
        if (customerBookings == null) {
            return 0;
        }
        for (Booking b : customerBookings) {
            if (b.getTrip() == this && b.isInMonth(currentMonth)) {
                totalPoints += b.getNumberOfSeats() * 5;
            }
        }
        return totalPoints;
    }
}
