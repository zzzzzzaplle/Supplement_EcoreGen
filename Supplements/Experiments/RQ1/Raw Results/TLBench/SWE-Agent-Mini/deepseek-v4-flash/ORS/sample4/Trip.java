import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.text.SimpleDateFormat;
import java.text.ParseException;

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
        int bookedSeats = 0;
        for (Booking b : bookings) {
            bookedSeats += b.getNumberOfSeats();
        }
        return bookedSeats;
    }

    public Set<String> getStopStations() {
        Set<String> stationSet = new HashSet<String>();
        for (Stop s : stops) {
            if (s.getStopStation() != null) {
                stationSet.add(s.getStopStation());
            }
        }
        return stationSet;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (departureTime == null || arrivalTime == null || newDepartureTime == null || newArrivalTime == null) {
            return false;
        }
        boolean endsBeforeStarts = newArrivalTime.compareTo(departureTime) <= 0;
        boolean startsAfterEnds = newDepartureTime.compareTo(arrivalTime) >= 0;
        return !(endsBeforeStarts || startsAfterEnds);
    }

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null || bookingTime == null) {
            return this.price;
        }
        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null || !pkg.hasAward(Award.DISCOUNTS)) {
            return this.price;
        }
        if (departureTime == null || departureDate == null) {
            return this.price;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String depDateTimeStr = new SimpleDateFormat("yyyy-MM-dd").format(departureDate) + " " + departureTime;
            Date depDateTime = sdf.parse(depDateTimeStr);
            
            // bookingTime is passed as "yyyy-MM-dd HH:mm" format
            Date bookDateTime = sdf.parse(bookingTime);
            
            long diffMillis = depDateTime.getTime() - bookDateTime.getTime();
            long diffHours = diffMillis / (1000 * 60 * 60);
            if (diffHours >= 24) {
                double discounted = this.price * 0.8;
                return Math.round(discounted * 10.0) / 10.0;
            }
        } catch (Exception e) {
            return this.price;
        }
        return this.price;
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
        if (bookings != null) {
            for (Booking b : bookings) {
                if (b.getCustomer() == customer && b.isInMonth(currentMonth)) {
                    totalPoints += b.getNumberOfSeats() * 5;
                }
            }
        }
        return totalPoints;
    }
}
