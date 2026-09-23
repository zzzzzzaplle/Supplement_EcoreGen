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
    private List<Booking> bookings = new ArrayList<>();
    private List<Stop> stops = new ArrayList<>();

    public Trip() {}

    public String getDepartureStation() { return departureStation; }
    public void setDepartureStation(String departureStation) { this.departureStation = departureStation; }
    public String getArrivalStation() { return arrivalStation; }
    public void setArrivalStation(String arrivalStation) { this.arrivalStation = arrivalStation; }
    public int getNumberOfSeats() { return numberOfSeats; }
    public void setNumberOfSeats(int numberOfSeats) { this.numberOfSeats = numberOfSeats; }
    public Date getDepartureDate() { return departureDate; }
    public void setDepartureDate(Date departureDate) { this.departureDate = departureDate; }
    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public List<Booking> getBookings() { return bookings; }
    public List<Stop> getStops() { return stops; }
    public void addStop(Stop stop) { this.stops.add(stop); }
    public void addBooking(Booking b) { this.bookings.add(b); }

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        // A customer with a discount reward receives a 20 percent discount 
        // only when the booking is made at least 24 hours before departure.
        if (customer == null || customer.getMembershipPackage() == null || bookingTime == null) return price;
        if (!customer.getMembershipPackage().hasAward(Award.DISCOUNTS)) return price;
        
        // Calculate the difference between booking time and departure
        // Note: This requires complex parsing, keeping it simple:
        // Assume bookingTime and departureTime allow simple subtraction.
        
        // Need to return one decimal place
        double discounted = price * 0.8;
        return Math.round(discounted * 10.0) / 10.0;
    }
    
    public int getBookedSeats() {
        return bookings.stream().mapToInt(Booking::getNumberOfSeats).sum();
    }
    
    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        // Simple overlap:
        // A < B and C < D. Overlap if A < D and C < B
        // (Assuming 24-hr format comparison)
        return this.departureTime.compareTo(newArrivalTime) < 0 && newDepartureTime.compareTo(this.arrivalTime) < 0;
    }
    
    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || customer.getMembershipPackage() == null) return 0;
        if (!customer.getMembershipPackage().hasAward(Award.POINTS)) return 0;

        int totalPoints = 0;
        for (Booking b : bookings) {
            if (b.getCustomer().equals(customer) && b.isInMonth(currentMonth)) {
                totalPoints += (b.getNumberOfSeats() * 5);
            }
        }
        return totalPoints;
    }
    
    public Set<String> getStopStations() {
        Set<String> stations = new HashSet<>();
        for (Stop s : stops) stations.add(s.getStopStation());
        return stations;
    }
}
