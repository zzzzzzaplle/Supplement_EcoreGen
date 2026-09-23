import java.util.ArrayList;
import java.util.Calendar;
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
        this.bookings = new ArrayList<Booking>();
        this.stops = new ArrayList<Stop>();
    }

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null || bookingTime == null || this.price <= 0) {
            return roundToOneDecimal(this.price);
        }
        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null || !pkg.hasAward(Award.DISCOUNTS)) {
            return roundToOneDecimal(this.price);
        }
        if (this.departureTime == null) {
            return roundToOneDecimal(this.price);
        }
        if (bookingTime.compareTo(this.departureTime) >= 0) {
            return roundToOneDecimal(this.price);
        }
        try {
            String[] bParts = bookingTime.split(":");
            int bHour = Integer.parseInt(bParts[0]);
            int bMin = 0;
            if (bParts.length > 1) {
                bMin = Integer.parseInt(bParts[1]);
            }
            String[] dParts = this.departureTime.split(":");
            int dHour = Integer.parseInt(dParts[0]);
            int dMin = 0;
            if (dParts.length > 1) {
                dMin = Integer.parseInt(dParts[1]);
            }
            int bMinutes = bHour * 60 + bMin;
            int dMinutes = dHour * 60 + dMin;
            if (dMinutes - bMinutes >= 24 * 60) {
                return roundToOneDecimal(this.price * 0.8);
            } else {
                return roundToOneDecimal(this.price);
            }
        } catch (Exception e) {
            return roundToOneDecimal(this.price);
        }
    }

    private double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    public int getBookedSeats() {
        if (bookings == null) {
            return 0;
        }
        int total = 0;
        for (Booking b : bookings) {
            if (b != null) {
                total += b.getNumberOfSeats();
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
        if (this.bookings == null) {
            this.bookings = new ArrayList<Booking>();
        }
        this.bookings.add(booking);
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || currentMonth == null) {
            return 0;
        }
        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null || !pkg.hasAward(Award.POINTS)) {
            return 0;
        }
        if (this.bookings == null) {
            return 0;
        }
        int total = 0;
        for (Booking b : this.bookings) {
            if (b != null && b.isInMonth(currentMonth) && b.getCustomer() != null
                    && b.getCustomer().getId() != null
                    && b.getCustomer().getId().equals(customer.getId())) {
                total += b.getNumberOfSeats() * 5;
            }
        }
        return total;
    }

    public Set<String> getStopStations() {
        Set<String> result = new HashSet<String>();
        if (stops == null) {
            return result;
        }
        for (Stop s : stops) {
            if (s != null && s.getStopStation() != null) {
                result.add(s.getStopStation());
            }
        }
        return result;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (newDepartureTime == null || newArrivalTime == null
                || this.departureTime == null || this.arrivalTime == null) {
            return false;
        }
        if (newDepartureTime.equals(this.departureTime) && newArrivalTime.equals(this.arrivalTime)) {
            return true;
        }
        return newDepartureTime.compareTo(this.arrivalTime) < 0
                && this.departureTime.compareTo(newArrivalTime) < 0;
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
        if (this.stops == null) {
            this.stops = new ArrayList<Stop>();
        }
        this.stops.add(stop);
    }
}
