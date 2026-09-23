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
            return this.price;
        }
        MembershipPackage mp = customer.getMembershipPackage();
        if (mp == null || !mp.hasAward(Award.DISCOUNTS)) {
            return this.price;
        }
        if (!isAtLeast24HoursBeforeDeparture(bookingTime)) {
            return this.price;
        }
        double discounted = this.price * 0.8;
        discounted = Math.round(discounted * 10) / 10.0;
        return discounted;
    }

    private boolean isAtLeast24HoursBeforeDeparture(String bookingTime) {
        if (bookingTime == null || this.departureTime == null) {
            return false;
        }
        String[] bookingParts = bookingTime.split(":");
        String[] departParts = this.departureTime.split(":");
        if (bookingParts.length != 2 || departParts.length != 2) {
            return false;
        }
        try {
            int bookingHour = Integer.parseInt(bookingParts[0]);
            int bookingMin = Integer.parseInt(bookingParts[1]);
            int departHour = Integer.parseInt(departParts[0]);
            int departMin = Integer.parseInt(departParts[1]);

            int bookingTotalMin = bookingHour * 60 + bookingMin;
            int departTotalMin = departHour * 60 + departMin;

            if (this.departureDate != null) {
                Date now = new Date();
                if (bookingDateIsSameOrAfterDepartureDate(this.departureDate, now)) {
                    return false;
                }
            }

            return (departTotalMin - bookingTotalMin) >= 24 * 60;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean bookingDateIsSameOrAfterDepartureDate(Date departDate, Date bookingDate) {
        return !bookingDate.before(departDate);
    }

    public int getBookedSeats() {
        int booked = 0;
        for (Booking b : bookings) {
            booked += b.getNumberOfSeats();
        }
        return booked;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void addBooking(Booking booking) {
        this.bookings.add(booking);
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || currentMonth == null) {
            return 0;
        }
        MembershipPackage mp = customer.getMembershipPackage();
        if (mp == null || !mp.hasAward(Award.POINTS)) {
            return 0;
        }
        int points = 0;
        for (Booking b : bookings) {
            if (b.getCustomer() == customer && b.isInMonth(currentMonth)) {
                points += b.getNumberOfSeats() * 5;
            }
        }
        return points;
    }

    public Set<String> getStopStations() {
        Set<String> stations = new HashSet<String>();
        for (Stop s : stops) {
            stations.add(s.getStopStation());
        }
        return stations;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (this.departureTime == null || this.arrivalTime == null || newDepartureTime == null || newArrivalTime == null) {
            return false;
        }
        String[] existDepartParts = this.departureTime.split(":");
        String[] existArriveParts = this.arrivalTime.split(":");
        String[] newDepartParts = newDepartureTime.split(":");
        String[] newArriveParts = newArrivalTime.split(":");

        if (existDepartParts.length != 2 || existArriveParts.length != 2 || newDepartParts.length != 2 || newArriveParts.length != 2) {
            return false;
        }

        try {
            int existDepart = Integer.parseInt(existDepartParts[0]) * 60 + Integer.parseInt(existDepartParts[1]);
            int existArrive = Integer.parseInt(existArriveParts[0]) * 60 + Integer.parseInt(existArriveParts[1]);
            int newDepart = Integer.parseInt(newDepartParts[0]) * 60 + Integer.parseInt(newDepartParts[1]);
            int newArrive = Integer.parseInt(newArriveParts[0]) * 60 + Integer.parseInt(newArriveParts[1]);

            boolean noOverlap = newArrive <= existDepart || newDepart >= existArrive;
            return !noOverlap;
        } catch (NumberFormatException e) {
            return false;
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
        this.stops.add(stop);
    }
}
