import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Calendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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
        if (trips == null) {
            trips = new ArrayList<>();
        }
        return trips;
    }

    public void addTrip(Trip trip) {
        if (trip != null) {
            getTrips().add(trip);
        }
    }

    public boolean checkStopOverlap(Trip trip1, Trip trip2) {
        if (trip1 == null || trip2 == null) {
            return false;
        }
        if (trip1.getStops() == null || trip2.getStops() == null) {
            return false;
        }
        if (trip1.getStops().isEmpty() || trip2.getStops().isEmpty()) {
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

    public boolean canPostTrip(Trip newTrip) {
        if (newTrip == null || newTrip.getDepartureTime() == null || newTrip.getArrivalTime() == null) {
            return false;
        }
        if (!Trip.isValidTimeFormat(newTrip.getDepartureTime()) || !Trip.isValidTimeFormat(newTrip.getArrivalTime())) {
            return false;
        }
        if (!Trip.isEarlier(newTrip.getDepartureTime(), newTrip.getArrivalTime())) {
            return false;
        }
        for (Trip existing : getTrips()) {
            if (existing == null) {
                continue;
            }
            if (existing.isTimeConflicting(newTrip.getDepartureTime(), newTrip.getArrivalTime())) {
                return false;
            }
            if (existing.getStops() != null && newTrip.getStops() != null
                    && !existing.getStops().isEmpty() && !newTrip.getStops().isEmpty()) {
                if (checkStopOverlap(existing, newTrip)) {
                    return false;
                }
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
        if (bookings == null) {
            bookings = new ArrayList<>();
        }
        return bookings;
    }

    public void addBooking(Booking booking) {
        if (booking != null) {
            getBookings().add(booking);
        }
    }

    public void bookTrip(Trip trip, int numberOfSeats) {
        Booking booking = new Booking();
        booking.setCustomer(this);
        booking.setTrip(trip);
        booking.setNumberOfSeats(numberOfSeats);
        booking.setBookingDate(new Date());
        if (booking.isBookingEligible()) {
            addBooking(booking);
            booking.updateTripSeats();
            if (trip != null) {
                trip.addBooking(booking);
            }
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        int total = 0;
        for (Booking booking : getBookings()) {
            if (booking != null) {
                total += booking.getTrip() == null ? 0 : booking.getTrip().calculateMonthlyPoints(this, currentMonth);
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

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null || bookingTime == null || customer.getMembershipPackage() == null) {
            return roundOneDecimal(price);
        }
        MembershipPackage mp = customer.getMembershipPackage();
        if (!mp.hasAward(Award.DISCOUNTS)) {
            return roundOneDecimal(price);
        }
        if (departureDate == null || departureTime == null || !isValidTimeFormat(bookingTime) || !isValidTimeFormat(departureTime)) {
            return roundOneDecimal(price);
        }
        Date bookingDateTime = combineDateAndTime(departureDate, bookingTime);
        Date depDateTime = combineDateAndTime(departureDate, departureTime);
        if (bookingDateTime == null || depDateTime == null) {
            return roundOneDecimal(price);
        }
        long diffMillis = depDateTime.getTime() - bookingDateTime.getTime();
        if (diffMillis >= 24L * 60L * 60L * 1000L) {
            return roundOneDecimal(price * 0.8);
        }
        return roundOneDecimal(price);
    }

    public int getBookedSeats() {
        int booked = 0;
        if (bookings != null) {
            for (Booking booking : bookings) {
                if (booking != null) {
                    booked += Math.max(0, booking.getNumberOfSeats());
                }
            }
        }
        return booked;
    }

    public List<Booking> getBookings() {
        if (bookings == null) {
            bookings = new ArrayList<>();
        }
        return bookings;
    }

    public void addBooking(Booking booking) {
        if (booking != null) {
            getBookings().add(booking);
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
        for (Booking booking : getBookings()) {
            if (booking != null && booking.getCustomer() == customer && booking.isInMonth(currentMonth)) {
                total += Math.max(0, booking.getNumberOfSeats()) * 5;
            }
        }
        return total;
    }

    public Set<String> getStopStations() {
        Set<String> result = new HashSet<>();
        if (stops != null) {
            for (Stop stop : stops) {
                if (stop != null && stop.getStopStation() != null) {
                    result.add(stop.getStopStation());
                }
            }
        }
        return result;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (departureTime == null || arrivalTime == null || newDepartureTime == null || newArrivalTime == null) {
            return false;
        }
        if (!isValidTimeFormat(departureTime) || !isValidTimeFormat(arrivalTime)
                || !isValidTimeFormat(newDepartureTime) || !isValidTimeFormat(newArrivalTime)) {
            return false;
        }
        int existingStart = parseMinutes(departureTime);
        int existingEnd = parseMinutes(arrivalTime);
        int newStart = parseMinutes(newDepartureTime);
        int newEnd = parseMinutes(newArrivalTime);
        if (existingStart == newStart && existingEnd == newEnd) {
            return true;
        }
        return existingStart < newEnd && newStart < existingEnd;
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
        if (stops == null) {
            stops = new ArrayList<>();
        }
        return stops;
    }

    public void addStop(Stop stop) {
        if (stop != null) {
            getStops().add(stop);
        }
    }

    static boolean isValidTimeFormat(String time) {
        if (time == null) {
            return false;
        }
        return time.matches("^([01]\\d|2[0-3]):[0-5]\\d$");
    }

    static int parseMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    static boolean isEarlier(String t1, String t2) {
        return parseMinutes(t1) < parseMinutes(t2);
    }

    static Date combineDateAndTime(Date date, String time) {
        if (date == null || !isValidTimeFormat(time)) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String[] parts = time.split(":");
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    static double roundOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
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
        if (trip.getDepartureDate() == null || trip.getDepartureTime() == null || !Trip.isValidTimeFormat(trip.getDepartureTime())) {
            return false;
        }
        if (trip.getNumberOfSeats() < numberOfSeats) {
            return false;
        }
        Date departureDateTime = Trip.combineDateAndTime(trip.getDepartureDate(), trip.getDepartureTime());
        if (departureDateTime == null) {
            return false;
        }
        long diffMillis = departureDateTime.getTime() - bookingDate.getTime();
        if (diffMillis <= 2L * 60L * 60L * 1000L) {
            return false;
        }
        if (customer.getBookings() != null) {
            for (Booking existing : customer.getBookings()) {
                if (existing == null || existing == this || existing.getTrip() == null || existing.getBookingDate() == null) {
                    continue;
                }
                if (sameDay(existing.getTrip().getDepartureDate(), trip.getDepartureDate())) {
                    if (existing.overlapsWith(trip)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void updateTripSeats() {
        if (trip != null) {
            trip.setNumberOfSeats(trip.getNumberOfSeats() - numberOfSeats);
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
        if (trip == null || otherTrip == null) {
            return false;
        }
        if (trip.getDepartureDate() == null || otherTrip.getDepartureDate() == null) {
            return false;
        }
        if (!sameDay(trip.getDepartureDate(), otherTrip.getDepartureDate())) {
            return false;
        }
        if (trip.getDepartureTime() == null || trip.getArrivalTime() == null
                || otherTrip.getDepartureTime() == null || otherTrip.getArrivalTime() == null) {
            return false;
        }
        int s1 = Trip.parseMinutes(trip.getDepartureTime());
        int e1 = Trip.parseMinutes(trip.getArrivalTime());
        int s2 = Trip.parseMinutes(otherTrip.getDepartureTime());
        int e2 = Trip.parseMinutes(otherTrip.getArrivalTime());
        return s1 < e2 && s2 < e1;
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        sdf.setLenient(false);
        return sdf.format(bookingDate).equals(month);
    }

    private boolean sameDay(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            return false;
        }
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

    public Award[] getAwards() {
        return awards;
    }

    public void setAwards(Award[] awards) {
        this.awards = awards;
    }

    public boolean hasAward(Award award) {
        if (award == null || awards == null) {
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