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

        MembershipPackage membershipPackage = customer.getMembershipPackage();
        if (membershipPackage == null) {
            return this.price;
        }

        if (!membershipPackage.hasAward(Award.DISCOUNTS)) {
            return this.price;
        }

        if (departureDate == null || departureTime == null) {
            return this.price;
        }

        long twentyFourHoursInMillis = 24L * 60 * 60 * 1000;
        long bookingTimeMs = parseBookingTimeToMillis(bookingTime);
        long departureTimeMs = parseTimeToMillis(departureDate, departureTime);

        if (departureTimeMs - bookingTimeMs < twentyFourHoursInMillis) {
            return this.price;
        }

        double discounted = this.price * 0.8;
        discounted = Math.round(discounted * 10.0) / 10.0;
        return discounted;
    }

    public int getBookedSeats() {
        if (bookings == null) {
            return 0;
        }
        int total = 0;
        for (Booking b : bookings) {
            if (b != null) {
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
        MembershipPackage membershipPackage = customer.getMembershipPackage();
        if (membershipPackage == null) {
            return 0;
        }
        if (!membershipPackage.hasAward(Award.POINTS)) {
            return 0;
        }
        if (bookings == null) {
            return 0;
        }
        int totalPoints = 0;
        for (Booking booking : bookings) {
            if (booking != null && booking.isInMonth(currentMonth)) {
                totalPoints += booking.getNumberOfSeats() * 5;
            }
        }
        return totalPoints;
    }

    public Set<String> getStopStations() {
        Set<String> stations = new HashSet<String>();
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
        if (newDepartureTime == null || newArrivalTime == null) {
            return false;
        }
        if (departureTime == null || arrivalTime == null) {
            return false;
        }

        long existingDepMs = timeStringToMillis(departureTime);
        long existingArrMs = timeStringToMillis(arrivalTime);
        long newDepMs = timeStringToMillis(newDepartureTime);
        long newArrMs = timeStringToMillis(newArrivalTime);

        if (existingDepMs < newArrMs && newDepMs < existingArrMs) {
            return true;
        }
        return false;
    }

    private long parseTimeToMillis(Date date, String time) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);
        try {
            java.util.Date parsed = sdf.parse(dateStr + " " + time);
            return parsed.getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    private long parseBookingTimeToMillis(String bookingTime) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            java.util.Date parsed = sdf.parse(bookingTime);
            return parsed.getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    private long timeStringToMillis(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return (long) hours * 3600000 + (long) minutes * 60000;
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
