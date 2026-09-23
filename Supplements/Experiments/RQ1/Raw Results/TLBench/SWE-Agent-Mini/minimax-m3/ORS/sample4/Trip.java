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
        if (customer == null || customer.getMembershipPackage() == null || bookingTime == null) {
            return getPrice();
        }
        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null || !pkg.hasAward(Award.DISCOUNTS)) {
            return getPrice();
        }
        // Check if booking is made at least 24 hours before departure
        if (departureDate == null || departureTime == null) {
            return getPrice();
        }
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
            java.util.Date tripDeparture = sdf.parse(formatDate(departureDate) + " " + departureTime);
            java.util.Date bookingDateTime = sdf.parse(bookingTime);
            long diffMillis = tripDeparture.getTime() - bookingDateTime.getTime();
            long diffHours = diffMillis / (1000 * 60 * 60);
            if (diffHours >= 24) {
                double discounted = getPrice() * 0.8;
                return Math.round(discounted * 10.0) / 10.0;
            }
        } catch (Exception e) {
            return getPrice();
        }
        return getPrice();
    }

    private String formatDate(Date date) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
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
        if (customer == null || customer.getMembershipPackage() == null || currentMonth == null) {
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
        Set<String> stations = new HashSet<String>();
        if (stops != null) {
            for (Stop s : stops) {
                if (s != null && s.getStopStation() != null) {
                    stations.add(s.getStopStation());
                }
            }
        }
        return stations;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (newDepartureTime == null || newArrivalTime == null || departureTime == null || arrivalTime == null) {
            return false;
        }
        try {
            int existingStart = timeToMinutes(departureTime);
            int existingEnd = timeToMinutes(arrivalTime);
            int newStart = timeToMinutes(newDepartureTime);
            int newEnd = timeToMinutes(newArrivalTime);
            // Overlap if start < other end AND end > other start
            return newStart < existingEnd && newEnd > existingStart;
        } catch (Exception e) {
            return false;
        }
    }

    private int timeToMinutes(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        return hours * 60 + minutes;
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
