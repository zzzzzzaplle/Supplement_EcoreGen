import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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
        if (customer == null || bookingTime == null || bookingTime.isEmpty()) {
            return this.price;
        }
        if (customer.getMembershipPackage() == null) {
            return this.price;
        }
        Award[] awards = customer.getMembershipPackage().getAwards();
        if (awards == null || !customer.getMembershipPackage().hasAward(Award.DISCOUNTS)) {
            return this.price;
        }
        int bookingSecs;
        int departureSecs;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date bookingDate = sdf.parse(bookingTime);
            Date departureDate = this.departureDate;
            if (bookingDate == null || departureDate == null) {
                return this.price;
            }
            bookingSecs = (int) (bookingDate.getTime() / 1000);
            departureSecs = (int) (departureDate.getTime() / 1000);
        } catch (ParseException e) {
            return this.price;
        }
        int twentyFourHoursInSeconds = 24 * 60 * 60;
        if ((departureSecs - bookingSecs) >= twentyFourHoursInSeconds) {
            double discountPrice = this.price * 0.8;
            double result = Math.round(discountPrice * 10.0) / 10.0;
            if (Double.isNaN(result) || Double.isInfinite(result)) {
                return this.price;
            }
            return result;
        }
        return this.price;
    }

    public int getBookedSeats() {
        if (this.bookings == null) {
            return 0;
        }
        int total = 0;
        for (Booking booking : this.bookings) {
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
        if (booking != null && this.bookings != null) {
            this.bookings.add(booking);
        }
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null
                || currentMonth == null
                || currentMonth.isEmpty()
                || customer.getMembershipPackage() == null
                || !customer.getMembershipPackage().hasAward(Award.POINTS)) {
            return 0;
        }
        int[] totalPoints = new int[]{0};
        int[] pointsPerSeat = new int[]{5};
        for (Booking booking : this.bookings) {
            if (booking != null && booking.isInMonth(currentMonth)) {
                int seats = booking.getNumberOfSeats();
                if (seats <= 0) {
                    seats = 1;
                }
                totalPoints[0] += seats * pointsPerSeat[0];
            }
        }
        return totalPoints[0];
    }

    public Set<String> getStopStations() {
        Set<String> stations = new HashSet<>();
        if (this.stops != null) {
            for (Stop stop : this.stops) {
                if (stop != null) {
                    String station = stop.getStopStation();
                    if (station != null) {
                        stations.add(station);
                    }
                }
            }
        }
        return stations;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        int newDepSecs = parseTimeToSeconds(newDepartureTime);
        int newArrSecs = parseTimeToSeconds(newArrivalTime);
        if (newDepSecs < 0 || newArrSecs < 0) {
            return true;
        }
        if (newDepSecs >= newArrSecs) {
            return true;
        }
        for (Booking booking : this.bookings) {
            if (booking == null || booking.getTrip() == null) {
                continue;
            }
            String existingDep = booking.getTrip().getDepartureTime();
            String existingArr = booking.getTrip().getArrivalTime();
            if (existingDep == null || existingArr == null) {
                continue;
            }
            int existingDepSecs = parseTimeToSeconds(existingDep);
            int existingArrSecs = parseTimeToSeconds(existingArr);
            if (existingDepSecs < 0 || existingArrSecs < 0) {
                continue;
            }
            if (newDepSecs < existingArrSecs && newArrSecs > existingDepSecs) {
                return true;
            }
        }
        return false;
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
        if (stop != null && this.stops != null) {
            this.stops.add(stop);
        }
    }

    private int parseTimeToSeconds(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) {
            return -1;
        }
        try {
            String[] parts = timeStr.split(":");
            if (parts.length != 2) {
                return -1;
            }
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59) {
                return -1;
            }
            return hours * 3600 + minutes * 60;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
