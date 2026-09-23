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
        if (stops1 == null || stops2 == null || stops1.isEmpty() || stops2.isEmpty()) {
            return false;
        }
        for (String s : stops1) {
            if (stops2.contains(s)) {
                return true;
            }
        }
        return false;
    }

    public boolean canPostTrip(Trip newTrip) {
        if (newTrip == null) {
            return false;
        }
        if (newTrip.getDepartureTime() == null || newTrip.getArrivalTime() == null) {
            return false;
        }
        if (newTrip.getDepartureTime().compareTo(newTrip.getArrivalTime()) >= 0) {
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
        Booking booking = new Booking();
        booking.setCustomer(this);
        booking.setTrip(trip);
        booking.setNumberOfSeats(numberOfSeats);
        booking.setBookingDate(new Date());
        if (booking.isBookingEligible()) {
            booking.updateTripSeats();
            this.addBooking(booking);
            if (trip != null) {
                trip.addBooking(booking);
            }
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (this.membershipPackage == null) {
            return 0;
        }
        if (!this.membershipPackage.hasAward(Award.POINTS)) {
            return 0;
        }
        int totalPoints = 0;
        for (Booking booking : bookings) {
            if (booking != null && booking.isInMonth(currentMonth)) {
                totalPoints += booking.getNumberOfSeats() * 5;
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
        if (customer == null || customer.getMembershipPackage() == null || bookingTime == null) {
            return price;
        }
        if (!customer.getMembershipPackage().hasAward(Award.DISCOUNTS)) {
            return price;
        }
        if (this.departureTime == null) {
            return price;
        }
        if (bookingTime.compareTo(this.departureTime) >= 0) {
            return price;
        }
        // Check at least 24 hours before departure
        long bookingMinutes = parseTimeToMinutes(bookingTime);
        long departureMinutes = parseTimeToMinutes(this.departureTime);
        if (bookingMinutes < 0 || departureMinutes < 0) {
            return price;
        }
        // Simple time check: if departure is on the same day, ensure booking time is 24h earlier
        // Since times are strings like "HH:MM", we compare them as strings after considering a full day difference
        if ((departureMinutes - bookingMinutes) < 24 * 60 && (departureMinutes - bookingMinutes) >= 0) {
            // same day, less than 24h, but if departure is next day, we need more info
            // Simplified: require 24*60 minutes difference
            return price;
        }
        double discounted = price * 0.8;
        return Math.round(discounted * 10.0) / 10.0;
    }

    private long parseTimeToMinutes(String time) {
        try {
            String[] parts = time.split(":");
            return Long.parseLong(parts[0]) * 60 + Long.parseLong(parts[1]);
        } catch (Exception e) {
            return -1;
        }
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
        if (customer == null) {
            return 0;
        }
        if (customer.getMembershipPackage() == null) {
            return 0;
        }
        if (!customer.getMembershipPackage().hasAward(Award.POINTS)) {
            return 0;
        }
        int total = 0;
        for (Booking b : bookings) {
            if (b != null && b.getCustomer() != null && b.getCustomer().equals(customer) && b.isInMonth(currentMonth)) {
                total += b.getNumberOfSeats() * 5;
            }
        }
        return total;
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
        if (newDepartureTime == null || newArrivalTime == null || this.departureTime == null || this.arrivalTime == null) {
            return false;
        }
        // Adjacent boundaries are allowed: one ends exactly when another starts is NOT a conflict
        // Conflict: newStart < existingEnd AND newEnd > existingStart
        return newDepartureTime.compareTo(this.arrivalTime) < 0 && newArrivalTime.compareTo(this.departureTime) > 0;
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
        if (this.customer == null || this.trip == null || this.bookingDate == null) {
            return false;
        }
        if (this.numberOfSeats <= 0) {
            return false;
        }
        int remaining = this.trip.getNumberOfSeats() - this.trip.getBookedSeats();
        if (this.numberOfSeats > remaining) {
            return false;
        }
        // Check time: booking must be strictly more than 2 hours before departure
        if (this.trip.getDepartureDate() == null) {
            return false;
        }
        long tripDepartureMillis = this.trip.getDepartureDate().getTime();
        long bookingMillis = this.bookingDate.getTime();
        long twoHoursMillis = 2L * 60L * 60L * 1000L;
        if (tripDepartureMillis - bookingMillis <= twoHoursMillis) {
            return false;
        }
        // Check no overlapping booking on the same day
        for (Booking existing : this.customer.getBookings()) {
            if (existing != null && existing != this && existing.overlapsWith(this.trip)) {
                return false;
            }
        }
        return true;
    }

    public void updateTripSeats() {
        if (this.trip != null) {
            this.trip.setNumberOfSeats(this.trip.getNumberOfSeats() - this.numberOfSeats);
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
        if (otherTrip == null || this.trip == null) {
            return false;
        }
        if (this.trip.getDepartureDate() == null || otherTrip.getDepartureDate() == null) {
            return false;
        }
        // Same day check
        if (!isSameDay(this.trip.getDepartureDate(), otherTrip.getDepartureDate())) {
            return false;
        }
        // Time overlap: existing departure < otherArrival AND existingArrival > otherDeparture
        String existingDep = this.trip.getDepartureTime();
        String existingArr = this.trip.getArrivalTime();
        String otherDep = otherTrip.getDepartureTime();
        String otherArr = otherTrip.getArrivalTime();
        if (existingDep == null || existingArr == null || otherDep == null || otherArr == null) {
            return false;
        }
        return existingDep.compareTo(otherArr) < 0 && existingArr.compareTo(otherDep) > 0;
    }

    private boolean isSameDay(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            return false;
        }
        return d1.getYear() == d2.getYear() && d1.getMonth() == d2.getMonth() && d1.getDate() == d2.getDate();
    }

    public boolean isInMonth(String month) {
        if (this.bookingDate == null || month == null) {
            return false;
        }
        // month format: "YYYY-MM"
        try {
            String[] parts = month.split("-");
            int year = Integer.parseInt(parts[0]);
            int mon = Integer.parseInt(parts[1]);
            int bookingYear = this.bookingDate.getYear() + 1900;
            int bookingMonth = this.bookingDate.getMonth() + 1;
            return year == bookingYear && mon == bookingMonth;
        } catch (Exception e) {
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