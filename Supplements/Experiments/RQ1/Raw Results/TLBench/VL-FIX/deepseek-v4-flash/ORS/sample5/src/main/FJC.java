import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Date;

enum Award {
    CASHBACK,
    DISCOUNTS,
    POINTS
}

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
    private List<Trip> trips = new ArrayList<>();

    public Driver() {
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void addTrip(Trip trip) {
        trips.add(trip);
    }

    public boolean checkStopOverlap(Trip trip1, Trip trip2) {
        if (trip1 == null || trip2 == null) {
            return false;
        }
        Set<String> stops1 = trip1.getStopStations();
        Set<String> stops2 = trip2.getStopStations();
        if (stops1.isEmpty() || stops2.isEmpty()) {
            return false;
        }
        for (String stop : stops1) {
            if (stops2.contains(stop)) {
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
        if (depTime == null || arrTime == null || depTime.isEmpty() || arrTime.isEmpty()) {
            return false;
        }
        if (!depTime.matches("\\d{2}:\\d{2}") || !arrTime.matches("\\d{2}:\\d{2}")) {
            return false;
        }
        if (depTime.compareTo(arrTime) >= 0) {
            return false;
        }
        for (Trip existingTrip : trips) {
            if (existingTrip == null) {
                continue;
            }
            if (newTrip.getDepartureDate() != null && existingTrip.getDepartureDate() != null) {
                if (!newTrip.getDepartureDate().equals(existingTrip.getDepartureDate())) {
                    continue;
                }
            } else if (newTrip.getDepartureDate() == null && existingTrip.getDepartureDate() == null) {
                // both null, treat as same day? assume not overlapping
                continue;
            } else {
                // one null, one not - not overlapping
                continue;
            }
            if (newTrip.isTimeConflicting(existingTrip.getDepartureTime(), existingTrip.getArrivalTime())) {
                return false;
            }
        }
        return true;
    }
}

class Customer extends User {
    private MembershipPackage membershipPackage;
    private List<Booking> bookings = new ArrayList<>();

    public Customer() {
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
        bookings.add(booking);
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
            trip.addBooking(booking);
            this.addBooking(booking);
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (currentMonth == null || currentMonth.isEmpty()) {
            return 0;
        }
        if (membershipPackage == null) {
            return 0;
        }
        if (!membershipPackage.hasAward(Award.POINTS)) {
            return 0;
        }
        int totalPoints = 0;
        for (Booking booking : bookings) {
            if (booking == null) {
                continue;
            }
            if (booking.getTrip() == null) {
                continue;
            }
            if (booking.isInMonth(currentMonth)) {
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
    private List<Booking> bookings = new ArrayList<>();
    private List<Stop> stops = new ArrayList<>();

    public Trip() {
    }

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null || bookingTime == null || bookingTime.isEmpty()) {
            return this.price;
        }
        if (!bookingTime.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}")) {
            return this.price;
        }
        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null) {
            return this.price;
        }
        if (!pkg.hasAward(Award.DISCOUNTS)) {
            return this.price;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date bookDate = sdf.parse(bookingTime);
            Date depDate = null;
            if (departureDate != null && departureTime != null) {
                String depStr = new SimpleDateFormat("yyyy-MM-dd").format(departureDate) + " " + departureTime;
                depDate = sdf.parse(depStr);
            }
            if (depDate == null) {
                return this.price;
            }
            long diffMillis = depDate.getTime() - bookDate.getTime();
            long diffHours = diffMillis / (1000 * 60 * 60);
            if (diffHours >= 24) {
                double discounted = this.price * 0.8;
                return Math.round(discounted * 10) / 10.0;
            }
        } catch (Exception e) {
            return this.price;
        }
        return this.price;
    }

    public int getBookedSeats() {
        int sum = 0;
        for (Booking b : bookings) {
            if (b != null) {
                sum += b.getNumberOfSeats();
            }
        }
        return sum;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || currentMonth == null || currentMonth.isEmpty()) {
            return 0;
        }
        return customer.computeMonthlyRewardPoints(currentMonth);
    }

    public Set<String> getStopStations() {
        Set<String> stationSet = new HashSet<>();
        for (Stop stop : stops) {
            if (stop != null && stop.getStopStation() != null) {
                stationSet.add(stop.getStopStation());
            }
        }
        return stationSet;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (newDepartureTime == null || newArrivalTime == null || departureTime == null || arrivalTime == null) {
            return false;
        }
        // Check if intervals overlap: [departureTime, arrivalTime) and [newDepartureTime, newArrivalTime)
        // Allow adjacent boundaries (one ends exactly when another starts)
        boolean overlap = departureTime.compareTo(newArrivalTime) < 0 && newDepartureTime.compareTo(arrivalTime) < 0;
        return overlap;
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
        stops.add(stop);
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
        int availableSeats = trip.getNumberOfSeats() - trip.getBookedSeats();
        if (numberOfSeats > availableSeats) {
            return false;
        }
        // Check overlapping bookings on same day
        for (Booking existingBooking : customer.getBookings()) {
            if (existingBooking == null || existingBooking.getTrip() == null) {
                continue;
            }
            if (existingBooking.overlapsWith(trip)) {
                return false;
            }
        }
        // Check booking time is more than 2 hours before departure
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String tripDepStr = new SimpleDateFormat("yyyy-MM-dd").format(trip.getDepartureDate()) + " " + trip.getDepartureTime();
            Date tripDepDate = sdf.parse(tripDepStr);
            long diffMillis = tripDepDate.getTime() - bookingDate.getTime();
            long diffHours = diffMillis / (1000 * 60 * 60);
            if (diffHours <= 2) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void updateTripSeats() {
        if (trip != null) {
            int newSeats = trip.getNumberOfSeats() - numberOfSeats;
            trip.setNumberOfSeats(newSeats);
        }
    }

    public boolean overlapsWith(Trip otherTrip) {
        if (otherTrip == null || this.trip == null) {
            return false;
        }
        if (this.trip.getDepartureDate() == null || otherTrip.getDepartureDate() == null) {
            return false;
        }
        if (!this.trip.getDepartureDate().equals(otherTrip.getDepartureDate())) {
            return false;
        }
        String thisDep = this.trip.getDepartureTime();
        String thisArr = this.trip.getArrivalTime();
        String otherDep = otherTrip.getDepartureTime();
        String otherArr = otherTrip.getArrivalTime();
        if (thisDep == null || thisArr == null || otherDep == null || otherArr == null) {
            return false;
        }
        // Check overlapping intervals, allow adjacent (one ends exactly when another starts)
        boolean overlap = thisDep.compareTo(otherArr) < 0 && otherDep.compareTo(thisArr) < 0;
        return overlap;
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null || month.isEmpty()) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String bookingMonth = sdf.format(bookingDate);
        return bookingMonth.equals(month);
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
        if (awards == null) {
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