import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
            trips.add(trip);
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
        for (String s : stops1) {
            if (s != null && stops2.contains(s)) {
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
        if (timeToMinutes(depTime) >= timeToMinutes(arrTime)) {
            return false;
        }
        if (trips == null || trips.isEmpty()) {
            return true;
        }
        for (Trip existing : trips) {
            if (existing == null) {
                continue;
            }
            String exDep = existing.getDepartureTime();
            String exArr = existing.getArrivalTime();
            if (exDep == null || exArr == null || exDep.isEmpty() || exArr.isEmpty()) {
                continue;
            }
            if (timeToMinutes(depTime) < timeToMinutes(exArr) && timeToMinutes(arrTime) > timeToMinutes(exDep)) {
                return false;
            }
        }
        return true;
    }

    private int timeToMinutes(String time) {
        if (time == null) {
            return 0;
        }
        String[] parts = time.split(":");
        int h = 0, m = 0;
        try {
            h = Integer.parseInt(parts[0]);
            if (parts.length > 1) {
                m = Integer.parseInt(parts[1]);
            }
        } catch (Exception e) {
            return 0;
        }
        return h * 60 + m;
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
            bookings.add(booking);
        }
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
            bookings.add(booking);
            trip.addBooking(booking);
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (membershipPackage == null || !membershipPackage.hasAward(Award.POINTS)) {
            return 0;
        }
        if (bookings == null || bookings.isEmpty() || currentMonth == null) {
            return 0;
        }
        int totalPoints = 0;
        for (Booking b : bookings) {
            if (b != null && b.isInMonth(currentMonth)) {
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

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null || customer.getMembershipPackage() == null || bookingTime == null) {
            return roundToOneDecimal(price);
        }
        if (!customer.getMembershipPackage().hasAward(Award.DISCOUNTS)) {
            return roundToOneDecimal(price);
        }
        String depTime = this.departureTime;
        if (depTime == null) {
            return roundToOneDecimal(price);
        }
        int bookingMins = timeToMinutes(bookingTime);
        int depMins = timeToMinutes(depTime);
        if (depMins - bookingMins >= 24 * 60) {
            return roundToOneDecimal(price * 0.8);
        }
        return roundToOneDecimal(price);
    }

    private double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private int timeToMinutes(String time) {
        if (time == null) {
            return 0;
        }
        String[] parts = time.split(":");
        int h = 0, m = 0;
        try {
            h = Integer.parseInt(parts[0]);
            if (parts.length > 1) {
                m = Integer.parseInt(parts[1]);
            }
        } catch (Exception e) {
            return 0;
        }
        return h * 60 + m;
    }

    public int getBookedSeats() {
        if (bookings == null) {
            return 0;
        }
        int total = 0;
        for (Booking b : bookings) {
            if (b != null) {
                total += b.getNumberOfSeats();
            }
        }
        return total;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void addBooking(Booking booking) {
        if (booking != null) {
            bookings.add(booking);
        }
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null) {
            return 0;
        }
        return customer.computeMonthlyRewardPoints(currentMonth);
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
        if (newDepartureTime == null || newArrivalTime == null) {
            return false;
        }
        if (departureTime == null || arrivalTime == null) {
            return false;
        }
        int newDep = timeToMinutes(newDepartureTime);
        int newArr = timeToMinutes(newArrivalTime);
        int exDep = timeToMinutes(departureTime);
        int exArr = timeToMinutes(arrivalTime);
        return newDep < exArr && newArr > exDep;
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
            stops.add(stop);
        }
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
        if (trip.getNumberOfSeats() - trip.getBookedSeats() < numberOfSeats) {
            return false;
        }
        if (customer.getBookings() != null) {
            for (Booking b : customer.getBookings()) {
                if (b != null && b != this && b.overlapsWith(trip)) {
                    return false;
                }
            }
        }
        if (trip.getDepartureTime() == null) {
            return false;
        }
        int bookingMins = timeToMinutesFromDate(bookingDate);
        int depMins = timeToMinutes(trip.getDepartureTime());
        if (depMins - bookingMins <= 120) {
            return false;
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
        if (this.trip == null || otherTrip == null) {
            return false;
        }
        Date d1 = this.trip.getDepartureDate();
        Date d2 = otherTrip.getDepartureDate();
        if (d1 == null || d2 == null) {
            return false;
        }
        if (!isSameDay(d1, d2)) {
            return false;
        }
        String t1Dep = this.trip.getDepartureTime();
        String t1Arr = this.trip.getArrivalTime();
        String t2Dep = otherTrip.getDepartureTime();
        String t2Arr = otherTrip.getArrivalTime();
        if (t1Dep == null || t1Arr == null || t2Dep == null || t2Arr == null) {
            return false;
        }
        int a1 = timeToMinutes(t1Dep);
        int b1 = timeToMinutes(t1Arr);
        int a2 = timeToMinutes(t2Dep);
        int b2 = timeToMinutes(t2Arr);
        return a1 < b2 && b1 > a2;
    }

    private boolean isSameDay(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            return false;
        }
        java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return fmt.format(d1).equals(fmt.format(d2));
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null) {
            return false;
        }
        java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("yyyy-MM");
        String bookingMonth = fmt.format(bookingDate);
        return bookingMonth.equals(month);
    }

    private int timeToMinutesFromDate(Date d) {
        if (d == null) {
            return 0;
        }
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(d);
        return cal.get(java.util.Calendar.HOUR_OF_DAY) * 60 + cal.get(java.util.Calendar.MINUTE);
    }

    private int timeToMinutes(String time) {
        if (time == null) {
            return 0;
        }
        String[] parts = time.split(":");
        int h = 0, m = 0;
        try {
            h = Integer.parseInt(parts[0]);
            if (parts.length > 1) {
                m = Integer.parseInt(parts[1]);
            }
        } catch (Exception e) {
            return 0;
        }
        return h * 60 + m;
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