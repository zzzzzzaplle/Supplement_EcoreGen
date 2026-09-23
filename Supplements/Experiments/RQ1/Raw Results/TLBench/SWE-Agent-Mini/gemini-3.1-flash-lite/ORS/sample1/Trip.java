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
    public void addBooking(Booking b) { bookings.add(b); }
    public List<Stop> getStops() { return stops; }
    public void addStop(Stop s) { stops.add(s); }

    public int getBookedSeats() {
        int seats = 0;
        for (Booking b : bookings) seats += b.getNumberOfSeats();
        return seats;
    }

    public boolean isTimeConflicting(String newDepTime, String newArrTime) {
        // String time format HH:MM
        return !(newArrTime.compareTo(this.departureTime) <= 0 || newDepTime.compareTo(this.arrivalTime) >= 0);
    }

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null || customer.getMembershipPackage() == null || !customer.getMembershipPackage().hasAward(Award.DISCOUNTS)) return price;
        
        // Assumes bookingTime is format "yyyy-MM-dd HH:mm"
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date bTime = sdf.parse(bookingTime);
            String depDateTimeStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(departureDate) + " " + departureTime;
            Date dTime = sdf.parse(depDateTimeStr);
            long diff = dTime.getTime() - bTime.getTime();
            if (diff >= 24 * 60 * 60 * 1000) {
                return Math.round(price * 0.8 * 10.0) / 10.0;
            }
        } catch (Exception e) {}
        return price;
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || customer.getMembershipPackage() == null || !customer.getMembershipPackage().hasAward(Award.POINTS)) return 0;
        int seats = 0;
        for (Booking b : bookings) {
            if (b.getCustomer().equals(customer) && b.isInMonth(currentMonth)) {
                seats += b.getNumberOfSeats();
            }
        }
        return seats * 5;
    }

    public Set<String> getStopStations() {
        Set<String> stopNames = new HashSet<>();
        for (Stop s : stops) stopNames.add(s.getStopStation());
        return stopNames;
    }
}
