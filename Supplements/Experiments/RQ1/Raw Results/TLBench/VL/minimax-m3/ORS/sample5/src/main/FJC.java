import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

abstract class User {
    private String id;
    private String email;
    private String phoneNumber;

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

class Driver extends User {
    private List<Trip> trips;

    public Driver() {
        this.trips = new ArrayList<>();
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void addTrip(Trip trip) {
        if (trip != null) {
            this.trips.add(trip);
        }
    }

    public boolean checkStopOverlap(Trip trip1, Trip trip2) {
        if (trip1 == null || trip2 == null) {
            return false;
        }
        Set<String> stops1 = trip1.getStopStations();
        Set<String> stops2 = trip2.getStopStations();
        if (stops1 == null || stops1.isEmpty() || stops2 == null || stops2.isEmpty()) {
            return false;
        }
        for (String s1 : stops1) {
            if (stops2.contains(s1)) {
                return true;
            }
        }
        return false;
    }

    public boolean canPostTrip(Trip newTrip) {
        if (newTrip == null) {
            return false;
        }
        String depTime = newTrip.getDepartureTime();
        String arrTime = newTrip.getArrivalTime();
        if (depTime == null || arrTime == null) {
            return false;
        }
        if (depTime.compareTo(arrTime) >= 0) {
            return false;
        }
        for (Trip existing : trips) {
            if (existing == null) {
                continue;
            }
            if (newTrip.isTimeConflicting(existing.getDepartureTime(), existing.getArrivalTime())) {
                return false;
            }
        }
        return true;
    }
}

class Customer extends User {
    private MembershipPackage membershipPackage;
    private List<Booking> bookings;

    public Customer() {
        this.bookings = new ArrayList<>();
    }

    public MembershipPackage getMembershipPackage() {
        return membershipPackage;
    }

