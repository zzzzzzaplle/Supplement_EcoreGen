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
        if (customer == null) {
            return price;
        }
        MembershipPackage mp = customer.getMembershipPackage();
        if (mp == null) {
            return price;
        }
        if (!mp.hasAward(Award.DISCOUNTS)) {
            return price;
        }
        if (bookingTime == null || departureTime == null || departureDate == null) {
            return price;
        }
        try {
            String[] depParts = departureTime.split(":");
            int depHour = Integer.parseInt(depParts[0]);
            int depMinute = depParts.length > 1 ? Integer.parseInt(depParts[1]) : 0;
            String[] bookParts = bookingTime.split(":");
            int bookHour = Integer.parseInt(bookParts[0]);
            int bookMinute = bookParts.length > 1 ? Integer.parseInt(bookParts[1]) : 0;
            int depTotalMin = depHour * 60 + depMinute;
            int bookTotalMin = bookHour * 60 + bookMinute;
            int diff = depTotalMin - bookTotalMin;
            if (diff >= 24 * 60) {
                double discounted = price * 0.8;
                return Math.round(discounted * 10.0) / 10.0;
            }
            return price;
        } catch (Exception e) {
            return price;
        }
    }

    public int getBookedSeats() {
        int total = 0;
        if (bookings != null) {
            for (Booking b : bookings) {
                if (b != null && b.getNumberOfSeats() > 0) {
                    total += b.getNumberOfSeats();
                }
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
        MembershipPackage mp = customer.getMembershipPackage();
        if (mp == null || !mp.hasAward(Award.POINTS)) {
            return 0;
        }
        int total = 0;
        if (bookings != null) {
            for (Booking b : bookings) {
                if (b != null && b.isInMonth(currentMonth)) {
                    total += b.getNumberOfSeats() * 5;
                }
            }
        }
        return total;
    }

    public Set<String> getStopStations() {
        Set<String> set = new HashSet<String>();
        if (stops != null) {
            for (Stop s : stops) {
                if (s != null) {
                    set.add(s.getStopStation());
                }
            }
        }
        return set;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (newDepartureTime == null || newArrivalTime == null
                || departureTime == null || arrivalTime == null) {
            return false;
        }
        try {
            int[] newDep = parseTime(newDepartureTime);
            int[] newArr = parseTime(newArrivalTime);
            int[] existingDep = parseTime(departureTime);
            int[] existingArr = parseTime(arrivalTime);
            int newDepMin = newDep[0] * 60 + newDep[1];
            int newArrMin = newArr[0] * 60 + newArr[1];
            int existDepMin = existingDep[0] * 60 + existingDep[1];
            int existArrMin = existingArr[0] * 60 + existingArr[1];
            // Overlap if new starts before existing ends and new ends after existing starts
            return newDepMin < existArrMin && newArrMin > existDepMin;
        } catch (Exception e) {
            return false;
        }
    }

    private int[] parseTime(String time) {
        String[] parts = time.split(":");
        int h = Integer.parseInt(parts[0]);
        int m = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        return new int[]{h, m};
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
