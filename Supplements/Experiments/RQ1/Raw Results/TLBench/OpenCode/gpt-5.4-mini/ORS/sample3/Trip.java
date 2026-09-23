import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
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
        this.bookings = new ArrayList<>();
        this.stops = new ArrayList<>();
    }

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null || bookingTime == null || departureTime == null) {
            return price;
        }
        MembershipPackage membershipPackage = customer.getMembershipPackage();
        if (membershipPackage == null || !membershipPackage.hasAward(Award.DISCOUNTS)) {
            return price;
        }
        if (!DateTimeUtil.isAtLeastHoursBefore(bookingTime, departureTime, 24)) {
            return price;
        }
        return Math.round(price * 0.8 * 10.0) / 10.0;
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
        if (this.bookings == null) {
            this.bookings = new ArrayList<>();
        }
        if (booking != null) {
            this.bookings.add(booking);
        }
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        return 0;
    }

    public Set<String> getStopStations() {
        Set<String> stations = new HashSet<>();
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
        if (!isValidTime(newDepartureTime) || !isValidTime(newArrivalTime) || !isValidTime(departureTime) || !isValidTime(arrivalTime)) {
            return false;
        }
        return compareTimes(newDepartureTime, arrivalTime) < 0 && compareTimes(newArrivalTime, departureTime) > 0;
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

    public List<Stop> getStops() {
        return stops;
    }

    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }

    public void addStop(Stop stop) {
        if (this.stops == null) {
            this.stops = new ArrayList<>();
        }
        if (stop != null) {
            this.stops.add(stop);
        }
    }

    public static boolean isValidTime(String time) {
        return time != null && time.matches("^\\d{2}:\\d{2}$");
    }

    public static int compareTimes(String time1, String time2) {
        if (!isValidTime(time1) || !isValidTime(time2)) {
            return 0;
        }
        return time1.compareTo(time2);
    }
}
