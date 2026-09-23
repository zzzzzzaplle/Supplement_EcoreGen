import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Calendar;
import java.util.Objects;

/**
 * Online Rideshare System (ORS) domain model and business logic.
 * All classes are included in a single file for convenience.
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
        if (this.trips == null) {
            this.trips = new ArrayList<>();
        }
        if (trip != null) {
            this.trips.add(trip);
        }
    }

    /**
     * Determines whether two indirect trips share at least one common stop station.
     * Case-sensitive comparison. Returns false if any input is null or any stop collection is empty.
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
        if (stops1 == null || stops2 == null || stops1.isEmpty() || stops2.isEmpty()) {
            return false;
        }
        for (String stop : stops1) {
            if (stops2.contains(stop)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates whether a driver can publish a new trip.
     * Invalid inputs return false. The trip must have a valid time window and not overlap
     * with existing trips. Adjacent boundaries are allowed.
     */
    public boolean canPostTrip(Trip newTrip) {
        if (newTrip == null) {
            return false;
        }
        if (newTrip.getDepartureTime() == null || newTrip.getArrivalTime() == null) {
            return false;
        }
        if (!isValidTimeWindow(newTrip.getDepartureTime(), newTrip.getArrivalTime())) {
            return false;
        }
        if (this.trips == null) {
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

    private boolean isValidTimeWindow(String departureTime, String arrivalTime) {
        int dep = parseTimeToMinutes(departureTime);
        int arr = parseTimeToMinutes(arrivalTime);
        if (dep < 0 || arr < 0) {
            return false;
        }
        return dep < arr;
    }

    private int parseTimeToMinutes(String time) {
        if (time == null) {
            return -1;
        }
        String[] parts = time.trim().split(":");
        if (parts.length != 2) {
            return -1;
        }
        try {
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59) {
                return -1;
            }
            return hours * 60 + minutes;
        } catch (NumberFormatException ex) {
            return -1;
        }
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
        if (this.bookings == null) {
            this.bookings = new ArrayList<>();
        }
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
            this.addBooking(booking);
            if (trip != null) {
                trip.addBooking(booking);
            }
            booking.updateTripSeats();
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (this.membershipPackage == null || this.bookings == null || currentMonth == null) {
            return 0;
        }
        if (!this.membershipPackage.hasAward(Award.POINTS)) {
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
        if (customer == null || bookingTime == null || this.departureTime == null) {
            return this.price;
        }
        MembershipPackage membershipPackage = customer.getMembershipPackage();
        if (membershipPackage == null || !membershipPackage.hasAward(Award.DISCOUNTS)) {
            return this.price;
        }
        Integer bookingMinutes = parseDateTimeToMinutes(bookingTime);
        Integer departureMinutes = parseDateTimeToMinutes(composeDepartureDateTime());
        if (bookingMinutes == null || departureMinutes == null) {
            return this.price;
        }
        long diffMinutes = departureMinutes - bookingMinutes;
        if (diffMinutes >= 24L * 60L) {
            double discounted = this.price * 0.8d;
            return Math.round(discounted * 10.0d) / 10.0d;
        }
        return this.price;
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
        if (this.bookings == null) {
            this.bookings = new ArrayList<>();
        }
        if (booking != null) {
            this.bookings.add(booking);
        }
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || currentMonth == null) {
            return 0;
        }
        MembershipPackage membershipPackage = customer.getMembershipPackage();
        if (membershipPackage == null || !membershipPackage.hasAward(Award.POINTS)) {
            return 0;
        }
        int total = 0;
        if (customer.getBookings() == null) {
            return 0;
        }
        for (Booking booking : customer.getBookings()) {
            if (booking != null && booking.isInMonth(currentMonth)) {
                total += booking.getNumberOfSeats() * 5;
            }
        }
        return total;
    }

    public Set<String> getStopStations() {
        Set<String> stations = new HashSet<>();
        if (this.stops == null) {
            return stations;
        }
        for (Stop stop : this.stops) {
            if (stop != null && stop.getStopStation() != null) {
                stations.add(stop.getStopStation());
            }
        }
        return stations;
    }

    /**
     * Checks if the existing trip conflicts with a new time window.
     * Overlap is strict; adjacent boundaries are allowed.
     */
    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        int existingStart = parseTimeToMinutes(this.departureTime);
        int existingEnd = parseTimeToMinutes(this.arrivalTime);
        int newStart = parseTimeToMinutes(newDepartureTime);
        int newEnd = parseTimeToMinutes(newArrivalTime);
        if (existingStart < 0 || existingEnd < 0 || newStart < 0 || newEnd < 0) {
            return false;
        }
        if (existingStart >= existingEnd || newStart >= newEnd) {
            return false;
        }
        return existingStart < newEnd && newStart < existingEnd;
    }

    private int parseTimeToMinutes(String time) {
        if (time == null) {
            return -1;
        }
        String[] parts = time.trim().split(":");
        if (parts.length != 2) {
            return -1;
        }
        try {
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59) {
                return -1;
            }
            return hours * 60 + minutes;
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private Integer parseDateTimeToMinutes(String dateTime) {
        if (dateTime == null) {
            return null;
        }
        String[] parts = dateTime.trim().split("\\s+");
        if (parts.length != 2) {
            return null;
        }
        int timeMinutes = parseTimeToMinutes(parts[1]);
        if (timeMinutes < 0) {
            return null;
        }
        int dayIndex = parseDateToDays(parts[0]);
        if (dayIndex < 0) {
            return null;
        }
        return dayIndex * 24 * 60 + timeMinutes;
    }

    private int parseDateToDays(String date) {
        if (date == null) {
            return -1;
        }
        String[] parts = date.trim().split("-");
        if (parts.length != 3) {
            return -1;
        }
        try {
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);
            if (year < 0 || month < 1 || month > 12 || day < 1 || day > 31) {
                return -1;
            }
            return year * 372 + month * 31 + day;
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private String composeDepartureDateTime() {
        if (this.departureDate == null || this.departureTime == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.departureDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return String.format("%04d-%02d-%02d %s", year, month, day, this.departureTime);
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
        if (this.stops == null) {
            this.stops = new ArrayList<>();
        }
        if (stop != null) {
            this.stops.add(stop);
        }
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

    /**
     * Validates booking eligibility according to system rules.
     * Invalid inputs return false.
     */
    public boolean isBookingEligible() {
        if (this.customer == null || this.trip == null || this.bookingDate == null) {
            return false;
        }
        if (this.numberOfSeats <= 0) {
            return false;
        }
        if (this.trip.getNumberOfSeats() < this.numberOfSeats) {
            return false;
        }
        if (this.trip.getBookings() != null) {
            for (Booking existing : this.trip.getBookings()) {
                if (existing != null && existing.getBookingDate() != null && existing.getCustomer() != null && existing.getTrip() != null) {
                    if (Objects.equals(existing.getCustomer(), this.customer) && overlapsWith(existing.getTrip())) {
                        return false;
                    }
                }
            }
        }
        if (this.trip.getDepartureDate() == null || this.trip.getDepartureTime() == null) {
            return false;
        }
        long bookingMillis = this.bookingDate.getTime();
        long departureMillis = this.trip.getDepartureDate().getTime() + parseTimeToMillis(this.trip.getDepartureTime());
        long diffMillis = departureMillis - bookingMillis;
        return diffMillis > 2L * 60L * 60L * 1000L;
    }

    public void updateTripSeats() {
        if (this.trip != null) {
            this.trip.setNumberOfSeats(this.trip.getNumberOfSeats() - this.numberOfSeats);
        }
    }

    public boolean overlapsWith(Trip trip) {
        if (trip == null || this.trip == null) {
            return false;
        }
        if (this.trip.getDepartureDate() == null || trip.getDepartureDate() == null) {
            return false;
        }
        if (!sameDay(this.trip.getDepartureDate(), trip.getDepartureDate())) {
            return false;
        }
        int existingStart = parseTimeToMinutes(this.trip.getDepartureTime());
        int existingEnd = parseTimeToMinutes(this.trip.getArrivalTime());
        int otherStart = parseTimeToMinutes(trip.getDepartureTime());
        int otherEnd = parseTimeToMinutes(trip.getArrivalTime());
        if (existingStart < 0 || existingEnd < 0 || otherStart < 0 || otherEnd < 0) {
            return false;
        }
        return existingStart < otherEnd && otherStart < existingEnd;
    }

    public boolean isInMonth(String month) {
        if (month == null || this.bookingDate == null) {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.bookingDate);
        String bookingMonth = String.format("%04d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
        return bookingMonth.equals(month);
    }

    private boolean sameDay(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    private int parseTimeToMinutes(String time) {
        if (time == null) {
            return -1;
        }
        String[] parts = time.trim().split(":");
        if (parts.length != 2) {
            return -1;
        }
        try {
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59) {
                return -1;
            }
            return hours * 60 + minutes;
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private long parseTimeToMillis(String time) {
        int minutes = parseTimeToMinutes(time);
        if (minutes < 0) {
            return -1L;
        }
        return minutes * 60L * 1000L;
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