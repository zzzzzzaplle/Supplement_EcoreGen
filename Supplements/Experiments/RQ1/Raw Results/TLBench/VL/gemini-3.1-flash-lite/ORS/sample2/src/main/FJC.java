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
        Set<String> s1 = trip1.getStopStations();
        for (Stop s : trip2.getStops()) {
            if (s1.contains(s.getStopStation())) return true;
        }
        return false;
    }

    public boolean canPostTrip(Trip newTrip) {
        if (newTrip == null || newTrip.getDepartureTime() == null || newTrip.getArrivalTime() == null) return false;
        if (newTrip.getDepartureTime().compareTo(newTrip.getArrivalTime()) >= 0) return false;
        for (Trip t : trips) {
            if (newTrip.getDepartureDate().equals(t.getDepartureDate())) {
                if (!(newTrip.getArrivalTime().compareTo(t.getDepartureTime()) <= 0 || newTrip.getDepartureTime().compareTo(t.getArrivalTime()) >= 0)) {
                    return false;
                }
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
                points += b.getNumberOfSeats() * 5;
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
        if (customer != null && customer.getMembershipPackage() != null && customer.getMembershipPackage().hasAward(Award.DISCOUNTS)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date bTime = sdf.parse(bookingTime);
                Date dTime = sdf.parse(new SimpleDateFormat("yyyy-MM-dd").format(departureDate) + " " + departureTime);
                long diff = dTime.getTime() - bTime.getTime();
                if (diff >= 24 * 60 * 60 * 1000) {
                    return Math.round(price * 0.8 * 10.0) / 10.0;
                }
            } catch (Exception e) {}
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

    public String getDepartureStation() { return departureStation; }
    public void setDepartureStation(String ds) { this.departureStation = ds; }
    public String getArrivalStation() { return arrivalStation; }
    public void setArrivalStation(String as) { this.arrivalStation = as; }
    public int getNumberOfSeats() { return numberOfSeats; }
    public void setNumberOfSeats(int n) { this.numberOfSeats = n; }
    public Date getDepartureDate() { return departureDate; }
    public void setDepartureDate(Date d) { this.departureDate = d; }
    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String dt) { this.departureTime = dt; }
    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String at) { this.arrivalTime = at; }
    public double getPrice() { return price; }
    public void setPrice(double p) { this.price = p; }
}

class Stop {
    private String stopStation;
    public Stop() {}
    public String getStopStation() { return stopStation; }
    public void setStopStation(String s) { this.stopStation = s; }
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
            if (b.getBookingDate().equals(this.bookingDate) && b.overlapsWith(this.trip)) return false;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date dep = sdf.parse(new SimpleDateFormat("yyyy-MM-dd").format(trip.getDepartureDate()) + " " + trip.getDepartureTime());
            return (dep.getTime() - bookingDate.getTime()) > 2 * 60 * 60 * 1000;
        } catch (Exception e) { return false; }
    }

    public void updateTripSeats() {}

    public boolean overlapsWith(Trip t) {
        return this.trip.getDepartureDate().equals(t.getDepartureDate()) &&
               !(this.trip.getArrivalTime().compareTo(t.getDepartureTime()) <= 0 ||
                 this.trip.getDepartureTime().compareTo(t.getArrivalTime()) >= 0);
    }

    public boolean isInMonth(String month) {
        return new SimpleDateFormat("yyyy-MM").format(bookingDate).equals(month);
    }

    public int getNumberOfSeats() { return numberOfSeats; }
    public void setNumberOfSeats(int n) { this.numberOfSeats = n; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer c) { this.customer = c; }
    public Trip getTrip() { return trip; }
    public void setTrip(Trip t) { this.trip = t; }
    public Date getBookingDate() { return bookingDate; }
    public void setBookingDate(Date d) { this.bookingDate = d; }
}

class MembershipPackage {
    private Award[] awards;
    public MembershipPackage() {}
    public Award[] getAwards() { return awards; }
    public void setAwards(Award[] a) { this.awards = a; }
    public boolean hasAward(Award a) {
        if (awards == null) return false;
        for (Award award : awards) if (award == a) return true;
        return false;
    }
}