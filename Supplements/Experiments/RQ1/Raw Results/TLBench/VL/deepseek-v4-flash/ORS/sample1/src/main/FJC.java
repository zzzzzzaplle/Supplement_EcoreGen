import java.util.*;
import java.text.*;

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
    private List<Trip> trips;

    public Driver() {
        this.trips = new ArrayList<>();
    }

    public List<Trip> getTrips() { return trips; }

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
        String depTime = newTrip.getDepartureTime();
        String arrTime = newTrip.getArrivalTime();
        if (depTime == null || arrTime == null) return false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date dep = sdf.parse(depTime);
            Date arr = sdf.parse(arrTime);
            if (!dep.before(arr)) return false;
        } catch (ParseException e) {
            return false;
        }
        for (Trip existing : trips) {
            if (existing == null) continue;
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

    public MembershipPackage getMembershipPackage() { return membershipPackage; }
    public void setMembershipPackage(MembershipPackage membershipPackage) { this.membershipPackage = membershipPackage; }

    public List<Booking> getBookings() { return bookings; }

    public void addBooking(Booking booking) {
        this.bookings.add(booking);
    }

    public void bookTrip(Trip trip, int numberOfSeats) {
        if (trip == null || numberOfSeats <= 0) return;
        Booking booking = new Booking();
        booking.setNumberOfSeats(numberOfSeats);
        booking.setCustomer(this);
        booking.setTrip(trip);
        booking.setBookingDate(new Date());
        if (booking.isBookingEligible()) {
            booking.updateTripSeats();
            this.addBooking(booking);
            trip.addBooking(booking);
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (currentMonth == null || currentMonth.trim().isEmpty()) return 0;
        if (membershipPackage == null) return 0;
        if (!membershipPackage.hasAward(Award.POINTS)) return 0;
        int totalPoints = 0;
        for (Booking b : bookings) {
            if (b == null) continue;
            if (b.isInMonth(currentMonth)) {
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

    public List<Stop> getStops() { return stops; }

    public void addStop(Stop stop) {
        this.stops.add(stop);
    }

    public List<Booking> getBookings() { return bookings; }

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

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null) return price;
        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null) return price;
        if (!pkg.hasAward(Award.DISCOUNTS)) return price;
        if (bookingTime == null || bookingTime.trim().isEmpty()) return price;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date booking = sdf.parse(bookingTime);
            Date dep = sdf.parse(departureDate + " " + departureTime);
            long diff = dep.getTime() - booking.getTime();
            long hours24 = 24 * 60 * 60 * 1000L;
            if (diff >= hours24) {
                double discounted = price * 0.8;
                return Math.round(discounted * 10) / 10.0;
            }
        } catch (ParseException e) {
            return price;
        }
        return price;
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || currentMonth == null) return 0;
        return customer.computeMonthlyRewardPoints(currentMonth);
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (newDepartureTime == null || newArrivalTime == null) return false;
        if (departureTime == null || arrivalTime == null) return false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date existingDep = sdf.parse(departureTime);
            Date existingArr = sdf.parse(arrivalTime);
            Date newDep = sdf.parse(newDepartureTime);
            Date newArr = sdf.parse(newArrivalTime);
            // No conflict if existing ends exactly when new starts or new ends exactly when existing starts
            if (existingArr.equals(newDep) || newArr.equals(existingDep)) return false;
            // Check overlap
            if (newDep.before(existingArr) && newArr.after(existingDep)) return true;
            if (existingDep.before(newArr) && existingArr.after(newDep)) return true;
            return false;
        } catch (ParseException e) {
            return false;
        }
    }
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

    public int getNumberOfSeats() { return numberOfSeats; }
    public void setNumberOfSeats(int numberOfSeats) { this.numberOfSeats = numberOfSeats; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }

    public Date getBookingDate() { return bookingDate; }
    public void setBookingDate(Date bookingDate) { this.bookingDate = bookingDate; }

    public boolean isBookingEligible() {
        if (customer == null || trip == null || bookingDate == null) return false;
        if (numberOfSeats <= 0) return false;
        if (numberOfSeats > trip.getNumberOfSeats() - trip.getBookedSeats()) return false;
        // Check overlapping bookings on same day
        Calendar cal = Calendar.getInstance();
        cal.setTime(bookingDate);
        int bookingYear = cal.get(Calendar.YEAR);
        int bookingDay = cal.get(Calendar.DAY_OF_YEAR);
        for (Booking existing : customer.getBookings()) {
            if (existing == null) continue;
            if (existing.getTrip() == null) continue;
            Calendar existingCal = Calendar.getInstance();
            existingCal.setTime(existing.getBookingDate());
            int existingYear = existingCal.get(Calendar.YEAR);
            int existingDay = existingCal.get(Calendar.DAY_OF_YEAR);
            if (bookingYear == existingYear && bookingDay == existingDay) {
                if (overlapsWith(existing.getTrip())) return false;
            }
        }
        // Check booking time > 2 hours before departure
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            String depDateTime = trip.getDepartureDate() + " " + trip.getDepartureTime();
            Date dep = sdf.parse(depDateTime);
            String bookDateTime = sdf.format(bookingDate);
            Date book = sdf.parse(bookDateTime);
            long diff = dep.getTime() - book.getTime();
            long twoHours = 2 * 60 * 60 * 1000L;
            if (diff <= twoHours) return false; // must be strictly more than 2 hours
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public void updateTripSeats() {
        if (trip != null) {
            // Seats are reduced implicitly by checking booked seats
        }
    }

    public boolean overlapsWith(Trip otherTrip) {
        if (otherTrip == null || trip == null) return false;
        String dep1 = trip.getDepartureTime();
        String arr1 = trip.getArrivalTime();
        String dep2 = otherTrip.getDepartureTime();
        String arr2 = otherTrip.getArrivalTime();
        if (dep1 == null || arr1 == null || dep2 == null || arr2 == null) return false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date d1 = sdf.parse(dep1);
            Date a1 = sdf.parse(arr1);
            Date d2 = sdf.parse(dep2);
            Date a2 = sdf.parse(arr2);
            if (a1.equals(d2) || a2.equals(d1)) return false;
            if (d1.before(a2) && a1.after(d2)) return true;
            if (d2.before(a1) && a2.after(d1)) return true;
            return false;
        } catch (ParseException e) {
            return false;
        }
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null) return false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        return sdf.format(bookingDate).equals(month);
    }
}

class MembershipPackage {
    private Award[] awards;

    public MembershipPackage() {
        this.awards = new Award[0];
    }

    public Award[] getAwards() { return awards; }
    public void setAwards(Award[] awards) { this.awards = awards; }

    public boolean hasAward(Award award) {
        if (awards == null) return false;
        for (Award a : awards) {
            if (a == award) return true;
        }
        return false;
    }
}

enum Award {
    CASHBACK,
    DISCOUNTS,
    POINTS
}