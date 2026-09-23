import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Calendar;

class User {
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
        this.trips.add(trip);
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
        Set<String> stations1 = new HashSet<>();
        for (Stop s : stops1) {
            if (s != null && s.getStopStation() != null) {
                stations1.add(s.getStopStation());
            }
        }
        for (Stop s : stops2) {
            if (s != null && s.getStopStation() != null) {
                if (stations1.contains(s.getStopStation())) {
                    return true;
                }
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
        if (depTime.compareTo(arrTime) >= 0) {
            return false;
        }
        for (Trip existingTrip : this.trips) {
            if (existingTrip == null) {
                continue;
            }
            String existingDep = existingTrip.getDepartureTime();
            String existingArr = existingTrip.getArrivalTime();
            if (existingDep == null || existingArr == null || existingDep.isEmpty() || existingArr.isEmpty()) {
                continue;
            }
            if (existingTrip.isTimeConflicting(depTime, arrTime)) {
                return false;
            }
            if (existingTrip.getStops() != null && !existingTrip.getStops().isEmpty() && newTrip.getStops() != null && !newTrip.getStops().isEmpty()) {
                if (checkStopOverlap(newTrip, existingTrip)) {
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
        return bookings;
    }

    public void addBooking(Booking booking) {
        this.bookings.add(booking);
    }

    public void bookTrip(Trip trip, int numberOfSeats) {
        if (trip == null || numberOfSeats <= 0) {
            return;
        }
        Booking booking = new Booking();
        booking.setNumberOfSeats(numberOfSeats);
        booking.setCustomer(this);
        booking.setTrip(trip);
        booking.setBookingDate(new Date());
        if (booking.isBookingEligible()) {
            this.bookings.add(booking);
            trip.addBooking(booking);
            booking.updateTripSeats();
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (currentMonth == null || currentMonth.isEmpty()) {
            return 0;
        }
        if (this.membershipPackage == null) {
            return 0;
        }
        if (!this.membershipPackage.hasAward(Award.POINTS)) {
            return 0;
        }
        int totalPoints = 0;
        for (Booking booking : this.bookings) {
            if (booking != null && booking.isInMonth(currentMonth)) {
                totalPoints += booking.getNumberOfSeats() * 5;
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
        if (customer == null || bookingTime == null || bookingTime.isEmpty()) {
            return price;
        }
        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null || !pkg.hasAward(Award.DISCOUNTS)) {
            return price;
        }
        if (!isBookingMadeAtLeast24HoursBefore(bookingTime)) {
            return price;
        }
        double discount = price * 0.20;
        double finalPrice = price - discount;
        return Math.round(finalPrice * 10.0) / 10.0;
    }

    private boolean isBookingMadeAtLeast24HoursBefore(String bookingTime) {
        if (departureTime == null || departureTime.isEmpty()) {
            return false;
        }
        String depDateStr = departureDate != null ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(departureDate) : "1970-01-01";
        String depDateTimeStr = depDateStr + " " + departureTime;
        String bookingDateTimeStr = bookingTime;
        if (bookingDateTimeStr.length() > 10) {
            bookingDateTimeStr = bookingDateTimeStr.substring(0, 10) + " " + bookingDateTimeStr.substring(11);
        } else {
            bookingDateTimeStr = bookingDateTimeStr + " 00:00:00";
        }
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date depDate = sdf.parse(depDateTimeStr);
            Date bookDate = sdf.parse(bookingDateTimeStr);
            long diffMillis = depDate.getTime() - bookDate.getTime();
            long twentyFourHoursMillis = 24 * 60 * 60 * 1000;
            return diffMillis >= twentyFourHoursMillis;
        } catch (Exception e) {
            return false;
        }
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
            this.bookings.add(booking);
        }
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || currentMonth == null || currentMonth.isEmpty()) {
            return 0;
        }
        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null || !pkg.hasAward(Award.POINTS)) {
            return 0;
        }
        int points = 0;
        for (Booking b : this.bookings) {
            if (b != null && b.getCustomer() == customer && b.isInMonth(currentMonth)) {
                points += b.getNumberOfSeats() * 5;
            }
        }
        return points;
    }

    public Set<String> getStopStations() {
        Set<String> stations = new HashSet<>();
        if (stops == null) {
            return stations;
        }
        for (Stop s : stops) {
            if (s != null && s.getStopStation() != null) {
                stations.add(s.getStopStation());
            }
        }
        return stations;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (this.departureTime == null || this.arrivalTime == null ||
            newDepartureTime == null || newArrivalTime == null) {
            return false;
        }
        if (this.departureTime.compareTo(newArrivalTime) >= 0 ||
            this.arrivalTime.compareTo(newDepartureTime) <= 0) {
            return false;
        }
        return true;
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
        if (this.customer == null || this.trip == null || this.bookingDate == null) {
            return false;
        }
        if (this.trip.getStops() == null) {
            this.trip.setStops(new ArrayList<Stop>());
        }
        if (this.trip.getBookings() == null) {
            this.trip.setBookings(new ArrayList<Booking>());
        }
        int availableSeats = this.trip.getNumberOfSeats() - this.trip.getBookedSeats();
        if (this.numberOfSeats > availableSeats) {
            return false;
        }
        if (!isBookingMadeMoreThan2HoursBeforeDeparture()) {
            return false;
        }
        if (hasOverlappingBookingOnSameDay()) {
            return false;
        }
        return true;
    }

    private boolean isBookingMadeMoreThan2HoursBeforeDeparture() {
        if (this.trip.getDepartureDate() == null || this.trip.getDepartureTime() == null) {
            return false;
        }
        String depDateStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(this.trip.getDepartureDate());
        String depDateTimeStr = depDateStr + " " + this.trip.getDepartureTime();
        String bookDateTimeStr = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.bookingDate);
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date depDate = sdf.parse(depDateTimeStr);
            Date bookDate = sdf.parse(bookDateTimeStr);
            long diffMillis = depDate.getTime() - bookDate.getTime();
            long twoHoursMillis = 2 * 60 * 60 * 1000;
            return diffMillis > twoHoursMillis;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean hasOverlappingBookingOnSameDay() {
        if (this.trip.getDepartureDate() == null) {
            return false;
        }
        String depDateStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(this.trip.getDepartureDate());
        for (Booking existingBooking : this.trip.getBookings()) {
            if (existingBooking == null || existingBooking == this) {
                continue;
            }
            if (existingBooking.getCustomer() != this.customer) {
                continue;
            }
            if (existingBooking.getTrip() == null || existingBooking.getTrip().getDepartureDate() == null) {
                continue;
            }
            String existingDateStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(existingBooking.getTrip().getDepartureDate());
            if (!depDateStr.equals(existingDateStr)) {
                continue;
            }
            if (doesTripTimesOverlap(this.trip, existingBooking.getTrip())) {
                return true;
            }
        }
        return false;
    }

    private boolean doesTripTimesOverlap(Trip t1, Trip t2) {
        if (t1.getDepartureTime() == null || t1.getArrivalTime() == null ||
            t2.getDepartureTime() == null || t2.getArrivalTime() == null) {
            return false;
        }
        String dep1 = t1.getDepartureTime();
        String arr1 = t1.getArrivalTime();
        String dep2 = t2.getDepartureTime();
        String arr2 = t2.getArrivalTime();
        if (dep1.compareTo(arr2) >= 0 || arr1.compareTo(dep2) <= 0) {
            return false;
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
        if (this.trip == null || trip == null) {
            return false;
        }
        if (this.trip.getDepartureTime() == null || this.trip.getArrivalTime() == null ||
            trip.getDepartureTime() == null || trip.getArrivalTime() == null) {
            return false;
        }
        return doesTripTimesOverlap(this.trip, trip);
    }

    public boolean isInMonth(String month) {
        if (this.bookingDate == null || month == null || month.isEmpty()) {
            return false;
        }
        String bookingMonth = new java.text.SimpleDateFormat("yyyy-MM").format(this.bookingDate);
        return bookingMonth.equals(month);
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
        if (awards == null) {
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