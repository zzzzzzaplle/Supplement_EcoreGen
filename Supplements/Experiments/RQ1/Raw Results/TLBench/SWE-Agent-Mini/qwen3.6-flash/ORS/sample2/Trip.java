import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;

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
        if (customer == null || customer.getMembershipPackage() == null) {
            return price;
        }
        
        MembershipPackage membershipPackage = customer.getMembershipPackage();
        if (!membershipPackage.hasAward(Award.DISCOUNTS)) {
            return price;
        }
        
        if (bookingTime == null || bookingTime.isEmpty()) {
            return price;
        }
        
        if (departureDate == null) {
            return price;
        }
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date bookingDate = sdf.parse(bookingTime);
            
            long diffMillis = departureDate.getTime() - bookingDate.getTime();
            long diffHours = diffMillis / (1000 * 60 * 60);
            
            if (diffHours >= 24) {
                double discountedPrice = price * 0.8;
                return Math.round(discountedPrice * 10.0) / 10.0;
            }
        } catch (ParseException e) {
            return price;
        }
        
        return price;
    }

    public int getBookedSeats() {
        int total = 0;
        if (bookings != null) {
            for (Booking booking : bookings) {
                if (booking != null) {
                    total += booking.getNumberOfSeats();
                }
            }
        }
        return total;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void addBooking(Booking booking) {
        if (booking != null && !bookings.contains(booking)) {
            bookings.add(booking);
        }
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || currentMonth == null || currentMonth.isEmpty()) {
            return 0;
        }
        
        if (customer.getMembershipPackage() == null) {
            return 0;
        }
        
        if (!customer.getMembershipPackage().hasAward(Award.POINTS)) {
            return 0;
        }
        
        int points = 0;
        if (bookings != null) {
            for (Booking booking : bookings) {
                if (booking != null && booking.isInMonth(currentMonth)) {
                    points += booking.getNumberOfSeats() * 5;
                }
            }
        }
        
        return points;
    }

    public Set<String> getStopStations() {
        Set<String> stations = new HashSet<>();
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
        
        String[] depParts = departureTime.split(":");
        String[] arrParts = arrivalTime.split(":");
        String[] newDepParts = newDepartureTime.split(":");
        String[] newArrParts = newArrivalTime.split(":");
        
        int currentDep = Integer.parseInt(depParts[0]) * 60 + Integer.parseInt(depParts[1]);
        int currentArr = Integer.parseInt(arrParts[0]) * 60 + Integer.parseInt(arrParts[1]);
        int newDep = Integer.parseInt(newDepParts[0]) * 60 + Integer.parseInt(newDepParts[1]);
        int newArr = Integer.parseInt(newArrParts[0]) * 60 + Integer.parseInt(newArrParts[1]);
        
        return Math.max(currentDep, newDep) < Math.min(currentArr, newArr);
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

    public void addStop(Stop stop) {
        if (stop != null && !stops.contains(stop)) {
            stops.add(stop);
        }
    }
}
