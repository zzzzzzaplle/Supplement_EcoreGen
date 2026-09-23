import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

class Trip {
    private String departureStation;
    private String arrivalStation;
    private int numberOfSeats;
    private LocalDate departureDate;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private double price;
    private List<Booking> bookings;
    private List<Stop> stops;

    public Trip() {
        bookings = new ArrayList<>();
        stops = new ArrayList<>();
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

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public void addStop(Stop stop) {
        stops.add(stop);
    }

    public int getBookedSeats() {
        if (bookings == null) {
            return 0;
        }
        int counted = 0;
        for (Booking b : bookings) {
            counted += b.getNumberOfSeats();
        }
        return counted;
    }

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null || bookingTime == null) {
            return price;
        }
        MembershipPackage mp = customer.getMembershipPackage();
        if (mp == null) {
            return price;
        }
        double result = price;
        if (mp.hasAward(Award.DISCOUNTS)) {
            try {
                LocalDateTime bookingTimeParsed = LocalDateTime.parse(bookingTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                LocalDateTime departureDateTime = LocalDateTime.of(departureDate, departureTime);
                long hoursBetween = java.time.temporal.ChronoUnit.HOURS.between(bookingTimeParsed, departureDateTime);
                if (hoursBetween >= 24) {
                    result = price * 0.8;
                    java.text.DecimalFormat df = new java.text.DecimalFormat("0.0");
                    result = Double.parseDouble(df.format(result));
                }
            } catch (Exception ex) {
                return price;
            }
        }
        return result;
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || currentMonth == null) {
            return 0;
        }
        MembershipPackage mp = customer.getMembershipPackage();
        if (mp == null) {
            return 0;
        }
        if (!mp.hasAward(Award.POINTS)) {
            return 0;
        }
        int totalPoints = 0;
        for (Booking booking : customer.getBookings()) {
            if (booking != null && booking.isInMonth(currentMonth)) {
                totalPoints += booking.getNumberOfSeats() * 5;
            }
        }
        return totalPoints;
    }

    public Set<String> getStopStations() {
        Set<String> stationSet = new HashSet<>();
        for (Stop stop : stops) {
            stationSet.add(stop.getStopStation());
        }
        return stationSet;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        try {
            LocalTime newDeparture = LocalTime.parse(newDepartureTime);
            LocalTime newArrival = LocalTime.parse(newArrivalTime);
            if (newDeparture.equals(departureTime) && newArrival.equals(arrivalTime)) {
                return true;
            }
            if (newDeparture.isBefore(arrivalTime) && newDeparture.isBefore(departureTime)) {
                if (newArrival.isAfter(departureTime)) {
                    return true;
                }
            }
            if (newDeparture.isBefore(arrivalTime) && newDeparture.isAfter(departureTime)) {
                return true;
            }
            if (newDeparture.equals(departureTime)) {
                return true;
            }
            if (newArrival.equals(newDeparture)) {
                return true;
            }
        } catch (Exception ex) {
            return false;
        }
        return false;
    }
}
