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
        this.stops.add(stop);
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void addBooking(Booking booking) {
        this.bookings.add(booking);
    }

    public int getBookedSeats() {
        int bookedSeats = 0;
        for (Booking booking : bookings) {
            bookedSeats += booking.getNumberOfSeats();
        }
        return bookedSeats;
    }

    public Set<String> getStopStations() {
        Set<String> stations = new HashSet<String>();
        for (Stop stop : stops) {
            if (stop.getStopStation() != null) {
                stations.add(stop.getStopStation());
            }
        }
        return stations;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (departureTime == null || arrivalTime == null || newDepartureTime == null || newArrivalTime == null) {
            return false;
        }
        // Compare time ranges: they overlap if not (newEnd <= thisStart OR newStart >= thisEnd)
        // Adjacent boundaries (one ends exactly when other starts) are allowed, so use < not <=
        int thisStart = timeToMinutes(departureTime);
        int thisEnd = timeToMinutes(arrivalTime);
        int newStart = timeToMinutes(newDepartureTime);
        int newEnd = timeToMinutes(newArrivalTime);
        
        // No overlap if newEnd <= thisStart (new ends before or exactly when this starts)
        // or newStart >= thisEnd (new starts after or exactly when this ends)
        return !(newEnd <= thisStart || newStart >= thisEnd);
    }

    public static int timeToMinutes(String time) {
        if (time == null || !time.contains(":")) {
            return 0;
        }
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null || bookingTime == null || departureTime == null || departureDate == null) {
            return price;
        }
        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null) {
            return price;
        }
        if (!pkg.hasAward(Award.DISCOUNTS)) {
            return price;
        }
        // Check if booking is made at least 24 hours before departure
        // We compare bookingTime with departureTime on the same day logic
        // Actually, we need departure date + time vs booking date + time
        // The requirement says: booking is made at least 24 hours before departure
        // For simplicity, we'll compare the bookingTime string to departureTime string
        // assuming same day or using dates.
        // Since we have Date objects, let's do a proper check:
        // The bookingTime is a string like "HH:mm", and we also need the booking date.
        // But we only have bookingTime string. Let's interpret it as time on the same day as departure.
        // Actually the requirement: "the booking is made at least 24 hours before departure"
        // We'll approximate by checking if departure date/time is at least 24h after booking time on same day
        // A simpler approach: compare bookingTime string to departureTime string and also the dates.
        // Since we don't have the booking date here, we'll assume it's passed via bookingTime as "yyyy-MM-dd HH:mm"
        // Actually looking at the method signature: calculateDiscountedPrice(Customer customer, String bookingTime)
        // The bookingTime likely represents the time when booking is made, e.g., "2024-01-15 10:00"
        // We'll parse it.
        try {
            String[] parts = bookingTime.split(" ");
            String datePart = parts[0];
            String timePart = parts[1];
            String[] dateParts = datePart.split("-");
            int year = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]);
            int day = Integer.parseInt(dateParts[2]);
            
            java.util.Calendar bookingCal = java.util.Calendar.getInstance();
            bookingCal.set(year, month - 1, day, Integer.parseInt(timePart.split(":")[0]), Integer.parseInt(timePart.split(":")[1]));
            
            java.util.Calendar depCal = java.util.Calendar.getInstance();
            depCal.setTime(departureDate);
            String[] depTimeParts = departureTime.split(":");
            depCal.set(java.util.Calendar.HOUR_OF_DAY, Integer.parseInt(depTimeParts[0]));
            depCal.set(java.util.Calendar.MINUTE, Integer.parseInt(depTimeParts[1]));
            
            long diffMs = depCal.getTimeInMillis() - bookingCal.getTimeInMillis();
            long diffHours = diffMs / (1000 * 60 * 60);
            
            if (diffHours >= 24) {
                double discountedPrice = price * 0.8;
                return Math.round(discountedPrice * 10) / 10.0;
            }
        } catch (Exception e) {
            return price;
        }
        return price;
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || currentMonth == null) {
            return 0;
        }
        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null || !pkg.hasAward(Award.POINTS)) {
            return 0;
        }
        int totalPoints = 0;
        List<Booking> customerBookings = customer.getBookings();
        if (customerBookings == null) {
            return 0;
        }
        for (Booking booking : customerBookings) {
            if (booking != null && booking.getTrip() == this && booking.isInMonth(currentMonth)) {
                totalPoints += booking.getNumberOfSeats() * 5;
            }
        }
        return totalPoints;
    }
}
