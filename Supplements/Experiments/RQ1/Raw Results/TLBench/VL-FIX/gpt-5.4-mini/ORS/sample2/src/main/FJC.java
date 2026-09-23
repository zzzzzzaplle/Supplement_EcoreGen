import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
            if (this.trips == null) {
                this.trips = new ArrayList<>();
            }
            this.trips.add(trip);
        }
    }

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
        if (!Trip.isValidTimeFormat(newTrip.getDepartureTime()) || !Trip.isValidTimeFormat(newTrip.getArrivalTime())) {
            return false;
        }
        if (!Trip.isDepartureEarlierThanArrival(newTrip.getDepartureTime(), newTrip.getArrivalTime())) {
            return false;
        }

        if (trips == null) {
            return true;
        }
        for (Trip existing : trips) {
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
            if (this.bookings == null) {
                this.bookings = new ArrayList<>();
            }
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
        if (currentMonth == null || bookings == null || membershipPackage == null || membershipPackage.getAwards() == null) {
            return 0;
        }
        boolean hasPoints = membershipPackage.hasAward(Award.POINTS);
        if (!hasPoints) {
            return 0;
        }
        int points = 0;
        for (Booking booking : bookings) {
            if (booking != null && booking.isInMonth(currentMonth)) {
                points += booking.getNumberOfSeats() * 5;
            }
        }
        return points;
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
        if (customer == null || bookingTime == null || customer.getMembershipPackage() == null || customer.getMembershipPackage().getAwards() == null) {
            return price;
        }
        if (!customer.getMembershipPackage().hasAward(Award.DISCOUNTS)) {
            return price;
        }
        if (departureDate == null || departureTime == null || !isValidTimeFormat(departureTime) || !isValidTimeFormat(bookingTime)) {
            return price;
        }
        Date departureDateTime = DateUtils.combineDateAndTime(departureDate, departureTime);
        Date bookingDateTime = DateUtils.combineDateAndTime(departureDate, bookingTime);
        if (departureDateTime == null || bookingDateTime == null) {
            return price;
        }
        long diffMillis = departureDateTime.getTime() - bookingDateTime.getTime();
        if (diffMillis < 24L * 60L * 60L * 1000L) {
            return price;
        }
        double discounted = price * 0.8d;
        return Math.round(discounted * 10.0d) / 10.0d;
    }

    public int getBookedSeats() {
        if (bookings == null) {
            return 0;
        }
        int sum = 0;
        for (Booking booking : bookings) {
            if (booking != null) {
                sum += booking.getNumberOfSeats();
            }
        }
        return sum;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void addBooking(Booking booking) {
        if (booking != null) {
            if (this.bookings == null) {
                this.bookings = new ArrayList<>();
            }
            this.bookings.add(booking);
        }
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || currentMonth == null || customer.getMembershipPackage() == null || customer.getMembershipPackage().getAwards() == null) {
            return 0;
        }
        if (!customer.getMembershipPackage().hasAward(Award.POINTS)) {
            return 0;
        }
        int points = 0;
        if (bookings == null) {
            return 0;
        }
        for (Booking booking : bookings) {
            if (booking != null && booking.getCustomer() == customer && booking.isInMonth(currentMonth)) {
                points += booking.getNumberOfSeats() * 5;
            }
        }
        return points;
    }

    public Set<String> getStopStations() {
        Set<String> stations = new HashSet<>();
        if (stops == null) {
            return stations;
        }
        for (Stop stop : stops) {
            if (stop != null && stop.getStopStation() != null) {
                stations.add(stop.getStopStation());
            }
        }
        return stations;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (departureTime == null || arrivalTime == null || newDepartureTime == null || newArrivalTime == null) {
            return false;
        }
        if (!isValidTimeFormat(departureTime) || !isValidTimeFormat(arrivalTime) || !isValidTimeFormat(newDepartureTime) || !isValidTimeFormat(newArrivalTime)) {
            return false;
        }
        int existingStart = TimeUtils.toMinutes(departureTime);
        int existingEnd = TimeUtils.toMinutes(arrivalTime);
        int newStart = TimeUtils.toMinutes(newDepartureTime);
        int newEnd = TimeUtils.toMinutes(newArrivalTime);
        return newStart < existingEnd && existingStart < newEnd;
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
            if (this.stops == null) {
                this.stops = new ArrayList<>();
            }
            this.stops.add(stop);
        }
    }

    public static boolean isValidTimeFormat(String time) {
        return TimeUtils.isValidTimeFormat(time);
    }

    public static boolean isDepartureEarlierThanArrival(String dep, String arr) {
        if (!isValidTimeFormat(dep) || !isValidTimeFormat(arr)) {
            return false;
        }
        return TimeUtils.toMinutes(dep) < TimeUtils.toMinutes(arr);
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
        if (trip.getNumberOfSeats() < numberOfSeats) {
            return false;
        }
        if (trip.getDepartureDate() == null || trip.getDepartureTime() == null) {
            return false;
        }
        Date departureDateTime = DateUtils.combineDateAndTime(trip.getDepartureDate(), trip.getDepartureTime());
        if (departureDateTime == null) {
            return false;
        }
        long diffMillis = departureDateTime.getTime() - bookingDate.getTime();
        if (diffMillis <= 2L * 60L * 60L * 1000L) {
            return false;
        }
        if (customer.getBookings() != null) {
            for (Booking existing : customer.getBookings()) {
                if (existing != null && existing != this && overlapsWith(existing.getTrip())) {
                    if (existing.getTrip() != null && existing.getTrip().getDepartureDate() != null) {
                        Date existingDeparture = DateUtils.combineDateAndTime(existing.getTrip().getDepartureDate(), existing.getTrip().getDepartureTime());
                        Date existingArrival = DateUtils.combineDateAndTime(existing.getTrip().getDepartureDate(), existing.getTrip().getArrivalTime());
                        if (existingDeparture != null && existingArrival != null && DateUtils.isSameDay(existingDeparture, departureDateTime)) {
                            if (TimeUtils.intervalsOverlapStrict(DateUtils.combineDateAndTime(trip.getDepartureDate(), trip.getDepartureTime()),
                                    DateUtils.combineDateAndTime(trip.getDepartureDate(), trip.getArrivalTime()),
                                    existingDeparture, existingArrival)) {
                                return false;
                            }
                        }
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
        if (trip == null || otherTrip == null || trip.getDepartureDate() == null || otherTrip.getDepartureDate() == null) {
            return false;
        }
        Date thisStart = DateUtils.combineDateAndTime(trip.getDepartureDate(), trip.getDepartureTime());
        Date thisEnd = DateUtils.combineDateAndTime(trip.getDepartureDate(), trip.getArrivalTime());
        Date otherStart = DateUtils.combineDateAndTime(otherTrip.getDepartureDate(), otherTrip.getDepartureTime());
        Date otherEnd = DateUtils.combineDateAndTime(otherTrip.getDepartureDate(), otherTrip.getArrivalTime());
        if (thisStart == null || thisEnd == null || otherStart == null || otherEnd == null) {
            return false;
        }
        return DateUtils.isSameDay(thisStart, otherStart) && TimeUtils.intervalsOverlapStrict(thisStart, thisEnd, otherStart, otherEnd);
    }

    public boolean isInMonth(String month) {
        return DateUtils.isInMonth(bookingDate, month);
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

class DateUtils {
    private DateUtils() {
    }

    public static Date combineDateAndTime(Date date, String time) {
        if (date == null || time == null || !TimeUtils.isValidTimeFormat(time)) {
            return null;
        }
        int minutes = TimeUtils.toMinutes(time);
        long dayMillis = 24L * 60L * 60L * 1000L;
        long datePart = floorToDay(date.getTime());
        return new Date(datePart + minutes * 60L * 1000L);
    }

    public static long floorToDay(long millis) {
        long dayMillis = 24L * 60L * 60L * 1000L;
        return (millis / dayMillis) * dayMillis;
    }

    public static boolean isSameDay(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            return false;
        }
        return floorToDay(d1.getTime()) == floorToDay(d2.getTime());
    }

    public static boolean isInMonth(Date date, String month) {
        if (date == null || month == null) {
            return false;
        }
        String normalized = month.trim();
        if (normalized.matches("\\d{4}-\\d{2}")) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(date);
            int y = cal.get(java.util.Calendar.YEAR);
            int m = cal.get(java.util.Calendar.MONTH) + 1;
            String value = String.format("%04d-%02d", y, m);
            return value.equals(normalized);
        }
        return false;
    }
}

class TimeUtils {
    private TimeUtils() {
    }

    public static boolean isValidTimeFormat(String time) {
        if (time == null) {
            return false;
        }
        return time.matches("([01]\\d|2[0-3]):[0-5]\\d");
    }

    public static int toMinutes(String time) {
        if (!isValidTimeFormat(time)) {
            return -1;
        }
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    public static boolean intervalsOverlapStrict(Date start1, Date end1, Date start2, Date end2) {
        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            return false;
        }
        return start1.before(end2) && start2.before(end1);
    }
}