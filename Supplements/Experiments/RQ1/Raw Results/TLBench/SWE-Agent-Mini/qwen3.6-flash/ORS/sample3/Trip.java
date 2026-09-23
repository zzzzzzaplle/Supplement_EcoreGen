import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

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
        this.bookings = new ArrayList<>();
        this.stops = new ArrayList<>();
    }

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (!membershipHasDiscountsAndEligible(customer, bookingTime)) {
            return Math.round(price * 10.0) / 10.0;
        }
        double discount = price * 0.20;
        return Math.round((price - discount) * 10.0) / 10.0;
    }

    private boolean membershipHasDiscountsAndEligible(Customer customer, String bookingTime) {
        if (customer == null || bookingTime == null) {
            return false;
        }
        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null || !pkg.hasAward(Award.DISCOUNTS)) {
            return false;
        }
        return isAtLeast24HoursBefore(bookingTime, departureDate, departureTime);
    }

    private boolean isAtLeast24HoursBefore(String bookingTimeStr, java.util.Date departureDate, String departureTimeStr) {
        if (departureDate == null || bookingTimeStr == null || departureTimeStr == null) {
            return false;
        }
        java.util.Calendar depCal = java.util.Calendar.getInstance();
        depCal.setTime(departureDate);
        String[] parts = departureTimeStr.split(":");
        depCal.set(java.util.Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
        depCal.set(java.util.Calendar.MINUTE, Integer.parseInt(parts[1]));
        depCal.set(java.util.Calendar.SECOND, parts.length > 2 ? parseSecond(parts[2]) : 0);

        java.util.Calendar bookCal = java.util.Calendar.getInstance();
        String[] bp = bookingTimeStr.split(":");
        bookCal.set(java.util.Calendar.HOUR_OF_DAY, Integer.parseInt(bp[0]));
        bookCal.set(java.util.Calendar.MINUTE, Integer.parseInt(bp[1]));
        bookCal.set(java.util.Calendar.SECOND, bp.length > 2 ? Integer.parseInt(bp[2]) : 0);

        if (depCal.get(java.util.Calendar.DATE) != bookCal.get(java.util.Calendar.DATE)
            || depCal.get(java.util.Calendar.MONTH) != bookCal.get(java.util.Calendar.MONTH)
            || depCal.get(java.util.Calendar.YEAR) != bookCal.get(java.util.Calendar.YEAR)) {
            return false;
        }

        long diffMs = depCal.getTime().getTime() - bookCal.getTime().getTime();
        long twentyFourHoursMs = 24L * 60 * 60 * 1000;
        return diffMs >= twentyFourHoursMs;
    }

    private int parseSecond(String s) {
        if (s == null) return 0;
        int idx = s.indexOf('.');
        if (idx > 0) {
            return Integer.parseInt(s.substring(0, idx));
        }
        return Integer.parseInt(s);
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
        if (booking != null && bookings != null) {
            bookings.add(booking);
        }
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (currentMonth == null) {
            return 0;
        }
        if (customer == null) {
            return 0;
        }
        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null || !pkg.hasAward(Award.POINTS)) {
            return 0;
        }
        int totalPoints = 0;
        if (bookings != null) {
            for (Booking b : bookings) {
                if (b != null && b.isInMonth(currentMonth)) {
                    totalPoints += b.getNumberOfSeats() * 5;
                }
            }
        }
        return totalPoints;
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
        if (this.departureTime == null || this.arrivalTime == null) {
            return false;
        }
        if (newDepartureTime == null || newArrivalTime == null) {
            return false;
        }
        int depCmp = compareTime(this.departureTime, newDepartureTime);
        int arrCmp = compareTime(this.arrivalTime, newDepartureTime);
        return depCmp > 0 && depCmp < arrCmp;
    }

    private int compareTime(String time1, String time2) {
        if (time1 == null || time2 == null) {
            return 0;
        }
        return time1.compareTo(time2);
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

    public void addStop(Stop stop) {
        if (stop != null && stops != null) {
            stops.add(stop);
        }
    }
}
