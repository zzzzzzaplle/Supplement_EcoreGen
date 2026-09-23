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

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null || bookingTime == null || membershipMissing(customer) || departureTime == null || departureDate == null) {
            return price;
        }
        MembershipPackage membershipPackage = customer.getMembershipPackage();
        if (membershipPackage == null || !membershipPackage.hasAward(Award.DISCOUNTS)) {
            return price;
        }
        Date bookingDateTime = DateUtil.parseDateTime(bookingTime);
        Date departureDateTime = DateUtil.combineDateAndTime(departureDate, departureTime);
        if (bookingDateTime == null || departureDateTime == null) {
            return price;
        }
        long diff = departureDateTime.getTime() - bookingDateTime.getTime();
        if (diff < 24L * 60L * 60L * 1000L) {
            return price;
        }
        return Math.round(price * 0.8d * 10.0d) / 10.0d;
    }

    private boolean membershipMissing(Customer customer) {
        return customer.getMembershipPackage() == null;
    }

    public int getBookedSeats() {
        if (bookings == null) {
            return 0;
        }
        int total = 0;
        for (Booking booking : bookings) {
            if (booking != null) {
                total += booking.getNumberOfSeats();
            }
        }
        return total;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public void addBooking(Booking booking) {
        if (bookings == null) {
            bookings = new ArrayList<>();
        }
        if (booking != null) {
            bookings.add(booking);
        }
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || currentMonth == null || customer.getMembershipPackage() == null || !customer.getMembershipPackage().hasAward(Award.POINTS)) {
            return 0;
        }
        return customer.computeMonthlyRewardPoints(currentMonth);
    }

    public Set<String> getStopStations() {
        if (stops == null) {
            return new HashSet<>();
        }
        Set<String> stations = new HashSet<>();
        for (Stop stop : stops) {
            if (stop != null && stop.getStopStation() != null) {
                stations.add(stop.getStopStation());
            }
        }
        return stations;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (!isTimeValid() || newDepartureTime == null || newArrivalTime == null) {
            return false;
        }
        return !(newArrivalTime.compareTo(departureTime) <= 0 || newDepartureTime.compareTo(arrivalTime) >= 0);
    }

    public boolean isTimeValid() {
        if (departureTime == null || arrivalTime == null) {
            return false;
        }
        return departureTime.compareTo(arrivalTime) < 0;
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

    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }

    public void addStop(Stop stop) {
        if (stops == null) {
            stops = new ArrayList<>();
        }
        if (stop != null) {
            stops.add(stop);
        }
    }
}
