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
        this.bookings = new ArrayList<>();
        this.stops = new ArrayList<>();
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
        this.stops.add(stop);
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void addBooking(Booking booking) {
        this.bookings.add(booking);
    }

    public int getBookedSeats() {
        int bookedSeats = 0;
        for (Booking b : bookings) {
            bookedSeats += b.getNumberOfSeats();
        }
        return bookedSeats;
    }

    public Set<String> getStopStations() {
        Set<String> stations = new HashSet<>();
        for (Stop s : stops) {
            stations.add(s.getStopStation());
        }
        return stations;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        int existingDep = timeToMinutes(this.departureTime);
        int existingArr = timeToMinutes(this.arrivalTime);
        int newDep = timeToMinutes(newDepartureTime);
        int newArr = timeToMinutes(newArrivalTime);

        // No overlap if one ends exactly when the other starts (adjacent allowed)
        // Overlap if newDep < existingArr && newArr > existingDep
        return newDep < existingArr && newArr > existingDep;
    }

    private int timeToMinutes(String time) {
        if (time == null || !time.contains(":")) {
            return 0;
        }
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null || bookingTime == null) {
            return this.price;
        }
        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null) {
            return this.price;
        }
        if (!pkg.hasAward(Award.DISCOUNTS)) {
            return this.price;
        }
        // Check if booking is made at least 24 hours before departure
        if (this.departureTime == null || this.departureDate == null) {
            return this.price;
        }
        try {
            String[] depParts = this.departureTime.split(":");
            int depHour = Integer.parseInt(depParts[0]);
            int depMin = Integer.parseInt(depParts[1]);

            String[] bookParts = bookingTime.split(":");
            int bookHour = Integer.parseInt(bookParts[0]);
            int bookMin = Integer.parseInt(bookParts[1]);

            int depTotalMin = depHour * 60 + depMin;
            int bookTotalMin = bookHour * 60 + bookMin;

            // At least 24 hours (1440 minutes) before departure
            if (bookTotalMin + 1440 <= depTotalMin) {
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
        if (pkg == null) {
            return 0;
        }
        if (!pkg.hasAward(Award.POINTS)) {
            return 0;
        }
        int totalPoints = 0;
        for (Booking b : bookings) {
            if (b.getCustomer() == customer && b.isInMonth(currentMonth)) {
                totalPoints += b.getNumberOfSeats() * 5;
            }
        }
        return totalPoints;
    }
}
