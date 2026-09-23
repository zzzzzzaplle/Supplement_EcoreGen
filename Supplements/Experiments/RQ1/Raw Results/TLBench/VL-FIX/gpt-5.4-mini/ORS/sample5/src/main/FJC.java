import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Online Rideshare System (ORS) domain model and logic implementation.
 * All classes are included in a single compilation unit as requested.
 */

abstract class User {
    private String id;
    private String email;
    private String phoneNumber;

    public User() {
    }

    public User(String id, String email, String phoneNumber) {
        this.id = id;
        this.email = email;
        this.phoneNumber = phoneNumber;
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

    public Driver(String id, String email, String phoneNumber) {
        super(id, email, phoneNumber);
        this.trips = new ArrayList<>();
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    public void addTrip(Trip trip) {
        if (trip == null) {
            return;
        }
        if (this.trips == null) {
            this.trips = new ArrayList<>();
        }
        this.trips.add(trip);
    }

    /**
     * Determines whether two indirect trips share at least one common stop station.
     * Comparison is case-sensitive.
     */
    public boolean checkStopOverlap(Trip trip1, Trip trip2) {
        if (trip1 == null || trip2 == null) {
            return false;
        }
        if (trip1.getStops() == null || trip2.getStops() == null || trip1.getStops().isEmpty() || trip2.getStops().isEmpty()) {
            return false;
        }
        Set<String> stops1 = trip1.getStopStations();
        Set<String> stops2 = trip2.getStopStations();
        for (String stop : stops1) {
            if (stops2.contains(stop)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates whether the driver can publish the proposed trip.
     * The proposed trip must have valid time window and must not overlap with existing trips.
     * Adjacent boundaries are allowed.
     */
    public boolean canPostTrip(Trip newTrip) {
        if (newTrip == null) {
            return false;
        }
        if (newTrip.getDepartureTime() == null || newTrip.getArrivalTime() == null) {
            return false;
        }
        if (!Trip.isValidTime(newTrip.getDepartureTime()) || !Trip.isValidTime(newTrip.getArrivalTime())) {
            return false;
        }
        if (!Trip.isTimeEarlierThan(newTrip.getDepartureTime(), newTrip.getArrivalTime())) {
            return false;
        }
        if (this.trips == null || this.trips.isEmpty()) {
            return true;
        }
        for (Trip existing : this.trips) {
            if (existing == null) {
                continue;
            }
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

    public Customer(String id, String email, String phoneNumber) {
        super(id, email, phoneNumber);
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

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public void addBooking(Booking booking) {
        if (booking == null) {
            return;
        }
        if (this.bookings == null) {
            this.bookings = new ArrayList<>();
        }
        this.bookings.add(booking);
    }

    /**
     * Convenience method to create and add a booking.
     */
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

    /**
     * Computes monthly reward points for this customer.
     */
    public int computeMonthlyRewardPoints(String currentMonth) {
        if (this.membershipPackage == null || !this.membershipPackage.hasAward(Award.POINTS)) {
            return 0;
        }
        if (this.bookings == null || currentMonth == null) {
            return 0;
        }
        int total = 0;
        for (Booking booking : this.bookings) {
            if (booking != null && booking.isInMonth(currentMonth)) {
                total += booking.getNumberOfSeats() * 5;
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
    }

    public Trip(String departureStation, String arrivalStation, int numberOfSeats, Date departureDate, String departureTime, String arrivalTime, double price) {
        this.departureStation = departureStation;
        this.arrivalStation = arrivalStation;
        this.numberOfSeats = numberOfSeats;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
        this.bookings = new ArrayList<>();
        this.stops = new ArrayList<>();
    }

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null || bookingTime == null || this.departureDate == null || this.departureTime == null) {
            return roundToOneDecimal(this.price);
        }
        MembershipPackage membershipPackage = customer.getMembershipPackage();
        if (membershipPackage == null || !membershipPackage.hasAward(Award.DISCOUNTS)) {
            return roundToOneDecimal(this.price);
        }
        Date bookingDateTime = DateTimeUtil.parseDateTime(bookingTime);
        Date departureDateTime = DateTimeUtil.combineDateAndTime(this.departureDate, this.departureTime);
        if (bookingDateTime == null || departureDateTime == null) {
            return roundToOneDecimal(this.price);
        }
        long diffMillis = departureDateTime.getTime() - bookingDateTime.getTime();
        if (diffMillis >= 24L * 60L * 60L * 1000L) {
            return roundToOneDecimal(this.price * 0.8d);
        }
        return roundToOneDecimal(this.price);
    }

    public int getBookedSeats() {
        if (this.bookings == null) {
            return 0;
        }
        int total = 0;
        for (Booking booking : this.bookings) {
            if (booking != null) {
                total += booking.getNumberOfSeats();
            }
        }
        return total;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public void addBooking(Booking booking) {
        if (booking == null) {
            return;
        }
        if (this.bookings == null) {
            this.bookings = new ArrayList<>();
        }
        this.bookings.add(booking);
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || currentMonth == null) {
            return 0;
        }
        MembershipPackage membershipPackage = customer.getMembershipPackage();
        if (membershipPackage == null || !membershipPackage.hasAward(Award.POINTS)) {
            return 0;
        }
        if (customer.getBookings() == null) {
            return 0;
        }
        int total = 0;
        for (Booking booking : customer.getBookings()) {
            if (booking != null && booking.isInMonth(currentMonth) && Objects.equals(booking.getTrip(), this)) {
                total += booking.getNumberOfSeats() * 5;
            }
        }
        return total;
    }

    public Set<String> getStopStations() {
        Set<String> result = new HashSet<>();
        if (this.stops == null) {
            return result;
        }
        for (Stop stop : this.stops) {
            if (stop != null && stop.getStopStation() != null) {
                result.add(stop.getStopStation());
            }
        }
        return result;
    }

    /**
     * Returns true if the provided time interval overlaps with this trip's time interval.
     * Adjacent boundaries are allowed, identical periods are conflicts.
     */
    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (!isValidTime(newDepartureTime) || !isValidTime(newArrivalTime) || !isValidTime(this.departureTime) || !isValidTime(this.arrivalTime)) {
            return false;
        }
        int newStart = timeToMinutes(newDepartureTime);
        int newEnd = timeToMinutes(newArrivalTime);
        int existingStart = timeToMinutes(this.departureTime);
        int existingEnd = timeToMinutes(this.arrivalTime);
        return newStart < existingEnd && newEnd > existingStart;
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

    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }

    public void addStop(Stop stop) {
        if (stop == null) {
            return;
        }
        if (this.stops == null) {
            this.stops = new ArrayList<>();
        }
        this.stops.add(stop);
    }

    static boolean isValidTime(String time) {
        return DateTimeUtil.parseTimeToMinutes(time) != null;
    }

    static boolean isTimeEarlierThan(String departureTime, String arrivalTime) {
        Integer dep = DateTimeUtil.parseTimeToMinutes(departureTime);
        Integer arr = DateTimeUtil.parseTimeToMinutes(arrivalTime);
        if (dep == null || arr == null) {
            return false;
        }
        return dep < arr;
    }

    private double roundToOneDecimal(double value) {
        return Math.round(value * 10.0d) / 10.0d;
    }

    private static int timeToMinutes(String time) {
        Integer minutes = DateTimeUtil.parseTimeToMinutes(time);
        return minutes == null ? -1 : minutes;
    }
}

class Stop {
    private String stopStation;

    public Stop() {
    }

    public Stop(String stopStation) {
        this.stopStation = stopStation;
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

    public Booking(int numberOfSeats, Customer customer, Trip trip, Date bookingDate) {
        this.numberOfSeats = numberOfSeats;
        this.customer = customer;
        this.trip = trip;
        this.bookingDate = bookingDate;
    }

    public boolean isBookingEligible() {
        if (this.customer == null || this.trip == null || this.bookingDate == null) {
            return false;
        }
        if (this.numberOfSeats <= 0) {
            return false;
        }
        if (this.trip.getDepartureDate() == null || this.trip.getDepartureTime() == null) {
            return false;
        }
        if (this.trip.getNumberOfSeats() < this.numberOfSeats) {
            return false;
        }
        Date departureDateTime = DateTimeUtil.combineDateAndTime(this.trip.getDepartureDate(), this.trip.getDepartureTime());
        if (departureDateTime == null) {
            return false;
        }
        if (!bookingDate.before(DateTimeUtil.subtractHours(departureDateTime, 2))) {
            return false;
        }
        if (this.customer.getBookings() != null) {
            for (Booking existing : this.customer.getBookings()) {
                if (existing != null && existing != this && overlapsWith(existing.getTrip())) {
                    return false;
                }
            }
        }
        return true;
    }

    public void updateTripSeats() {
        if (this.trip == null) {
            return;
        }
        this.trip.setNumberOfSeats(this.trip.getNumberOfSeats() - this.numberOfSeats);
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
        if (this.trip == null || otherTrip == null || this.trip.getDepartureDate() == null || otherTrip.getDepartureDate() == null) {
            return false;
        }
        if (!sameDay(this.trip.getDepartureDate(), otherTrip.getDepartureDate())) {
            return false;
        }
        if (this.trip.getDepartureTime() == null || this.trip.getArrivalTime() == null || otherTrip.getDepartureTime() == null || otherTrip.getArrivalTime() == null) {
            return false;
        }
        int aStart = DateTimeUtil.parseTimeToMinutes(this.trip.getDepartureTime());
        int aEnd = DateTimeUtil.parseTimeToMinutes(this.trip.getArrivalTime());
        int bStart = DateTimeUtil.parseTimeToMinutes(otherTrip.getDepartureTime());
        int bEnd = DateTimeUtil.parseTimeToMinutes(otherTrip.getArrivalTime());
        if (aStart < 0 || aEnd < 0 || bStart < 0 || bEnd < 0) {
            return false;
        }
        return aStart < bEnd && aEnd > bStart;
    }

    public boolean isInMonth(String month) {
        if (this.bookingDate == null || month == null) {
            return false;
        }
        String normalized = month.trim();
        if (!normalized.matches("\\d{4}-\\d{2}")) {
            return false;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.bookingDate);
        int bookingYear = cal.get(Calendar.YEAR);
        int bookingMonth = cal.get(Calendar.MONTH) + 1;
        String bookingMonthStr = String.format("%04d-%02d", bookingYear, bookingMonth);
        return bookingMonthStr.equals(normalized);
    }

    private boolean sameDay(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }
}

class MembershipPackage {
    private Award[] awards;

    public MembershipPackage() {
        this.awards = new Award[0];
    }

    public MembershipPackage(Award[] awards) {
        this.awards = awards;
    }

    public Award[] getAwards() {
        return awards;
    }

    public void setAwards(Award[] awards) {
        this.awards = awards;
    }

    public boolean hasAward(Award award) {
        if (award == null || this.awards == null) {
            return false;
        }
        for (Award a : this.awards) {
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

class DateTimeUtil {
    private static final List<String> TIME_PATTERNS = Arrays.asList("HH:mm", "HH:mm:ss");
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final SimpleDateFormat DATE_ONLY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private DateTimeUtil() {
    }

    static Integer parseTimeToMinutes(String time) {
        if (time == null) {
            return null;
        }
        String[] parts = time.trim().split(":");
        if (parts.length < 2 || parts.length > 3) {
            return null;
        }
        try {
            int h = Integer.parseInt(parts[0]);
            int m = Integer.parseInt(parts[1]);
            int s = parts.length == 3 ? Integer.parseInt(parts[2]) : 0;
            if (h < 0 || h > 23 || m < 0 || m > 59 || s < 0 || s > 59) {
                return null;
            }
            return h * 60 + m;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    static Date parseDateTime(String value) {
        if (value == null) {
            return null;
        }
        try {
            DATE_TIME_FORMAT.setLenient(false);
            return DATE_TIME_FORMAT.parse(value.trim());
        } catch (ParseException e) {
            return null;
        }
    }

    static Date combineDateAndTime(Date date, String time) {
        if (date == null || time == null) {
            return null;
        }
        Integer minutes = parseTimeToMinutes(time);
        if (minutes == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = minutes / 60;
        int minute = minutes % 60;
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    static Date subtractHours(Date date, int hours) {
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, -hours);
        return cal.getTime();
    }

    static String formatDateOnly(Date date) {
        if (date == null) {
            return null;
        }
        DATE_ONLY_FORMAT.setLenient(false);
        return DATE_ONLY_FORMAT.format(date);
    }
}