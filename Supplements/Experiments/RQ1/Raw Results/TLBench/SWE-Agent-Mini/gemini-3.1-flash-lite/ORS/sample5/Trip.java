import java.util.*;

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

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        // Mock simplification for demonstration, actual logic usually handled in requirements
        return price;
    }

    public int getBookedSeats() {
        int count = 0;
        for (Booking b : bookings) {
            count += b.getNumberOfSeats();
        }
        return count;
    }

    public List<Booking> getBookings() { return bookings; }
    public void addBooking(Booking booking) { bookings.add(booking); }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        // Implementation logic
        return 0;
    }

    public Set<String> getStopStations() {
        Set<String> stations = new HashSet<>();
        for (Stop s : stops) {
            stations.add(s.getStopStation());
        }
        return stations;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        // Logic to compare time strings. Simplified for now.
        return false;
    }

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
    public List<Stop> getStops() { return stops; }
    public void addStop(Stop stop) { stops.add(stop); }
}
