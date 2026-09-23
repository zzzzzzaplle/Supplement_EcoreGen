import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        this.bookings = new ArrayList<Booking>();
        this.stops = new ArrayList<Stop>();
    }

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null || bookingTime == null) {
            return price;
        }
        MembershipPackage mp = customer.getMembershipPackage();
        if (mp == null) {
            return price;
        }
        if (!mp.hasAward(Award.DISCOUNTS)) {
            return price;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date bookingDt = sdf.parse(bookingTime);
            String depStr = departureDate.getTime() + " " + departureTime;
            Date departDt = sdf.parse(depStr);

            long diffMs = departDt.getTime() - bookingDt.getTime();
            long twentyFourHoursMs = 24 * 60 * 60 * 1000L;
            if (diffMs >= twentyFourHoursMs) {
                double discounted = price * 0.8;
                return Math.round(discounted * 10) / 10.0;
            }
        } catch (ParseException e) {
            return price;
        }
        return price;
    }

    public int getBookedSeats() {
        int booked = 0;
        if (bookings != null) {
            for (Booking b : bookings) {
                booked += b.getNumberOfSeats();
            }
        }
        return booked;
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
        MembershipPackage mp = customer.getMembershipPackage();
        if (mp == null || !mp.hasAward(Award.POINTS)) {
            return 0;
        }
        int totalPoints = 0;
        if (customer.getBookings() != null) {
            for (Booking b : customer.getBookings()) {
                if (b.getTrip() == this && b.isInMonth(currentMonth)) {
                    totalPoints += b.getNumberOfSeats() * 5;
                }
            }
        }
        return totalPoints;
    }

    public Set<String> getStopStations() {
        Set<String> stations = new HashSet<String>();
        if (stops != null) {
            for (Stop s : stops) {
                stations.add(s.getStopStation());
            }
        }
        return stations;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (departureTime == null || arrivalTime == null || newDepartureTime == null || newArrivalTime == null) {
            return false;
        }
        int dep1 = timeToMinutes(departureTime);
        int arr1 = timeToMinutes(arrivalTime);
        int dep2 = timeToMinutes(newDepartureTime);
        int arr2 = timeToMinutes(newArrivalTime);
        if (dep1 == arr2 || dep2 == arr1) {
            return false;
        }
        return !(arr1 <= dep2 || arr2 <= dep1);
    }

    private int timeToMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
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
        if (stops == null) {
            stops = new ArrayList<Stop>();
        }
        stops.add(stop);
    }
}
