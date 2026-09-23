import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Trip {
    private String departureStation;
    private String arrivalStation;
    private int numberOfSeats;
    private java.util.Date departureDate;
    private String departureTime;
    private String arrivalTime;
    private double price;
    private List<Booking> bookings;
    private List<Stop> stops;

    public Trip() {
        this.bookings = new ArrayList<Booking>();
        this.stops = new ArrayList<Stop>();
    }

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null || bookingTime == null || departureDate == null || departureTime == null) {
            return price;
        }
        MembershipPackage membershipPackage = customer.getMembershipPackage();
        if (membershipPackage == null || !membershipPackage.hasAward(Award.DISCOUNTS)) {
            return price;
        }
        java.util.Date bookingDate = DateUtil.parseDateTime(bookingTime);
        java.util.Date departureDateTime = DateUtil.combineDateAndTime(departureDate, departureTime);
        if (bookingDate == null || departureDateTime == null) {
            return price;
        }
        long diff = departureDateTime.getTime() - bookingDate.getTime();
        if (diff < 24L * 60L * 60L * 1000L) {
            return price;
        }
        return Math.round(price * 0.8d * 10.0d) / 10.0d;
    }

    public int getBookedSeats() {
        if (bookings == null) {
            return 0;
        }
        int bookedSeats = 0;
        for (Booking booking : bookings) {
            if (booking != null) {
                bookedSeats += booking.getNumberOfSeats();
            }
        }
        return bookedSeats;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public void addBooking(Booking booking) {
        if (booking == null) {
            return;
        }
        if (bookings == null) {
            bookings = new ArrayList<Booking>();
        }
        bookings.add(booking);
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || currentMonth == null) {
            return 0;
        }
        MembershipPackage membershipPackage = customer.getMembershipPackage();
        if (membershipPackage == null || !membershipPackage.hasAward(Award.POINTS)) {
            return 0;
        }
        int points = 0;
        if (bookings == null) {
            return 0;
        }
        for (Booking booking : bookings) {
            if (booking != null && booking.getCustomer() == customer && booking.isInMonth(currentMonth)) {
                points += booking.getNumberOfSeats() * 5;
            }
        }
        return points;
    }

    public Set<String> getStopStations() {
        Set<String> stations = new HashSet<String>();
        if (stops == null) {
            return stations;
        }
        for (Stop stop : stops) {
            if (stop != null && stop.getStopStation() != null) {
                stations.add(stop.getStopStation());
            }
        }
        return stations;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (departureTime == null || arrivalTime == null || newDepartureTime == null || newArrivalTime == null || departureDate == null) {
            return false;
        }
        java.util.Date existingStart = DateUtil.combineDateAndTime(departureDate, departureTime);
        java.util.Date existingEnd = DateUtil.combineDateAndTime(departureDate, arrivalTime);
        java.util.Date newStart = DateUtil.combineDateAndTime(departureDate, newDepartureTime);
        java.util.Date newEnd = DateUtil.combineDateAndTime(departureDate, newArrivalTime);
        if (existingStart == null || existingEnd == null || newStart == null || newEnd == null) {
            return false;
        }
        return newStart.before(existingEnd) && newEnd.after(existingStart);
    }

    public boolean hasValidTimeWindow() {
        if (departureDate == null || departureTime == null || arrivalTime == null) {
            return false;
        }
        java.util.Date start = DateUtil.combineDateAndTime(departureDate, departureTime);
        java.util.Date end = DateUtil.combineDateAndTime(departureDate, arrivalTime);
        if (start == null || end == null) {
            return false;
        }
        return start.before(end);
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

    public java.util.Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(java.util.Date departureDate) {
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

    public List<Stop> getStops() {
        return stops;
    }

    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }

    public void addStop(Stop stop) {
        if (stop == null) {
            return;
        }
        if (stops == null) {
            stops = new ArrayList<Stop>();
        }
        stops.add(stop);
    }
}
