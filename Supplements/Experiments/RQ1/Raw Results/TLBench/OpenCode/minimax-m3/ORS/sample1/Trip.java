import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.text.SimpleDateFormat;

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
        if (this.bookings == null) {
            this.bookings = new ArrayList<Booking>();
        }
        this.bookings.add(booking);
    }

    public List<Stop> getStops() {
        return stops;
    }

    public void addStop(Stop stop) {
        if (this.stops == null) {
            this.stops = new ArrayList<Stop>();
        }
        this.stops.add(stop);
    }

    public int getBookedSeats() {
        int total = 0;
        if (bookings == null) {
            return 0;
        }
        for (Booking b : bookings) {
            if (b != null) {
                total += b.getNumberOfSeats();
            }
        }
        return total;
    }

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null || customer.getMembershipPackage() == null || bookingTime == null) {
            return price;
        }
        if (!customer.getMembershipPackage().hasAward(Award.DISCOUNTS)) {
            return price;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date bookingDateTime = sdf.parse(bookingTime);
            Date departure = combineDateTime(this.departureDate, this.departureTime);
            if (departure == null) {
                return price;
            }
            long diff = departure.getTime() - bookingDateTime.getTime();
            long hours = diff / (1000L * 60 * 60);
            if (hours >= 24) {
                double discounted = price * 0.8;
                return Math.round(discounted * 10.0) / 10.0;
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
        int points = 0;
        if (bookings == null) {
            return 0;
        }
        for (Booking b : bookings) {
            if (b != null && b.isInMonth(currentMonth)) {
                points += b.getNumberOfSeats() * 5;
            }
        }
        return points;
    }

    public Set<String> getStopStations() {
        Set<String> set = new HashSet<String>();
        if (stops == null) {
            return set;
        }
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
        if (this.departureTime == null || this.arrivalTime == null) {
            return false;
        }
        int t1Dep = toMinutes(this.departureTime);
        int t1Arr = toMinutes(this.arrivalTime);
        int t2Dep = toMinutes(newDepartureTime);
        int t2Arr = toMinutes(newArrivalTime);
        return t1Dep < t2Arr && t2Dep < t1Arr;
    }

    private int toMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    private Date combineDateTime(Date date, String time) {
        if (date == null || time == null) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(dateFmt.format(date) + " " + time);
        } catch (Exception e) {
            return null;
        }
    }
}
