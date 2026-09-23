import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Date;

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
        int total = 0;
        for (Booking b : bookings) {
            total += b.getNumberOfSeats();
        }
        return total;
    }

    public int getAvailableSeats() {
        return numberOfSeats - getBookedSeats();
    }

    public Set<String> getStopStations() {
        Set<String> stations = new HashSet<String>();
        for (Stop s : stops) {
            stations.add(s.getStopStation());
        }
        return stations;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (newDepartureTime == null || newArrivalTime == null || departureTime == null || arrivalTime == null) {
            return false;
        }
        int ndt = Integer.parseInt(newDepartureTime.replace(":", ""));
        int nat = Integer.parseInt(newArrivalTime.replace(":", ""));
        int dt = Integer.parseInt(departureTime.replace(":", ""));
        int at = Integer.parseInt(arrivalTime.replace(":", ""));

        if (dt < nat && ndt < at) {
            return true;
        }
        return false;
    }

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null || bookingTime == null || departureTime == null || departureDate == null) {
            return price;
        }
        MembershipPackage mp = customer.getMembershipPackage();
        if (mp == null) {
            return price;
        }
        if (!mp.hasAward(Award.DISCOUNTS)) {
            return price;
        }
        String[] parts = bookingTime.split(" ");
        if (parts.length < 2) {
            return price;
        }
        String bookingDateStr = parts[0];
        String bookingTimeStr = parts[1];
        String depDateStr = departureDate.toString();
        String depTimeStr = departureTime;

        int bookingDay = Integer.parseInt(bookingDateStr.split("-")[2]);
        int depDay = Integer.parseInt(depDateStr.split("-")[2]);
        int bookingMonth = Integer.parseInt(bookingDateStr.split("-")[1]);
        int depMonth = Integer.parseInt(depDateStr.split("-")[1]);
        int bookingYear = Integer.parseInt(bookingDateStr.split("-")[0]);
        int depYear = Integer.parseInt(depDateStr.split("-")[0]);

        long bookingMillis = getTimeInMillis(bookingDateStr, bookingTimeStr);
        long depMillis = getTimeInMillis(depDateStr, depTimeStr);

        long diff = depMillis - bookingMillis;
        long hours24 = 24 * 60 * 60 * 1000L;

        if (diff >= hours24) {
            double discounted = price * 0.8;
            return Math.round(discounted * 10.0) / 10.0;
        }
        return price;
    }

    private long getTimeInMillis(String dateStr, String timeStr) {
        String[] dateParts = dateStr.split("-");
        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]) - 1;
        int day = Integer.parseInt(dateParts[2]);
        String[] timeParts = timeStr.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(year, month, day, hour, minute, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || currentMonth == null) {
            return 0;
        }
        MembershipPackage mp = customer.getMembershipPackage();
        if (mp == null) {
            return 0;
        }
        if (!mp.hasAward(Award.POINTS)) {
            return 0;
        }
        int totalPoints = 0;
        for (Booking b : customer.getBookings()) {
            if (b.getTrip() == this && b.isInMonth(currentMonth)) {
                totalPoints += b.getNumberOfSeats() * 5;
            }
        }
        return totalPoints;
    }
}
