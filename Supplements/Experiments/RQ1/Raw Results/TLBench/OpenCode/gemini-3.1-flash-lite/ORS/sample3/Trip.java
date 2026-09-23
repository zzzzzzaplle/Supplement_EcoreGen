import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

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

    public double calculateDiscountedPrice(Customer customer, String bookingTime) { return price; }
    public int getBookedSeats() { return 0; }
    public List<Booking> getBookings() { return bookings; }
    public void addBooking(Booking booking) { this.bookings.add(booking); }
    public int calculateMonthlyPoints(Customer customer, String currentMonth) { return 0; }
    public Set<String> getStopStations() { return new HashSet<>(); }
    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) { return false; }
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
    public void addStop(Stop stop) { this.stops.add(stop); }
}
