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
    private List<Trip> trips = new ArrayList<>();

    public Driver() {}

    public List<Trip> getTrips() { return trips; }
    public void addTrip(Trip trip) { this.trips.add(trip); }

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
        if (depTime.compareTo(arrTime) >= 0) return false;
        for (Trip existing : trips) {
            if (existing == null) continue;
            if (existing.isTimeConflicting(depTime, arrTime)) return false;
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
        if (trip == null || numberOfSeats <= 0) return;
        Booking booking = new Booking();
        booking.setCustomer(this);
        booking.setTrip(trip);
        booking.setNumberOfSeats(numberOfSeats);
        booking.setBookingDate(new Date());
        if (booking.isBookingEligible()) {
            booking.updateTripSeats();
            this.bookings.add(booking);
            trip.addBooking(booking);
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (currentMonth == null || currentMonth.isEmpty()) return 0;
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
    private List<Booking> bookings = new ArrayList<>();
    private List<Stop> stops = new ArrayList<>();

    public Trip() {}

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

    public List<Booking> getBookings() { return bookings; }
    public void addBooking(Booking booking) { this.bookings.add(booking); }

    public List<Stop> getStops() { return stops; }
    public void addStop(Stop stop) { this.stops.add(stop); }

    public int getBookedSeats() {
        int sum = 0;
        for (Booking b : bookings) {
            if (b != null) sum += b.getNumberOfSeats();
        }
        return sum;
    }

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null || bookingTime == null) return this.price;
        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null) return this.price;
        if (!pkg.hasAward(Award.DISCOUNTS)) return this.price;
        // bookingTime format "yyyy-MM-dd HH:mm"
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date bTime = sdf.parse(bookingTime);
            Date depTime = sdf.parse(this.departureDate.toString().substring(0,10) + " " + this.departureTime);
            long diff = depTime.getTime() - bTime.getTime();
            long hours24 = 24 * 60 * 60 * 1000L;
            if (diff >= hours24) {
                double discounted = this.price * 0.8;
                return Math.round(discounted * 10) / 10.0;
            }
        } catch (ParseException e) {
            return this.price;
        }
        return this.price;
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || currentMonth == null) return 0;
        return customer.computeMonthlyRewardPoints(currentMonth);
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
        if (this.departureTime == null || this.arrivalTime == null) return false;
        // if completely overlapping or partially overlapping, but not adjacent
        // conflict if not (newArrival <= this.departure OR newDeparture >= this.arrival)
        boolean endsBeforeStarts = newArrivalTime.compareTo(this.departureTime) <= 0;
        boolean startsAfterEnds = newDepartureTime.compareTo(this.arrivalTime) >= 0;
        return !(endsBeforeStarts || startsAfterEnds);
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
        // trip must exist (non-null already)
        int availableSeats = trip.getNumberOfSeats() - trip.getBookedSeats();
        if (numberOfSeats <= 0 || numberOfSeats > availableSeats) return false;
        // check overlapping bookings on same day
        Calendar cal = Calendar.getInstance();
        cal.setTime(bookingDate);
        int year = cal.get(Calendar.YEAR);
        int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
        for (Booking b : customer.getBookings()) {
            if (b == null || b.getTrip() == null) continue;
            if (b.overlapsWith(trip) && b.getBookingDate() != null) {
                Calendar bCal = Calendar.getInstance();
                bCal.setTime(b.getBookingDate());
                if (bCal.get(Calendar.YEAR) == year && bCal.get(Calendar.DAY_OF_YEAR) == dayOfYear) {
                    return false;
                }
            }
        }
        // booking must be more than 2 hours before departure
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            String depDateTimeStr = trip.getDepartureDate().toString().substring(0,10) + " " + trip.getDepartureTime();
            Date depDateTime = sdf.parse(depDateTimeStr);
            long diff = depDateTime.getTime() - bookingDate.getTime();
            long twoHours = 2 * 60 * 60 * 1000L;
            if (diff <= twoHours) return false;
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public void updateTripSeats() {
        if (trip != null) {
            trip.setNumberOfSeats(trip.getNumberOfSeats() - numberOfSeats);
        }
    }

    public boolean overlapsWith(Trip otherTrip) {
        if (trip == null || otherTrip == null) return false;
        // compare times only, ignoring date? assume same day check is done elsewhere
        return trip.isTimeConflicting(otherTrip.getDepartureTime(), otherTrip.getArrivalTime());
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null) return false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        return sdf.format(bookingDate).equals(month);
    }
}

class MembershipPackage {
    private Award[] awards;

    public MembershipPackage() {}

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