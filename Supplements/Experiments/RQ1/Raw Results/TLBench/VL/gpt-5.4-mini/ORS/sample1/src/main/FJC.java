import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Abstract base class representing a user in the Online Rideshare System.
 */
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

/**
 * Driver who can publish trips.
 */
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
        for (String stop : stops1) {
            if (stops2.contains(stop)) {
                return true;
            }
        }
        return false;
    }

    public boolean canPostTrip(Trip newTrip) {
        if (newTrip == null || newTrip.getDepartureTime() == null || newTrip.getArrivalTime() == null) {
            return false;
        }
        if (!isValidTime(newTrip.getDepartureTime()) || !isValidTime(newTrip.getArrivalTime())) {
            return false;
        }
        if (!isDepartureEarlierThanArrival(newTrip.getDepartureTime(), newTrip.getArrivalTime())) {
            return false;
        }
        if (this.trips == null) {
            return true;
        }
        for (Trip existingTrip : this.trips) {
            if (existingTrip == null || existingTrip.getDepartureTime() == null || existingTrip.getArrivalTime() == null) {
                continue;
            }
            if (timesConflict(existingTrip.getDepartureTime(), existingTrip.getArrivalTime(),
                    newTrip.getDepartureTime(), newTrip.getArrivalTime())) {
                return false;
            }
            if (!existingTrip.getStops().isEmpty() && !newTrip.getStops().isEmpty()) {
                if (checkStopOverlap(existingTrip, newTrip)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValidTime(String time) {
        return parseMinutes(time) >= 0;
    }

    private boolean isDepartureEarlierThanArrival(String departure, String arrival) {
        int dep = parseMinutes(departure);
        int arr = parseMinutes(arrival);
        return dep >= 0 && arr >= 0 && dep < arr;
    }

    private boolean timesConflict(String existingDeparture, String existingArrival, String newDeparture, String newArrival) {
        int ed = parseMinutes(existingDeparture);
        int ea = parseMinutes(existingArrival);
        int nd = parseMinutes(newDeparture);
        int na = parseMinutes(newArrival);
        if (ed < 0 || ea < 0 || nd < 0 || na < 0) {
            return false;
        }
        return nd < ea && na > ed;
    }

    private int parseMinutes(String time) {
        if (time == null) {
            return -1;
        }
        String[] parts = time.trim().split(":");
        if (parts.length != 2) {
            return -1;
        }
        try {
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
                return -1;
            }
            return hour * 60 + minute;
        } catch (NumberFormatException ex) {
            return -1;
        }
    }
}

/**
 * Customer who can create bookings and receive rewards.
 */
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
            addBooking(booking);
            if (trip != null) {
                trip.addBooking(booking);
            }
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (this.membershipPackage == null || this.bookings == null || currentMonth == null) {
            return 0;
        }
        boolean hasPoints = this.membershipPackage.hasAward(Award.POINTS);
        if (!hasPoints) {
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

/**
 * Trip published by a driver.
 */
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
        if (customer == null || bookingTime == null || this.departureTime == null) {
            return roundOneDecimal(this.price);
        }
        MembershipPackage membershipPackage = customer.getMembershipPackage();
        if (membershipPackage == null || !membershipPackage.hasAward(Award.DISCOUNTS)) {
            return roundOneDecimal(this.price);
        }
        if (!isValidDateTime(bookingTime) || !isValidDateTime(this.departureTime)) {
            return roundOneDecimal(this.price);
        }
        long diffMillis = parseDateTime(this.departureTime).getTime() - parseDateTime(bookingTime).getTime();
        long hours = diffMillis / (60L * 60L * 1000L);
        if (diffMillis < 24L * 60L * 60L * 1000L) {
            return roundOneDecimal(this.price);
        }
        return roundOneDecimal(this.price * 0.8);
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

    public void addBooking(Booking booking) {
        if (booking != null) {
            this.bookings.add(booking);
        }
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || currentMonth == null || customer.getMembershipPackage() == null) {
            return 0;
        }
        if (!customer.getMembershipPackage().hasAward(Award.POINTS)) {
            return 0;
        }
        int total = 0;
        if (customer.getBookings() != null) {
            for (Booking booking : customer.getBookings()) {
                if (booking != null && booking.isInMonth(currentMonth)) {
                    total += booking.getNumberOfSeats() * 5;
                }
            }
        }
        return total;
    }

    public Set<String> getStopStations() {
        Set<String> stations = new HashSet<>();
        if (this.stops != null) {
            for (Stop stop : this.stops) {
                if (stop != null && stop.getStopStation() != null) {
                    stations.add(stop.getStopStation());
                }
            }
        }
        return stations;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (!isValidTime(newDepartureTime) || !isValidTime(newArrivalTime)) {
            return false;
        }
        int nd = parseMinutes(newDepartureTime);
        int na = parseMinutes(newArrivalTime);
        int ed = parseMinutes(this.departureTime);
        int ea = parseMinutes(this.arrivalTime);
        if (nd < 0 || na < 0 || ed < 0 || ea < 0) {
            return false;
        }
        return nd < ea && na > ed;
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

    private boolean isValidTime(String time) {
        return parseMinutes(time) >= 0;
    }

    private int parseMinutes(String time) {
        if (time == null) {
            return -1;
        }
        String[] parts = time.trim().split(":");
        if (parts.length != 2) {
            return -1;
        }
        try {
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
                return -1;
            }
            return hour * 60 + minute;
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private boolean isValidDateTime(String value) {
        return parseDateTime(value) != null;
    }

    private Date parseDateTime(String value) {
        if (value == null) {
            return null;
        }
        String[] parts = value.trim().split(" ");
        if (parts.length != 2) {
            return null;
        }
        String datePart = parts[0];
        String timePart = parts[1];
        String[] date = datePart.split("-");
        String[] time = timePart.split(":");
        if (date.length != 3 || time.length != 2) {
            return null;
        }
        try {
            int year = Integer.parseInt(date[0]);
            int month = Integer.parseInt(date[1]) - 1;
            int day = Integer.parseInt(date[2]);
            int hour = Integer.parseInt(time[0]);
            int minute = Integer.parseInt(time[1]);
            Calendar cal = Calendar.getInstance();
            cal.setLenient(false);
            cal.set(year, month, day, hour, minute, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTime();
        } catch (Exception ex) {
            return null;
        }
    }

    private double roundOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}

/**
 * Stop on an indirect trip.
 */
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

/**
 * Booking made by a customer for a trip.
 */
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
        if (this.trip.getNumberOfSeats() < this.numberOfSeats) {
            return false;
        }
        if (this.trip.getDepartureDate() == null || this.trip.getDepartureTime() == null) {
            return false;
        }
        Date departureDateTime = combineDateAndTime(this.trip.getDepartureDate(), this.trip.getDepartureTime());
        if (departureDateTime == null) {
            return false;
        }
        long diffMillis = departureDateTime.getTime() - this.bookingDate.getTime();
        if (diffMillis <= 2L * 60L * 60L * 1000L) {
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

    public boolean overlapsWith(Trip trip) {
        if (this.trip == null || trip == null || this.trip.getDepartureDate() == null || trip.getDepartureDate() == null) {
            return false;
        }
        Date thisStart = combineDateAndTime(this.trip.getDepartureDate(), this.trip.getDepartureTime());
        Date thisEnd = combineDateAndTime(this.trip.getDepartureDate(), this.trip.getArrivalTime());
        Date otherStart = combineDateAndTime(trip.getDepartureDate(), trip.getDepartureTime());
        Date otherEnd = combineDateAndTime(trip.getDepartureDate(), trip.getArrivalTime());
        if (thisStart == null || thisEnd == null || otherStart == null || otherEnd == null) {
            return false;
        }
        return thisStart.before(otherEnd) && otherStart.before(thisEnd);
    }

    public boolean isInMonth(String month) {
        if (this.bookingDate == null || month == null) {
            return false;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.bookingDate);
        int bookingMonth = cal.get(Calendar.MONTH) + 1;
        int bookingYear = cal.get(Calendar.YEAR);
        String formatted = String.format("%04d-%02d", bookingYear, bookingMonth);
        return formatted.equals(month);
    }

    private Date combineDateAndTime(Date date, String time) {
        if (date == null || time == null) {
            return null;
        }
        String[] parts = time.trim().split(":");
        if (parts.length != 2) {
            return null;
        }
        try {
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, minute);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTime();
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}

/**
 * Membership package containing rewards.
 */
class MembershipPackage {
    private Award[] awards;

    public MembershipPackage() {
        this.awards = new Award[0];
    }

    public Award[] getAwards() {
        return awards;
    }

    public void setAwards(Award[] awards) {
        this.awards = awards;
    }

    public boolean hasAward(Award award) {
        if (this.awards == null || award == null) {
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

/**
 * Supported award types.
 */
enum Award {
    CASHBACK,
    DISCOUNTS,
    POINTS
}