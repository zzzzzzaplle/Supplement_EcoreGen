import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Trip {
    private String departureStation;
    private String arrivalStation;
    private int numberOfSeats;
    private Date departureDate;
    private String departureTime;
    private String arrivalTime;
    private double price;
    private java.util.List<Booking> bookings;
    private java.util.List<Stop> stops;

    public Trip() {
        this.bookings = new java.util.ArrayList<Booking>();
        this.stops = new java.util.ArrayList<Stop>();
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

    public java.util.List<Booking> getBookings() {
        return bookings;
    }

    public void addBooking(Booking booking) {
        if (this.bookings == null) {
            this.bookings = new java.util.ArrayList<Booking>();
        }
        this.bookings.add(booking);
    }

    public java.util.List<Stop> getStops() {
        return stops;
    }

    public void addStop(Stop stop) {
        if (this.stops == null) {
            this.stops = new java.util.ArrayList<Stop>();
        }
        this.stops.add(stop);
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

    public Set<String> getStopStations() {
        Set<String> stationSet = new HashSet<String>();
        if (stops != null) {
            for (Stop s : stops) {
                if (s != null && s.getStopStation() != null) {
                    stationSet.add(s.getStopStation());
                }
            }
        }
        return stationSet;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (newDepartureTime == null || newArrivalTime == null
                || this.departureTime == null || this.arrivalTime == null) {
            return false;
        }
        int newDep = toMinutes(newDepartureTime);
        int newArr = toMinutes(newArrivalTime);
        int curDep = toMinutes(this.departureTime);
        int curArr = toMinutes(this.arrivalTime);
        // Overlap if newDep < curArr AND curDep < newArr
        return newDep < curArr && curDep < newArr;
    }

    private int toMinutes(String time) {
        try {
            String[] parts = time.split(":");
            return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
        } catch (Exception e) {
            return 0;
        }
    }

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null || customer.getMembershipPackage() == null
                || bookingTime == null || this.departureTime == null) {
            return Math.round(this.price * 10.0) / 10.0;
        }
        if (!customer.getMembershipPackage().hasAward(Award.DISCOUNTS)) {
            return Math.round(this.price * 10.0) / 10.0;
        }
        int bookingMin = toMinutes(bookingTime);
        int depMin = toMinutes(this.departureTime);
        int diff = depMin - bookingMin;
        if (diff >= 24 * 60) {
            double discounted = this.price * 0.8;
            return Math.round(discounted * 10.0) / 10.0;
        }
        return Math.round(this.price * 10.0) / 10.0;
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
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
}
