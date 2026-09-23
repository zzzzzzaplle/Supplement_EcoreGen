import java.util.*;

public class Trip {
    private String departureStation;
    private String arrivalStation;
    private int numberOfSeats;
    private Date departureDate;
    private String departureTime;
    private String arrivalTime;
    private double price;
    private int seatsAvailable;
    private List<Booking> bookings = new ArrayList<>();
    private List<Stop> stops = new ArrayList<>();

    public Trip() {}
    
    public int getSeatsAvailable() { return seatsAvailable; }
    public void setSeatsAvailable(int seatsAvailable) { this.seatsAvailable = seatsAvailable; }

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null || customer.getMembershipPackage() == null || !customer.getMembershipPackage().hasAward(Award.DISCOUNTS)) {
            return price;
        }
        return Math.round(price * 0.8 * 10.0) / 10.0;
    }

    public int getBookedSeats() {
        int count = 0;
        for (Booking b : bookings) {
            count += b.getNumberOfSeats();
        }
        return count;
    }

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }
    public void addBooking(Booking booking) { 
        bookings.add(booking);
        this.seatsAvailable -= booking.getNumberOfSeats();
    }
    
    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || customer.getMembershipPackage() == null || !customer.getMembershipPackage().hasAward(Award.POINTS)) return 0;
        
        int points = 0;
        for (Booking b : customer.getBookings()) {
            if (b.isInMonth(currentMonth)) {
                points += b.getNumberOfSeats() * 5;
            }
        }
        return points;
    }

    public Set<String> getStopStations() {
        Set<String> stations = new HashSet<>();
        for (Stop s : stops) { stations.add(s.getStopStation()); }
        return stations;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        return this.departureTime.compareTo(newArrivalTime) < 0 && newDepartureTime.compareTo(this.arrivalTime) < 0;
    }

    public String getDepartureStation() { return departureStation; }
    public void setDepartureStation(String departureStation) { this.departureStation = departureStation; }
    public String getArrivalStation() { return arrivalStation; }
    public void setArrivalStation(String arrivalStation) { this.arrivalStation = arrivalStation; }
    public int getNumberOfSeats() { return numberOfSeats; }
    public void setNumberOfSeats(int numberOfSeats) { this.numberOfSeats = numberOfSeats; this.seatsAvailable = numberOfSeats; }
    public Date getDepartureDate() { return departureDate; }
    public void setDepartureDate(Date departureDate) { this.departureDate = departureDate; }
    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public List<Stop> getStops() { return stops; }
    public void setStops(List<Stop> stops) { this.stops = stops; }
    public void addStop(Stop stop) { stops.add(stop); }
}
