import java.util.List;
import java.util.ArrayList;
import java.util.Date;
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
    private int bookedSeats;
    private List<Booking> bookings;
    private List<Stop> stops;

    public Trip() {
        bookings = new ArrayList<>();
        stops = new ArrayList<>();
    }

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null || bookingTime == null) {
            return Math.round(price * 10.0) / 10.0;
        }

        MembershipPackage membershipPackage = customer.getMembershipPackage();
        if (membershipPackage == null || !membershipPackage.hasAward(Award.DISCOUNTS)) {
            return Math.round(price * 10.0) / 10.0;
        }

        if (!isBookingAtLeast24HoursBefore(bookingTime)) {
            return Math.round(price * 10.0) / 10.0;
        }

        double discounted = price * 0.8;
        return Math.round(discounted * 10.0) / 10.0;
    }

    private boolean isBookingAtLeast24HoursBefore(String bookingTime) {
        if (departureDate == null || departureTime == null) {
            return false;
        }
        try {
            java.time.LocalDate date = java.time.LocalDate.parse(
                new java.text.SimpleDateFormat("yyyy-MM-dd").format(departureDate));
            java.time.LocalTime depTime = java.time.LocalTime.parse(departureTime);
            java.time.LocalDateTime departure = java.time.LocalDateTime.of(date, depTime);

            java.time.LocalTime bktTime = java.time.LocalTime.parse(bookingTime);
            java.time.LocalDate bktDate = departure.toLocalDate();
            java.time.LocalDateTime booking = java.time.LocalDateTime.of(bktDate, bktTime);

            long diffHours = java.time.Duration.between(booking, departure).toHours();
            return diffHours >= 24;
        } catch (Exception e) {
            return false;
        }
    }

    public int getBookedSeats() {
        return bookedSeats;
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

        MembershipPackage membershipPackage = customer.getMembershipPackage();
        if (membershipPackage == null || !membershipPackage.hasAward(Award.POINTS)) {
            return 0;
        }

        int points = 0;
        for (Booking booking : bookings) {
            if (booking != null && booking.isInMonth(currentMonth)) {
                points += booking.getNumberOfSeats() * 5;
            }
        }
        return points;
    }

    public Set<String> getStopStations() {
        Set<String> stations = new HashSet<>();
        if (stops == null) return stations;
        for (Stop stop : stops) {
            if (stop != null && stop.getStopStation() != null) {
                stations.add(stop.getStopStation());
            }
        }
        return stations;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (departureTime == null || arrivalTime == null ||
            newDepartureTime == null || newArrivalTime == null) {
            return false;
        }

        String thisStart = departureTime;
        String thisEnd = arrivalTime;
        String newStart = newDepartureTime;
        String newEnd = newArrivalTime;

        return thisStart.compareTo(newEnd) < 0 && newStart.compareTo(thisEnd) < 0;
    }

    public boolean isSeatAvailable(int requestedSeats) {
        if (numberOfSeats <= 0) return false;
        return requestedSeats <= (numberOfSeats - bookedSeats);
    }

    public void reduceBookedSeats(int seats) {
        bookedSeats += seats;
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
