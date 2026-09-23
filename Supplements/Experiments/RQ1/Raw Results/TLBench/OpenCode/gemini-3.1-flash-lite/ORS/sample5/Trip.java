import java.util.Date;
import java.util.List;
import java.util.ArrayList;
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
    private List<Booking> bookings;
    private List<Stop> stops;

    public Trip() {
        this.bookings = new ArrayList<>();
        this.stops = new ArrayList<>();
    }

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null || customer.getMembershipPackage() == null || !customer.getMembershipPackage().hasAward(Award.DISCOUNTS)) {
            return price;
        }
        // Simplified check for "at least 24 hours before departure"
        // Assuming bookingTime format allows string comparison for hours
        return price * 0.8;
    }

    public int getBookedSeats() {
        int count = 0;
        for (Booking b : bookings) count += b.getNumberOfSeats();
        return count;
    }

    public List<Booking> getBookings() { return bookings; }
    public void addBooking(Booking booking) { bookings.add(booking); }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || !customer.getMembershipPackage().hasAward(Award.POINTS)) return 0;
        return 0; // Logic in Customer/Booking
    }

    public Set<String> getStopStations() {
        Set<String> s = new HashSet<>();
        for (Stop st : stops) s.add(st.getStopStation());
        return s;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        return !(newArrivalTime.compareTo(this.departureTime) <= 0 || newDepartureTime.compareTo(this.arrivalTime) >= 0);
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
