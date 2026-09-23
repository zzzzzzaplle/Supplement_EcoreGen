import java.util.*;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

abstract class User {
    private String id;
    private String email;
    private String phoneNumber;

    public User() {}

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

enum Award {
    CASHBACK,
    DISCOUNTS,
    POINTS
}

class MembershipPackage {
    private Award[] awards;

    public MembershipPackage() {}

    public Award[] getAwards() {
        return awards;
    }

    public void setAwards(Award[] awards) {
        this.awards = awards;
    }

    public boolean hasAward(Award award) {
        if (awards == null) return false;
        for (Award a : awards) {
            if (a == award) return true;
        }
        return false;
    }
}

class Stop {
    private String stopStation;

    public Stop() {}

    public String getStopStation() {
        return stopStation;
    }

    public void setStopStation(String stopStation) {
        this.stopStation = stopStation;
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
        int booked = 0;
        for (Booking b : bookings) {
            booked += b.getNumberOfSeats();
        }
        return booked;
    }

    public Set<String> getStopStations() {
        Set<String> stations = new HashSet<>();
        for (Stop s : stops) {
            stations.add(s.getStopStation());
        }
        return stations;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        // Parse times assuming HH:mm format
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date existingDeparture = sdf.parse(this.departureTime);
            Date existingArrival = sdf.parse(this.arrivalTime);
            Date newDeparture = sdf.parse(newDepartureTime);
            Date newArrival = sdf.parse(newArrivalTime);

            // Check for overlap: not adjacent (equal boundaries are allowed)
            // Overlap if newDep < existingArr && newArr > existingDep
            // Adjacent allowed: newArr == existingDep or newDep == existingArr
            if (newDeparture.before(existingArrival) && newArrival.after(existingDeparture)) {
                // But if exactly equal boundaries, it's allowed
                if (newArrival.equals(existingDeparture) || newDeparture.equals(existingArrival)) {
                    return false;
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null) return this.price;
        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null) return this.price;
        if (!pkg.hasAward(Award.DISCOUNTS)) return this.price;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date bookingDateTime = sdf.parse(bookingTime);
            Date departureDateTime = sdf.parse(new SimpleDateFormat("yyyy-MM-dd").format(this.departureDate) + " " + this.departureTime);
            long diffInMillies = departureDateTime.getTime() - bookingDateTime.getTime();
            long diffInHours = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            if (diffInHours >= 24) {
                double discounted = this.price * 0.8;
                return Math.round(discounted * 10) / 10.0;
            }
        } catch (Exception e) {
            return this.price;
        }
        return this.price;
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null) return 0;
        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null) return 0;
        if (!pkg.hasAward(Award.POINTS)) return 0;
        int totalPoints = 0;
        for (Booking b : bookings) {
            if (b.getCustomer() != null && b.getCustomer().equals(customer)) {
                if (b.isInMonth(currentMonth)) {
                    totalPoints += b.getNumberOfSeats() * 5;
                }
            }
        }
        return totalPoints;
    }
}

class Booking {
    private int numberOfSeats;
    private Customer customer;
    private Trip trip;
    private Date bookingDate;

    public Booking() {}

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

    public boolean isBookingEligible() {
        if (customer == null || trip == null || bookingDate == null) return false;
        // Trip must exist (non-null) - already checked
        // Enough available seats
        int remainingSeats = trip.getNumberOfSeats() - trip.getBookedSeats();
        if (numberOfSeats <= 0 || numberOfSeats > remainingSeats) return false;
        // No overlapping booking on same day
        for (Booking existing : customer.getBookings()) {
            if (existing.overlapsWith(this.trip)) {
                return false;
            }
        }
        // Booking made more than two hours before departure
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String departureDateTimeStr = new SimpleDateFormat("yyyy-MM-dd").format(trip.getDepartureDate()) + " " + trip.getDepartureTime();
            Date departureDateTime = sdf.parse(departureDateTimeStr);
            long diffInMillies = departureDateTime.getTime() - bookingDate.getTime();
            long diffInHours = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            if (diffInHours <= 2) return false; // strictly more than 2 hours
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void updateTripSeats() {
        // Reduce trip seat inventory - handled by booking acceptance
        // This method is called when booking is accepted
        // No direct seat field in trip to reduce, but we track via bookings list
    }

    public boolean overlapsWith(Trip otherTrip) {
        if (otherTrip == null || this.trip == null) return false;
        // Check if same day
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String thisDate = sdf.format(this.trip.getDepartureDate());
        String otherDate = sdf.format(otherTrip.getDepartureDate());
        if (!thisDate.equals(otherDate)) return false;
        // Check time overlap
        try {
            SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm");
            Date thisDep = timeSdf.parse(this.trip.getDepartureTime());
            Date thisArr = timeSdf.parse(this.trip.getArrivalTime());
            Date otherDep = timeSdf.parse(otherTrip.getDepartureTime());
            Date otherArr = timeSdf.parse(otherTrip.getArrivalTime());
            // Overlap if thisDep < otherArr && thisArr > otherDep
            if (thisDep.before(otherArr) && thisArr.after(otherDep)) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null) return false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String bookingMonth = sdf.format(bookingDate);
        return bookingMonth.equals(month);
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
        this.trips.add(trip);
    }

    public boolean checkStopOverlap(Trip trip1, Trip trip2) {
        if (trip1 == null || trip2 == null) return false;
        Set<String> stops1 = trip1.getStopStations();
        Set<String> stops2 = trip2.getStopStations();
        if (stops1.isEmpty() || stops2.isEmpty()) return false;
        for (String s : stops1) {
            if (stops2.contains(s)) return true;
        }
        return false;
    }

    public boolean canPostTrip(Trip newTrip) {
        if (newTrip == null) return false;
        // Validate departure and arrival times
        if (newTrip.getDepartureTime() == null || newTrip.getArrivalTime() == null) return false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date dep = sdf.parse(newTrip.getDepartureTime());
            Date arr = sdf.parse(newTrip.getArrivalTime());
            if (!dep.before(arr)) return false;
        } catch (Exception e) {
            return false;
        }
        // Check no overlap with existing trips
        for (Trip existing : trips) {
            if (existing.isTimeConflicting(newTrip.getDepartureTime(), newTrip.getArrivalTime())) {
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
        this.bookings.add(booking);
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
            trip.addBooking(booking);
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (currentMonth == null) return 0;
        if (membershipPackage == null) return 0;
        if (!membershipPackage.hasAward(Award.POINTS)) return 0;
        int totalPoints = 0;
        for (Booking b : bookings) {
            if (b.isInMonth(currentMonth)) {
                totalPoints += b.getNumberOfSeats() * 5;
            }
        }
        return totalPoints;
    }
}