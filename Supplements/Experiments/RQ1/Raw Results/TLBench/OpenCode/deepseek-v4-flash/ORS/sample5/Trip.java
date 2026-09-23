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

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null || bookingTime == null) {
            return this.price;
        }
        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null || !pkg.hasAward(Award.DISCOUNTS)) {
            return this.price;
        }
        if (!isAtLeast24HoursBefore(bookingTime, this.departureTime)) {
            return this.price;
        }
        double discounted = this.price * 0.8;
        return Math.round(discounted * 10) / 10.0;
    }

    private boolean isAtLeast24HoursBefore(String bookingTime, String departureTime) {
        if (bookingTime == null || departureTime == null) {
            return false;
        }
        String[] bParts = bookingTime.split(":");
        String[] dParts = departureTime.split(":");
        int bHour = Integer.parseInt(bParts[0]);
        int bMin = Integer.parseInt(bParts[1]);
        int dHour = Integer.parseInt(dParts[0]);
        int dMin = Integer.parseInt(dParts[1]);
        int bookingMinutes = bHour * 60 + bMin;
        int departureMinutes = dHour * 60 + dMin;
        if (departureMinutes > bookingMinutes) {
            return false;
        }
        return (bookingMinutes - departureMinutes) >= 1440;
    }

    public int getBookedSeats() {
        int total = 0;
        for (Booking booking : bookings) {
            total += booking.getNumberOfSeats();
        }
        return total;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || currentMonth == null) {
            return 0;
        }
        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null || !pkg.hasAward(Award.POINTS)) {
            return 0;
        }
        int totalPoints = 0;
        for (Booking booking : bookings) {
            if (booking.getCustomer() != null && booking.getCustomer().equals(customer) && booking.isInMonth(currentMonth)) {
                totalPoints += booking.getNumberOfSeats() * 5;
            }
        }
        return totalPoints;
    }

    public Set<String> getStopStations() {
        Set<String> stationSet = new HashSet<String>();
        for (Stop stop : stops) {
            stationSet.add(stop.getStopStation());
        }
        return stationSet;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (departureTime == null || arrivalTime == null || newDepartureTime == null || newArrivalTime == null) {
            return false;
        }
        if (isEarlierOrEqual(newArrivalTime, newDepartureTime) || isEarlierOrEqual(arrivalTime, departureTime)) {
            return false;
        }
        if (isEarlierOrEqual(newArrivalTime, departureTime) || isEarlierOrEqual(arrivalTime, newDepartureTime)) {
            return false;
        }
        return true;
    }

    private boolean isEarlierOrEqual(String time1, String time2) {
        String[] parts1 = time1.split(":");
        String[] parts2 = time2.split(":");
        int h1 = Integer.parseInt(parts1[0]);
        int m1 = Integer.parseInt(parts1[1]);
        int h2 = Integer.parseInt(parts2[0]);
        int m2 = Integer.parseInt(parts2[1]);
        if (h1 != h2) {
            return h1 < h2;
        }
        return m1 <= m2;
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

    public void addStop(Stop stop) {
        stops.add(stop);
    }
}
