import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.Instant;

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

    private LocalDate getDateOnly(Date date) {
        if (date == null) return null;
        Instant instant = date.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null || bookingTime == null) {
            return price;
        }
        MembershipPackage membership = customer.getMembershipPackage();
        if (membership == null) {
            return price;
        }
        if (!membership.hasAward(Award.DISCOUNTS)) {
            return price;
        }
        try {
            LocalDateTime bookingDateTime = LocalDateTime.parse(bookingTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            Date depDate = getDepartureDate();
            if (depDate == null) {
                return price;
            }
            LocalDate date = getDateOnly(depDate);
            LocalTime departure = LocalTime.parse(getDepartureTime());
            LocalDateTime tripDateTime = date.atTime(departure);
            long hoursDiff = java.time.Duration.between(bookingDateTime, tripDateTime).toHours();
            if (hoursDiff >= 24) {
                return Math.round((price * 0.8) * 10.0) / 10.0;
            }
        } catch (DateTimeParseException e) {
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
        if (booking != null) {
            this.bookings.add(booking);
        }
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || currentMonth == null || currentMonth.isEmpty()) {
            return 0;
        }
        MembershipPackage membership = customer.getMembershipPackage();
        if (membership == null) {
            return 0;
        }
        if (!membership.hasAward(Award.POINTS)) {
            return 0;
        }
        int totalPoints = 0;
        if (bookings != null) {
            for (Booking booking : bookings) {
                if (booking != null) {
                    Date bookingDate = booking.getBookingDate();
                    if (bookingDate != null) {
                        LocalDate bd = getDateOnly(bookingDate);
                        String bookingMonth = DateTimeFormatter.ofPattern("yyyy-MM").format(bd);
                        if (bookingMonth.equals(currentMonth)) {
                            int seats = booking.getNumberOfSeats();
                            if (seats > 0) {
                                totalPoints += seats * 5;
                            }
                        }
                    }
                }
            }
        }
        return totalPoints;
    }

    public Set<String> getStopStations() {
        Set<String> stations = new HashSet<>();
        if (stops != null) {
            for (Stop stop : stops) {
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
        if (newDepartureTime == null || newArrivalTime == null) {
            return false;
        }
        try {
            LocalTime newDep = LocalTime.parse(newDepartureTime);
            LocalTime newArr = LocalTime.parse(newArrivalTime);
            if (!newDep.isBefore(newArr)) {
                return false;
            }
            String myDepTime = getDepartureTime();
            String myArrTime = getArrivalTime();
            if (myDepTime == null || myArrTime == null) {
                return false;
            }
            LocalTime myDep = LocalTime.parse(myDepTime);
            LocalTime myArr = LocalTime.parse(myArrTime);
            if (newDep.isAfter(myArr) || myDep.isAfter(newArr)) {
                return false;
            }
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
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
        if (stop != null) {
            this.stops.add(stop);
        }
    }
}
