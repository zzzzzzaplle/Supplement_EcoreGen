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
        bookings = new ArrayList<Booking>();
        stops = new ArrayList<Stop>();
    }

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null || bookingTime == null || customer.getMembershipPackage() == null || departureDate == null) {
            return price;
        }
        if (!customer.getMembershipPackage().hasAward(Award.DISCOUNTS)) return price;
        return Math.round(price * 0.8 * 10.0) / 10.0;
    }

    public int getBookedSeats() {
        int sum = 0;
        if (bookings != null) {
            for (Booking b : bookings) {
                if (b != null) sum += b.getNumberOfSeats();
            }
        }
        return sum;
    }

    public List<Booking> getBookings() { return bookings; }
    public void addBooking(Booking booking) { if (booking != null) bookings.add(booking); }
    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || customer.getMembershipPackage() == null || currentMonth == null || !customer.getMembershipPackage().hasAward(Award.POINTS)) return 0;
        int points = 0;
        if (bookings != null) {
            for (Booking b : bookings) if (b != null && b.getCustomer() == customer && b.isInMonth(currentMonth)) points += b.getNumberOfSeats() * 5;
        }
        return points;
    }
    public Set<String> getStopStations() {
        Set<String> set = new HashSet<String>();
        if (stops != null) for (Stop s : stops) if (s != null && s.getStopStation() != null) set.add(s.getStopStation());
        return set;
    }
    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (departureTime == null || arrivalTime == null || newDepartureTime == null || newArrivalTime == null) return false;
        return !(newArrivalTime.compareTo(departureTime) <= 0 || newDepartureTime.compareTo(arrivalTime) >= 0);
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
    public void addStop(Stop stop) { if (stop != null) stops.add(stop); }
}
