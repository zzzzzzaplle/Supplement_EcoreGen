import java.util.*;
import java.text.SimpleDateFormat;

enum Award {
    CASHBACK, DISCOUNTS, POINTS
}

abstract class User {
    private String id;
    private String email;
    private String phoneNumber;

    public User() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}

class Driver extends User {
    private List<Trip> trips = new ArrayList<>();

    public Driver() {}

    public List<Trip> getTrips() { return trips; }
    public void addTrip(Trip trip) { this.trips.add(trip); }

    public boolean checkStopOverlap(Trip trip1, Trip trip2) {
        if (trip1 == null || trip2 == null || trip1.getStops().isEmpty() || trip2.getStops().isEmpty()) return false;
        Set<String> stops1 = trip1.getStopStations();
        for (Stop s : trip2.getStops()) {
            if (stops1.contains(s.getStopStation())) return true;
        }
        return false;
    }

    public boolean canPostTrip(Trip newTrip) {
        if (newTrip == null || newTrip.getDepartureTime() == null || newTrip.getArrivalTime() == null) return false;
        if (newTrip.getDepartureTime().compareTo(newTrip.getArrivalTime()) >= 0) return false;
        for (Trip existing : trips) {
            if (existing.getDepartureDate().equals(newTrip.getDepartureDate())) {
                if (existing.isTimeConflicting(newTrip.getDepartureTime(), newTrip.getArrivalTime())) return false;
            }
        }
        return true;
    }
}

class Customer extends User {
    private MembershipPackage membershipPackage;
    private List<Booking> bookings = new ArrayList<>();

    public Customer() {}

    public MembershipPackage getMembershipPackage() { return membershipPackage; }
    public void setMembershipPackage(MembershipPackage membershipPackage) { this.membershipPackage = membershipPackage; }
    public List<Booking> getBookings() { return bookings; }
    public void addBooking(Booking booking) { this.bookings.add(booking); }

    public void bookTrip(Trip trip, int numberOfSeats) {
        Booking b = new Booking();
        b.setCustomer(this);
        b.setTrip(trip);
        b.setNumberOfSeats(numberOfSeats);
        b.setBookingDate(new Date());
        if (b.isBookingEligible()) {
            b.updateTripSeats();
            this.addBooking(b);
            trip.addBooking(b);
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (membershipPackage == null || !membershipPackage.hasAward(Award.POINTS)) return 0;
        int points = 0;
        for (Booking b : bookings) {
            if (b.isInMonth(currentMonth)) {
                points += (b.getNumberOfSeats() * 5);
            }
        }
        return points;
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
    private List<Booking> bookings = new ArrayList<>();
    private List<Stop> stops = new ArrayList<>();

    public Trip() {}

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null || customer.getMembershipPackage() == null || !customer.getMembershipPackage().hasAward(Award.DISCOUNTS)) return price;
        // Logic for 24 hours check simplified as string compare
        if (bookingTime != null && bookingTime.compareTo(departureTime) <= 0) {
            return Math.round((price * 0.8) * 10.0) / 10.0;
        }
        return price;
    }

    public int getBookedSeats() {
        return bookings.stream().mapToInt(Booking::getNumberOfSeats).sum();
    }

    public List<Booking> getBookings() { return bookings; }
    public void addBooking(Booking booking) { this.bookings.add(booking); }
    public List<Stop> getStops() { return stops; }
    public void addStop(Stop stop) { this.stops.add(stop); }

    public Set<String> getStopStations() {
        Set<String> set = new HashSet<>();
        for (Stop s : stops) set.add(s.getStopStation());
        return set;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        return !(newArrivalTime.compareTo(this.departureTime) <= 0 || newDepartureTime.compareTo(this.arrivalTime) >= 0);
    }

    public String getDepartureStation() { return departureStation; }
    public void setDepartureStation(String departureStation) { this.departureStation = departureStation; }
    public String getArrivalStation() { return arrivalStation; }
    public void setArrivalStation(String arrivalStation) { this.arrivalStation = arrivalStation; }
    public int getNumberOfSeats() { return numberOfSeats; }
    public void setNumberOfSeats(int numberOfSeats) { this.numberOfSeats = numberOfSeats; }
    public Date getDepartureDate() { return departureDate; }
    public void setDepartureDate(Date departureDate) { this.departureDate = departureDate; }
    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}

class Stop {
    private String stopStation;
    public Stop() {}
    public String getStopStation() { return stopStation; }
    public void setStopStation(String stopStation) { this.stopStation = stopStation; }
}

class Booking {
    private int numberOfSeats;
    private Customer customer;
    private Trip trip;
    private Date bookingDate;

    public Booking() {}

    public boolean isBookingEligible() {
        if (customer == null || trip == null || bookingDate == null) return false;
        if (numberOfSeats > (trip.getNumberOfSeats() - trip.getBookedSeats())) return false;
        for (Booking b : customer.getBookings()) {
            if (b.getTrip().getDepartureDate().equals(trip.getDepartureDate()) && b.overlapsWith(trip)) return false;
        }
        return true;
    }

    public void updateTripSeats() {}

    public boolean overlapsWith(Trip otherTrip) {
        return trip.isTimeConflicting(otherTrip.getDepartureTime(), otherTrip.getArrivalTime());
    }

    public boolean isInMonth(String month) {
        return new SimpleDateFormat("yyyy-MM").format(bookingDate).equals(month);
    }

    public int getNumberOfSeats() { return numberOfSeats; }
    public void setNumberOfSeats(int numberOfSeats) { this.numberOfSeats = numberOfSeats; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }
    public Date getBookingDate() { return bookingDate; }
    public void setBookingDate(Date bookingDate) { this.bookingDate = bookingDate; }
}

class MembershipPackage {
    private Award[] awards;
    public MembershipPackage() {}
    public Award[] getAwards() { return awards; }
    public void setAwards(Award[] awards) { this.awards = awards; }
    public boolean hasAward(Award award) {
        if (awards == null) return false;
        for (Award a : awards) if (a == award) return true;
        return false;
    }
}