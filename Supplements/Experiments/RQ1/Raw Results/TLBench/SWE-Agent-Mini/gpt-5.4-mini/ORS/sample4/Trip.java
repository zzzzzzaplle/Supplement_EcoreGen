import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Trip {
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
        if (customer == null || bookingTime == null || departureTime == null) {
            return price;
        }
        MembershipPackage membershipPackage = customer.getMembershipPackage();
        if (membershipPackage == null || !membershipPackage.hasAward(Award.DISCOUNTS)) {
            return price;
        }
        return Math.round(price * 0.8 * 10.0) / 10.0;
    }

    public int getBookedSeats() {
        return 0;
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
        if (customer == null || currentMonth == null) {
            return 0;
        }
        MembershipPackage membershipPackage = customer.getMembershipPackage();
        if (membershipPackage == null || !membershipPackage.hasAward(Award.POINTS)) {
            return 0;
        }
        int total = 0;
        if (bookings != null) {
            for (Booking booking : bookings) {
                if (booking != null && booking.isInMonth(currentMonth)) {
                    total += booking.getNumberOfSeats() * 5;
                }
            }
        }
        return total;
    }

    public Set<String> getStopStations() {
        Set<String> stations = new HashSet<String>();
        if (stops != null) {
            for (Stop stop : stops) {
                if (stop != null && stop.getStopStation() != null) {
                    stations.add(stop.getStopStation());
                }
            }
        }
        return stations;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (departureTime == null || arrivalTime == null || newDepartureTime == null || newArrivalTime == null) {
            return false;
        }
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
    public void addStop(Stop stop) { if (stops == null) { stops = new ArrayList<Stop>(); } stops.add(stop); }
}
