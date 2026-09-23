import java.util.Calendar;
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

    public Trip() { }

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null) {
            return price;
        }
        if (customer.getMembershipPackage() == null) {
            return price;
        }
        if (!customer.getMembershipPackage().hasAward(Award.DISCOUNTS)) {
            return price;
        }
        if (departureDate == null || departureTime == null) {
            return price;
        }
        if (bookingTime == null || bookingTime.isEmpty()) {
            return price;
        }

        Calendar bookingCal = Calendar.getInstance();
        String[] timeParts = bookingTime.split(":");
        if (timeParts.length >= 2) {
            bookingCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
            bookingCal.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
        }

        Calendar depCal = Calendar.getInstance();
        depCal.setTime(departureDate);
        String[] depParts = departureTime.split(":");
        if (depParts.length >= 2) {
            depCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(depParts[0]));
            depCal.set(Calendar.MINUTE, Integer.parseInt(depParts[1]));
        }

        long diff = depCal.getTimeInMillis() - bookingCal.getTimeInMillis();
        long twentyFourHours = 24L * 60 * 60 * 1000L;

        if (diff >= twentyFourHours) {
            return Math.round(price * 0.8 * 10.0) / 10.0;
        }

        return price;
    }

    public int getBookedSeats() {
        if (bookings == null) {
            return 0;
        }
        int total = 0;
        for (Booking b : bookings) {
            total += b.getNumberOfSeats();
        }
        return total;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void addBooking(Booking booking) {
        if (bookings == null) {
            bookings = new ArrayList<>();
        }
        bookings.add(booking);
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || customer.getMembershipPackage() == null) {
            return 0;
        }
        if (!customer.getMembershipPackage().hasAward(Award.POINTS)) {
            return 0;
        }
        if (bookings == null) {
            return 0;
        }

        int total = 0;
        for (Booking b : bookings) {
            if (b != null && b.getCustomer() != null && b.getCustomer().equals(customer) && b.isInMonth(currentMonth)) {
                total += 5 * b.getNumberOfSeats();
            }
        }
        return total;
    }

    public Set<String> getStopStations() {
        Set<String> stations = new HashSet<>();
        if (stops == null) {
            return stations;
        }
        for (Stop stop : stops) {
            if (stop != null && stop.getStopStation() != null) {
                stations.add(stop.getStopStation());
            }
        }
        return stations;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        int newDep = parseTimeToMinutes(newDepartureTime);
        int newArr = parseTimeToMinutes(newArrivalTime);
        int thisDep = parseTimeToMinutes(this.departureTime);
        int thisArr = parseTimeToMinutes(this.arrivalTime);

        if (newDep < 0 || newArr < 0 || thisDep < 0 || thisArr < 0) {
            return false;
        }

        return newDep < thisArr && thisDep < newArr;
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
        stops.add(stop);
    }

    private int parseTimeToMinutes(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) {
            return -1;
        }
        try {
            String[] parts = timeStr.split(":");
            return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
        } catch (Exception e) {
            return -1;
        }
    }
}
