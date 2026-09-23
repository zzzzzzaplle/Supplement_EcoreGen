import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
        List<Stop> stops1 = trip1.getStops();
        List<Stop> stops2 = trip2.getStops();
        if (stops1 == null || stops1.isEmpty() || stops2 == null || stops2.isEmpty()) {
            return false;
        }
        Set<String> stationSet1 = new HashSet<>();
        for (Stop s : stops1) {
            if (s != null && s.getStopStation() != null) {
                stationSet1.add(s.getStopStation());
            }
        }
        for (Stop s : stops2) {
            if (s != null && s.getStopStation() != null && stationSet1.contains(s.getStopStation())) {
                return true;
            }
        }
        return false;
    }

    public boolean canPostTrip(Trip newTrip) {
        if (newTrip == null) {
            return false;
        }
        if (newTrip.getDepartureDate() == null
                || newTrip.getDepartureTime() == null
                || newTrip.getArrivalTime() == null) {
            return false;
        }
        if (!isTimeBefore(newTrip.getDepartureTime(), newTrip.getArrivalTime())) {
            return false;
        }
        if (this.trips == null) {
            return true;
        }
        for (Trip existing : this.trips) {
            if (existing == null) {
                continue;
            }
            if (existing.getDepartureDate() == null
                    || existing.getDepartureTime() == null
                    || existing.getArrivalTime() == null) {
                continue;
            }
            if (existing.getDepartureDate().equals(newTrip.getDepartureDate())) {
                if (isTimeConflictingHelper(existing.getDepartureTime(), existing.getArrivalTime(),
                        newTrip.getDepartureTime(), newTrip.getArrivalTime())) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isConflicting(String s1Start, String s1End, String s2Start, String s2End) {
        return s1Start.compareTo(s2End) < 0 && s2Start.compareTo(s1End) < 0;
    }

    private boolean isTimeConflictingHelper(String existingStart, String existingEnd,
                                            String newStart, String newEnd) {
        return isConflicting(existingStart, existingEnd, newStart, newEnd);
    }

    private boolean isTimeBefore(String start, String end) {
        if (start == null || end == null) {
            return false;
        }
        return start.compareTo(end) < 0;
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
        Booking b = new Booking();
        b.setCustomer(this);
        b.setTrip(trip);
        b.setNumberOfSeats(numberOfSeats);
        b.setBookingDate(new Date());
        if (b.isBookingEligible()) {
            b.updateTripSeats();
            this.bookings.add(b);
            if (trip != null) {
                trip.addBooking(b);
            }
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (membershipPackage == null || !membershipPackage.hasAward(Award.POINTS)) {
            return 0;
        }
        if (currentMonth == null) {
            return 0;
        }
        int total = 0;
        if (bookings != null) {
            for (Booking b : bookings) {
                if (b != null && b.isInMonth(currentMonth)) {
                    total += b.getNumberOfSeats() * 5;
                }
            }
        }
        return total;
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
        this.numberOfSeats = 0;
        this.price = 0.0;
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

    public List<Booking> getBookings() {
        return bookings;
    }

    public void addBooking(Booking booking) {
        if (booking != null) {
            this.bookings.add(booking);
        }
    }

    public int getBookedSeats() {
        int total = 0;
        if (bookings != null) {
            for (Booking b : bookings) {
                if (b != null) {
                    total += b.getNumberOfSeats();
                }
            }
        }
        return total;
    }

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null || customer.getMembershipPackage() == null || bookingTime == null
                || this.departureTime == null) {
            return this.price;
        }
        if (!customer.getMembershipPackage().hasAward(Award.DISCOUNTS)) {
            return this.price;
        }
        if (isAtLeast24HoursBefore(bookingTime, this.departureTime)) {
            double discounted = this.price * 0.8;
            return Math.round(discounted * 10.0) / 10.0;
        }
        return this.price;
    }

    private boolean isAtLeast24HoursBefore(String bookingTime, String departureTime) {
        if (bookingTime == null || departureTime == null) {
            return false;
        }
        long diff = timeToMinutes(departureTime) - timeToMinutes(bookingTime);
        return diff >= 24 * 60;
    }

    private long timeToMinutes(String time) {
        if (time == null) return 0;
        String[] parts = time.split(":");
        if (parts.length < 2) return 0;
        try {
            int h = Integer.parseInt(parts[0]);
            int m = Integer.parseInt(parts[1]);
            return h * 60L + m;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || currentMonth == null) {
            return 0;
        }
        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null || !pkg.hasAward(Award.POINTS)) {
            return 0;
        }
        int total = 0;
        if (bookings != null) {
            for (Booking b : bookings) {
                if (b != null && b.getCustomer() != null && b.getCustomer().equals(customer)
                        && b.isInMonth(currentMonth)) {
                    total += b.getNumberOfSeats() * 5;
                }
            }
        }
        return total;
    }

    public Set<String> getStopStations() {
        Set<String> set = new HashSet<>();
        if (stops != null) {
            for (Stop s : stops) {
                if (s != null && s.getStopStation() != null) {
                    set.add(s.getStopStation());
                }
            }
        }
        return set;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (newDepartureTime == null || newArrivalTime == null
                || this.departureTime == null || this.arrivalTime == null) {
            return false;
        }
        return this.departureTime.compareTo(newArrivalTime) < 0
                && newDepartureTime.compareTo(this.arrivalTime) < 0;
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
        this.numberOfSeats = 0;
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

    public boolean isBookingEligible() {
        if (customer == null || trip == null || bookingDate == null) {
            return false;
        }
        if (numberOfSeats <= 0) {
            return false;
        }
        if (trip.getDepartureDate() == null || trip.getDepartureTime() == null) {
            return false;
        }
        if (trip.getNumberOfSeats() - trip.getBookedSeats() < numberOfSeats) {
            return false;
        }
        if (customer.getBookings() != null) {
            for (Booking existing : customer.getBookings()) {
                if (existing != null && existing != this && existing.overlapsWith(trip)) {
                    return false;
                }
            }
        }
        long diffMs = getTripDepartureTime().getTime() - bookingDate.getTime();
        long diffMinutes = TimeUnit.MILLISECONDS.toMinutes(diffMs);
        if (diffMinutes <= 120) {
            return false;
        }
        return true;
    }

    public void updateTripSeats() {
        if (trip != null) {
            trip.setNumberOfSeats(trip.getNumberOfSeats() - this.numberOfSeats);
        }
    }

    public boolean overlapsWith(Trip otherTrip) {
        if (this.trip == null || otherTrip == null) {
            return false;
        }
        if (this.trip == otherTrip) {
            return true;
        }
        Date d1 = this.trip.getDepartureDate();
        Date d2 = otherTrip.getDepartureDate();
        if (d1 == null || d2 == null) {
            return false;
        }
        if (!isSameDay(d1, d2)) {
            return false;
        }
        String s1 = this.trip.getDepartureTime();
        String e1 = this.trip.getArrivalTime();
        String s2 = otherTrip.getDepartureTime();
        String e2 = otherTrip.getArrivalTime();
        if (s1 == null || e1 == null || s2 == null || e2 == null) {
            return false;
        }
        return s1.compareTo(e2) < 0 && s2.compareTo(e1) < 0;
    }

    private boolean isSameDay(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    private Date getTripDepartureTime() {
        if (trip == null || trip.getDepartureDate() == null || trip.getDepartureTime() == null) {
            return new Date(0);
        }
        String[] parts = trip.getDepartureTime().split(":");
        Calendar cal = Calendar.getInstance();
        cal.setTime(trip.getDepartureDate());
        try {
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
            cal.set(Calendar.MINUTE, parts.length > 1 ? Integer.parseInt(parts[1]) : 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        } catch (NumberFormatException e) {
            return new Date(0);
        }
        return cal.getTime();
    }

    public boolean isInMonth(String month) {
        if (month == null || this.bookingDate == null) {
            return false;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.bookingDate);
        int year = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        String monthStr = String.format("%04d-%02d", year, m);
        return monthStr.equals(month);
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