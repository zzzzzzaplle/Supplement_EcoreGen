import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
        if (customer == null) {
            return price;
        }
        if (customer.getMembershipPackage() == null) {
            return price;
        }
        if (!customer.getMembershipPackage().hasAward(Award.DISCOUNTS)) {
            return price;
        }
        if (bookingTime == null) {
            return price;
        }
        if (departureDate == null || departureTime == null) {
            return price;
        }

        Calendar dep = Calendar.getInstance();
        dep.setTime(departureDate);
        String[] parts = departureTime.split(":");
        dep.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0].trim()));
        dep.set(Calendar.MINUTE, Integer.parseInt(parts[1].trim()));
        dep.set(Calendar.SECOND, 0);
        dep.set(Calendar.MILLISECOND, 0);

        Calendar book = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            book.setTime(sdf.parse(bookingTime));
        } catch (Exception e) {
            return price;
        }

        long diff = dep.getTimeInMillis() - book.getTimeInMillis();
        if (diff < 24L * 60L * 60L * 1000L) {
            return price;
        }

        BigDecimal bd = new BigDecimal(price * 0.8);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public int getBookedSeats() {
        int total = 0;
        if (bookings != null) {
            for (Booking b : bookings) {
                total += b.getNumberOfSeats();
            }
        }
        return total;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void addBooking(Booking booking) {
        if (bookings == null) {
            bookings = new ArrayList<Booking>();
        }
        bookings.add(booking);
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || currentMonth == null) {
            return 0;
        }
        if (customer.getMembershipPackage() == null) {
            return 0;
        }
        if (!customer.getMembershipPackage().hasAward(Award.POINTS)) {
            return 0;
        }

        int points = 0;
        if (bookings != null) {
            for (Booking b : bookings) {
                if (b.getCustomer() == customer && b.isInMonth(currentMonth)) {
                    points += b.getNumberOfSeats() * 5;
                }
            }
        }
        return points;
    }

    public Set<String> getStopStations() {
        Set<String> result = new HashSet<String>();
        if (stops != null) {
            for (Stop s : stops) {
                result.add(s.getStopStation());
            }
        }
        return result;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (departureTime == null || arrivalTime == null) {
            return false;
        }
        if (newDepartureTime == null || newArrivalTime == null) {
            return false;
        }

        int t1Start = parseTime(departureTime);
        int t1End = parseTime(arrivalTime);
        int t2Start = parseTime(newDepartureTime);
        int t2End = parseTime(newArrivalTime);

        return t1Start < t2End && t2Start < t1End;
    }

    private int parseTime(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0].trim()) * 60 + Integer.parseInt(parts[1].trim());
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
        if (stops == null) {
            stops = new ArrayList<Stop>();
        }
        stops.add(stop);
    }
}
