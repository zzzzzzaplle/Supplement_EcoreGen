import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.math.BigDecimal;
import java.math.RoundingMode;

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
        if (customer == null) {
            return price;
        }
        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null) {
            return price;
        }
        if (!pkg.hasAward(Award.DISCOUNTS)) {
            return price;
        }
        if (bookingTime == null || departureTime == null) {
            return price;
        }
        int booking = parseTime(bookingTime);
        int departure = parseTime(departureTime);
        if (booking < 0 || departure < 0) {
            return price;
        }
        int diff = departure - booking;
        if (diff < 0) {
            diff += 24 * 60;
        }
        if (diff < 24 * 60) {
            return price;
        }
        BigDecimal bd = new BigDecimal(price * 0.8);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public int getBookedSeats() {
        int total = 0;
        for (Booking b : bookings) {
            if (b != null) {
                total += b.getNumberOfSeats();
            }
        }
        return total;
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null) {
            return 0;
        }
        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null) {
            return 0;
        }
        if (!pkg.hasAward(Award.POINTS)) {
            return 0;
        }
        if (currentMonth == null) {
            return 0;
        }
        int total = 0;
        for (Booking b : bookings) {
            if (b == null) {
                continue;
            }
            if (b.getCustomer() == null || b.getCustomer() != customer) {
                continue;
            }
            if (b.isInMonth(currentMonth)) {
                total += b.getNumberOfSeats() * 5;
            }
        }
        return total;
    }

    public Set<String> getStopStations() {
        Set<String> set = new HashSet<String>();
        for (Stop s : stops) {
            if (s != null && s.getStopStation() != null) {
                set.add(s.getStopStation());
            }
        }
        return set;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (newDepartureTime == null || newArrivalTime == null) {
            return false;
        }
        if (departureTime == null || arrivalTime == null) {
            return false;
        }
        int nDep = parseTime(newDepartureTime);
        int nArr = parseTime(newArrivalTime);
        int eDep = parseTime(departureTime);
        int eArr = parseTime(arrivalTime);
        if (nDep < 0 || nArr < 0 || eDep < 0 || eArr < 0) {
            return false;
        }
        if (nArr <= eDep) {
            return false;
        }
        if (nDep >= eArr) {
            return false;
        }
        return true;
    }

    private static int parseTime(String t) {
        if (t == null) {
            return -1;
        }
        try {
            String[] parts = t.split(":");
            if (parts.length < 2) {
                return -1;
            }
            int h = Integer.parseInt(parts[0].trim());
            int m = Integer.parseInt(parts[1].trim());
            return h * 60 + m;
        } catch (Exception e) {
            return -1;
        }
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
        if (booking != null) {
            bookings.add(booking);
        }
    }

    public List<Stop> getStops() {
        return stops;
    }

    public void addStop(Stop stop) {
        if (stop != null) {
            stops.add(stop);
        }
    }
}
