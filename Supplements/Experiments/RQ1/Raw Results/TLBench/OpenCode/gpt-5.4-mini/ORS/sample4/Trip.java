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
        if (customer == null || bookingTime == null || membershipDiscountMissing(customer)) {
            return price;
        }
        if (departureDate == null || departureTime == null) {
            return price;
        }
        java.util.Date bookingDateTime = DateUtils.parseDateTime(bookingTime);
        java.util.Date tripDateTime = DateUtils.parseDateTime(DateUtils.formatDate(departureDate) + " " + departureTime);
        if (bookingDateTime == null || tripDateTime == null) {
            return price;
        }
        long diff = tripDateTime.getTime() - bookingDateTime.getTime();
        if (diff < 24L * 60L * 60L * 1000L) {
            return price;
        }
        double discounted = price * 0.8d;
        return Math.round(discounted * 10.0d) / 10.0d;
    }

    private boolean membershipDiscountMissing(Customer customer) {
        MembershipPackage membershipPackage = customer.getMembershipPackage();
        return membershipPackage == null || !membershipPackage.hasAward(Award.DISCOUNTS);
    }

    public int getBookedSeats() {
        if (bookings == null) {
            return 0;
        }
        int total = 0;
        for (Booking booking : bookings) {
            if (booking != null) {
                total += booking.getNumberOfSeats();
            }
        }
        return total;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public void addBooking(Booking booking) {
        if (booking != null) {
            if (bookings == null) {
                bookings = new ArrayList<Booking>();
            }
            bookings.add(booking);
        }
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || currentMonth == null) {
            return 0;
        }
        MembershipPackage membershipPackage = customer.getMembershipPackage();
        if (membershipPackage == null || !membershipPackage.hasAward(Award.POINTS)) {
            return 0;
        }
        if (bookings == null) {
            return 0;
        }
        int total = 0;
        for (Booking booking : bookings) {
            if (booking != null && booking.isInMonth(currentMonth) && customer.equals(booking.getCustomer())) {
                total += booking.getNumberOfSeats() * 5;
            }
        }
        return total;
    }

    public Set<String> getStopStations() {
        Set<String> stations = new HashSet<String>();
        if (stops != null) {
            for (Stop stop : stops) {
                if (stop != null && stop.getStopStation() != null) {
                    stations.add(stop.getStopStation());
                }
            }
        }
        return stations;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (departureTime == null || arrivalTime == null || newDepartureTime == null || newArrivalTime == null) {
            return false;
        }
        java.util.Date existingStart = DateUtils.parseTimeOnly(departureTime);
        java.util.Date existingEnd = DateUtils.parseTimeOnly(arrivalTime);
        java.util.Date newStart = DateUtils.parseTimeOnly(newDepartureTime);
        java.util.Date newEnd = DateUtils.parseTimeOnly(newArrivalTime);
        if (existingStart == null || existingEnd == null || newStart == null || newEnd == null) {
            return false;
        }
        return newStart.before(existingEnd) && newEnd.after(existingStart);
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
        if (stop != null) {
            if (stops == null) {
                stops = new ArrayList<Stop>();
            }
            stops.add(stop);
        }
    }
}

class DateUtils {
    public static java.util.Date parseDateTime(String value) {
        try {
            return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").parse(value);
        } catch (Exception ex) {
            return null;
        }
    }

    public static java.util.Date parseTimeOnly(String value) {
        try {
            return new java.text.SimpleDateFormat("HH:mm").parse(value);
        } catch (Exception ex) {
            return null;
        }
    }

    public static String formatDate(java.util.Date date) {
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}
