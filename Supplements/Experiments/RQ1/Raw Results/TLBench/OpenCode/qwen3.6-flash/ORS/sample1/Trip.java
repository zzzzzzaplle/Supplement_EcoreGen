import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
        if (customer == null || customer.getMembershipPackage() == null) {
            return this.price;
        }
        MembershipPackage pkg = customer.getMembershipPackage();
        if (!pkg.hasAward(Award.DISCOUNTS)) {
            return this.price;
        }
        if (bookingTime == null) {
            return this.price;
        }
        try {
            LocalDateTime bookingDateTime = parseDateTime(bookingTime);
            LocalDateTime departureDateTime = parseDepartureLocalDateTime();
            if (bookingDateTime == null || departureDateTime == null) {
                return this.price;
            }
            if (!bookingDateTime.isBefore(departureDateTime.minusHours(24))) {
                return this.price;
            }
            double discountedPrice = this.price * 0.8;
            return Math.round(discountedPrice * 10.0) / 10.0;
        } catch (Exception e) {
            return this.price;
        }
    }

    public int getBookedSeats() {
        int count = 0;
        for (Booking b : this.bookings) {
            count += b.getNumberOfSeats();
        }
        return count;
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
        if (customer == null || currentMonth == null) {
            return 0;
        }
        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null || !pkg.hasAward(Award.POINTS)) {
            return 0;
        }
        int totalSeats = 0;
        for (Booking b : this.bookings) {
            if (b.getCustomer() != null && b.getCustomer().equals(customer) && b.isInMonth(currentMonth)) {
                totalSeats += b.getNumberOfSeats();
            }
        }
        return totalSeats * 5;
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
        if (newDepartureTime == null || newArrivalTime == null) {
            return false;
        }
        String ownDep = this.departureTime;
        String ownArr = this.arrivalTime;
        return newDepartureTime.compareTo(ownArr) < 0 && newArrivalTime.compareTo(ownDep) > 0;
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            LocalDateTime dt = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            return dt;
        } catch (DateTimeParseException e1) {
            try {
                LocalDateTime dt = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
                return dt;
            } catch (DateTimeParseException e2) {
                try {
                    LocalDateTime timeOnly = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
                    LocalDateTime depDateTime = parseDepartureLocalDateTime();
                    return depDateTime.toLocalDate().atTime(timeOnly.toLocalTime());
                } catch (DateTimeParseException e3) {
                    return null;
                }
            }
        }
    }

    private LocalDateTime parseDepartureLocalDateTime() {
        try {
            LocalDateTime depDatePart = this.departureDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            String[] parts = this.departureTime.split(":");
            return depDatePart.withHour(Integer.parseInt(parts[0])).withMinute(Integer.parseInt(parts[1]));
        } catch (Exception e) {
            return null;
        }
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