    public void setMembershipPackage(MembershipPackage membershipPackage) {
        this.membershipPackage = membershipPackage;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void addBooking(Booking booking) {
        if (booking != null) {
            this.bookings.add(booking);
        }
    }

    public void bookTrip(Trip trip, int numberOfSeats) {
        if (trip == null || numberOfSeats <= 0) {
            return;
        }
        Booking booking = new Booking();
        booking.setCustomer(this);
        booking.setTrip(trip);
        booking.setNumberOfSeats(numberOfSeats);
        booking.setBookingDate(new Date());
        if (booking.isBookingEligible()) {
            booking.updateTripSeats();
            this.addBooking(booking);
            trip.addBooking(booking);
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (membershipPackage == null || !membershipPackage.hasAward(Award.POINTS)) {
            return 0;
        }
        if (currentMonth == null) {
            return 0;
        }
        int totalPoints = 0;
        for (Booking b : bookings) {
            if (b != null && b.isInMonth(currentMonth)) {
                totalPoints += b.getNumberOfSeats() * 5;
            }
        }
        return totalPoints;
    }
}

class Trip {
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
        if (customer == null || customer.getMembershipPackage() == null
                || bookingTime == null || this.departureTime == null) {
            return price;
        }
        if (!customer.getMembershipPackage().hasAward(Award.DISCOUNTS)) {
            return price;
        }
        // Check bookingTime is at least 24 hours before departureTime (string compare on HH:mm)
        if (bookingTime.compareTo(this.departureTime) >= 0) {
            return price;
        }
        double discounted = price * 0.80;
        return Math.round(discounted * 10.0) / 10.0;
    }

    public int getBookedSeats() {
        int total = 0;
        for (Booking b : bookings) {
            if (b != null) {
                total += b.getNumberOfSeats();
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
        if (customer == null || currentMonth == null) {
            return 0;
        }
        int totalPoints = 0;
        for (Booking b : bookings) {
            if (b != null && b.getCustomer() != null && b.getCustomer().equals(customer)
                    && b.isInMonth(currentMonth)) {
                totalPoints += b.getNumberOfSeats() * 5;
            }
        }
        return totalPoints;
    }

    public Set<String> getStopStations() {
        Set<String> stations = new HashSet<>();
        for (Stop s : stops) {
            if (s != null && s.getStopStation() != null) {
                stations.add(s.getStopStation());
            }
        }
        return stations;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (this.departureTime == null || this.arrivalTime == null
                || newDepartureTime == null || newArrivalTime == null) {
            return false;
        }
        // Adjacency allowed: existing starts when new ends, or new starts when existing ends
        if (this.arrivalTime.compareTo(newDepartureTime) <= 0
                || newArrivalTime.compareTo(this.departureTime) <= 0) {
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

class Stop {
    private String stopStation;

    public Stop() {
    }

    public String getStopStation() {
        return stopStation;
    }

    public void setStopStation(String stopStation) {
        this.stopStation = stopStation;
    }
}

class Booking {
    private int numberOfSeats;
    private Customer customer;
    private Trip trip;
    private Date bookingDate;

    public Booking() {
    }

    public boolean isBookingEligible() {
        if (customer == null || trip == null || bookingDate == null) {
            return false;
        }
        if (numberOfSeats <= 0) {
            return false;
        }
        int booked = trip.getBookedSeats();
        int remaining = trip.getNumberOfSeats() - booked;
        if (numberOfSeats > remaining) {
            return false;
        }
        for (Booking b : customer.getBookings()) {
            if (b != null && b != this && b.getTrip() != null && b.overlapsWith(b.getTrip())) {
                return false;
            }
        }
        // Check booking time is strictly more than two hours before departure
        String depTime = trip.getDepartureTime();
        if (depTime == null) {
            return false;
        }
        if (!isMoreThanTwoHoursBefore(depTime)) {
            return false;
        }
        return true;
    }

    private boolean isMoreThanTwoHoursBefore(String depTime) {
        // bookingDate is a Date; we treat its time-of-day as bookingTime, compared with depTime HH:mm
        if (bookingDate == null) {
            return false;
        }
        String[] parts = depTime.split(":");
        if (parts.length < 2) {
            return false;
        }
        try {
            int depHour = Integer.parseInt(parts[0]);
            int depMin = Integer.parseInt(parts[1]);
            int depTotal = depHour * 60 + depMin;
            int bookTotal = bookingDate.getHours() * 60 + bookingDate.getMinutes();
            int diff = depTotal - bookTotal;
            // strictly more than 120 minutes
            return diff > 120;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void updateTripSeats() {
        if (trip != null) {
            trip.setNumberOfSeats(trip.getNumberOfSeats() - numberOfSeats);
        }
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public boolean overlapsWith(Trip otherTrip) {
        if (trip == null || otherTrip == null) {
            return false;
        }
        if (trip.getDepartureDate() == null || otherTrip.getDepartureDate() == null) {
            return false;
        }
        // Same day check
        if (!isSameDay(trip.getDepartureDate(), otherTrip.getDepartureDate())) {
            return false;
        }
        String s1 = trip.getDepartureTime();
        String e1 = trip.getArrivalTime();
        String s2 = otherTrip.getDepartureTime();
        String e2 = otherTrip.getArrivalTime();
        if (s1 == null || e1 == null || s2 == null || e2 == null) {
            return false;
        }
        if (e1.compareTo(s2) <= 0 || e2.compareTo(s1) <= 0) {
            return false;
        }
        return true;
    }

    private boolean isSameDay(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            return false;
        }
        return d1.getYear() == d2.getYear()
                && d1.getMonth() == d2.getMonth()
                && d1.getDate() == d2.getDate();
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null) {
            return false;
        }
        // month format expected: yyyy-MM
        String[] parts = month.split("-");
        if (parts.length < 2) {
            return false;
        }
        try {
            int year = Integer.parseInt(parts[0]);
            int m = Integer.parseInt(parts[1]) - 1;
            return (bookingDate.getYear() + 1900) == year && bookingDate.getMonth() == m;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

class MembershipPackage {
    private Award[] awards;

    public MembershipPackage() {
    }

    public Award[] getAwards() {
        return awards;
    }

    public void setAwards(Award[] awards) {
        this.awards = awards;
    }

    public boolean hasAward(Award award) {
        if (awards == null || award == null) {
            return false;
        }
        for (Award a : awards) {
            if (a == award) {
                return true;
            }
        }
        return false;
    }
}

enum Award {
    CASHBACK,
    DISCOUNTS,
    POINTS
}