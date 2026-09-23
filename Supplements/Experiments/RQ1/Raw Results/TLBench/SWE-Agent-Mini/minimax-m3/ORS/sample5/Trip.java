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
            return price;
        }
        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null) {
            return price;
        }
        if (!pkg.hasAward(Award.DISCOUNTS)) {
            return price;
        }
        if (departureTime == null) {
            return price;
        }
        if (!isAtLeast24HoursBefore(bookingTime, departureTime)) {
            return price;
        }
        double discounted = price * 0.8;
        return Math.round(discounted * 10.0) / 10.0;
    }

    private boolean isAtLeast24HoursBefore(String bookingTime, String depTime) {
        try {
            String[] b = bookingTime.split(":");
            String[] d = depTime.split(":");
            int bh = Integer.parseInt(b[0]);
            int bm = Integer.parseInt(b[1]);
            int dh = Integer.parseInt(d[0]);
            int dm = Integer.parseInt(d[1]);
            int bookingMinutes = bh * 60 + bm;
            int depMinutes = dh * 60 + dm;
            int diff = depMinutes - bookingMinutes;
            if (diff >= 24 * 60) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public int getBookedSeats() {
        int total = 0;
        if (bookings != null) {
            for (Booking b : bookings) {
                if (b != null) {
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
        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null || !pkg.hasAward(Award.POINTS)) {
            return 0;
        }
        int total = 0;
        if (bookings != null) {
            for (Booking b : bookings) {
                if (b == null) {
                    continue;
                }
                if (b.getCustomer() == null || !b.getCustomer().equals(customer)) {
                    continue;
                }
                if (b.isInMonth(currentMonth)) {
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
                if (s != null && s.getStopStation() != null) {
                    set.add(s.getStopStation());
                }
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
        int existingStart = toMinutes(departureTime);
        int existingEnd = toMinutes(arrivalTime);
        int newStart = toMinutes(newDepartureTime);
        int newEnd = toMinutes(newArrivalTime);
        if (existingStart < 0 || existingEnd < 0 || newStart < 0 || newEnd < 0) {
            return false;
        }
        return newStart < existingEnd && existingStart < newEnd;
    }

    private int toMinutes(String t) {
        try {
            String[] parts = t.split(":");
            int h = Integer.parseInt(parts[0]);
            int m = Integer.parseInt(parts[1]);
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
