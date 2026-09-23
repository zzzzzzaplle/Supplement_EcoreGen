import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
            this.trips.add(trip);
        }
    }

    public boolean checkStopOverlap(Trip trip1, Trip trip2) {
        if (trip1 == null || trip2 == null) {
            return false;
        }
        Set<String> stops1 = trip1.getStopStations();
        Set<String> stops2 = trip2.getStopStations();
        if (stops1 == null || stops1.isEmpty() || stops2 == null || stops2.isEmpty()) {
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
        if (newTrip.getDepartureTime() == null || newTrip.getArrivalTime() == null) {
            return false;
        }
        if (newTrip.getDepartureTime().compareTo(newTrip.getArrivalTime()) >= 0) {
            return false;
        }
        if (this.trips == null) {
            return false;
        }
        for (Trip existing : this.trips) {
            if (existing == null) {
                continue;
            }
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
            this.addBooking(booking);
            trip.addBooking(booking);
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (currentMonth == null || this.membershipPackage == null) {
            return 0;
        }
        if (!this.membershipPackage.hasAward(Award.POINTS)) {
            return 0;
        }
        if (this.bookings == null) {
            return 0;
        }
        int totalPoints = 0;
        for (Booking b : this.bookings) {
            if (b == null) {
                continue;
            }
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

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        double originalPrice = this.price;
        if (customer == null || bookingTime == null || this.departureTime == null) {
            return roundToOneDecimal(originalPrice);
        }
        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null || !pkg.hasAward(Award.DISCOUNTS)) {
            return roundToOneDecimal(originalPrice);
        }
        // bookingTime format "yyyy-MM-dd HH:mm", departureTime "HH:mm"
        try {
            String[] dateTimeParts = bookingTime.split(" ");
            if (dateTimeParts.length < 2) {
                return roundToOneDecimal(originalPrice);
            }
            String[] bTime = dateTimeParts[1].split(":");
            String[] dTime = this.departureTime.split(":");
            int bHour = Integer.parseInt(bTime[0]);
            int bMin = Integer.parseInt(bTime[1]);
            int dHour = Integer.parseInt(dTime[0]);
            int dMin = Integer.parseInt(dTime[1]);
            int bMinutes = bHour * 60 + bMin;
            int dMinutes = dHour * 60 + dMin;
            // If same day and at least 24 hours before, apply discount
            if (dMinutes - bMinutes >= 24 * 60) {
                return roundToOneDecimal(originalPrice * 0.8);
            }
            // If different day, the booking date is earlier, need more careful check
            // For simplicity: if the day difference is more than 0, then it's >= 24 hours
            String[] bookingDate = dateTimeParts[0].split("-");
            String[] depDate = this.departureDate.toString().split(" ");
            // Use a simple check: if booking time string is much earlier (different day), apply
            // We'll do a basic check using day difference
            int dayDiff = estimateDayDifference(dateTimeParts[0], depDate);
            if (dayDiff >= 1) {
                return roundToOneDecimal(originalPrice * 0.8);
            } else if (dayDiff == 0 && dMinutes - bMinutes >= 24 * 60) {
                return roundToOneDecimal(originalPrice * 0.8);
            }
        } catch (Exception e) {
            return roundToOneDecimal(originalPrice);
        }
        return roundToOneDecimal(originalPrice);
    }

    private int estimateDayDifference(String bookingDateStr, String[] depDateParts) {
        // Simple heuristic: depDateParts from Date.toString() like "Mon Jan 01"
        // Just attempt to parse year/month/day from departureDate
        if (this.departureDate == null) {
            return 0;
        }
        Calendar dep = Calendar.getInstance();
        dep.setTime(this.departureDate);
        Calendar bk = Calendar.getInstance();
        try {
            String[] parts = bookingDateStr.split("-");
            if (parts.length == 3) {
                bk.set(Calendar.YEAR, Integer.parseInt(parts[0]));
                bk.set(Calendar.MONTH, Integer.parseInt(parts[1]) - 1);
                bk.set(Calendar.DAY_OF_MONTH, Integer.parseInt(parts[2]));
            }
        } catch (Exception e) {
            return 0;
        }
        long diffMillis = dep.getTimeInMillis() - bk.getTimeInMillis();
        long diffDays = diffMillis / (1000L * 60 * 60 * 24);
        return (int) diffDays;
    }

    private double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    public int getBookedSeats() {
        int total = 0;
        if (this.bookings != null) {
            for (Booking b : this.bookings) {
                if (b != null) {
                    total += b.getNumberOfSeats();
                }
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
        if (customer == null || currentMonth == null) {
            return 0;
        }
        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null || !pkg.hasAward(Award.POINTS)) {
            return 0;
        }
        int total = 0;
        for (Booking b : this.bookings) {
            if (b == null) {
                continue;
            }
            if (b.isInMonth(currentMonth)) {
                total += b.getNumberOfSeats() * 5;
            }
        }
        return total;
    }

    public Set<String> getStopStations() {
        Set<String> stations = new HashSet<>();
        if (this.stops != null) {
            for (Stop s : this.stops) {
                if (s != null && s.getStopStation() != null) {
                    stations.add(s.getStopStation());
                }
            }
        }
        return stations;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (this.departureTime == null || this.arrivalTime == null
                || newDepartureTime == null || newArrivalTime == null) {
            return false;
        }
        try {
            String[] thisDep = this.departureTime.split(":");
            String[] thisArr = this.arrivalTime.split(":");
            String[] newDep = newDepartureTime.split(":");
            String[] newArr = newArrivalTime.split(":");
            int thisDepMin = Integer.parseInt(thisDep[0]) * 60 + Integer.parseInt(thisDep[1]);
            int thisArrMin = Integer.parseInt(thisArr[0]) * 60 + Integer.parseInt(thisArr[1]);
            int newDepMin = Integer.parseInt(newDep[0]) * 60 + Integer.parseInt(newDep[1]);
            int newArrMin = Integer.parseInt(newArr[0]) * 60 + Integer.parseInt(newArr[1]);
            // Conflict if intervals overlap (adjacent boundaries are allowed)
            return newDepMin < thisArrMin && newArrMin > thisDepMin;
        } catch (Exception e) {
            return false;
        }
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
        if (this.numberOfSeats <= 0) {
            return false;
        }
        // Trip must still have enough available seats
        int bookedSeats = this.trip.getBookedSeats();
        int remaining = this.trip.getNumberOfSeats() - bookedSeats;
        if (this.numberOfSeats > remaining) {
            return false;
        }
        // Check booking time is more than 2 hours before departure
        if (this.trip.getDepartureTime() == null) {
            return false;
        }
        // Combine departure date and time
        if (this.trip.getDepartureDate() == null) {
            return false;
        }
        Calendar dep = Calendar.getInstance();
        dep.setTime(this.trip.getDepartureDate());
        String[] timeParts = this.trip.getDepartureTime().split(":");
        if (timeParts.length < 2) {
            return false;
        }
        try {
            dep.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
            dep.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
            dep.set(Calendar.SECOND, 0);
            dep.set(Calendar.MILLISECOND, 0);
        } catch (Exception e) {
            return false;
        }
        long depMillis = dep.getTimeInMillis();
        long bookMillis = this.bookingDate.getTime();
        long diffMillis = depMillis - bookMillis;
        long twoHoursMillis = 2L * 60 * 60 * 1000;
        if (diffMillis <= twoHoursMillis) {
            return false;
        }
        // Check no overlapping booking on the same day
        if (this.customer.getBookings() != null) {
            for (Booking existing : this.customer.getBookings()) {
                if (existing == null || existing == this) {
                    continue;
                }
                if (existing.getTrip() == null) {
                    continue;
                }
                if (existing.overlapsWith(this.trip)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void updateTripSeats() {
        if (this.trip != null) {
            int newTotal = this.trip.getNumberOfSeats() - this.numberOfSeats;
            this.trip.setNumberOfSeats(newTotal);
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
        if (otherTrip == null || this.trip == null) {
            return false;
        }
        if (this.trip.getDepartureDate() == null || otherTrip.getDepartureDate() == null) {
            return false;
        }
        Calendar c1 = Calendar.getInstance();
        c1.setTime(this.trip.getDepartureDate());
        Calendar c2 = Calendar.getInstance();
        c2.setTime(otherTrip.getDepartureDate());
        if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR)
                || c1.get(Calendar.DAY_OF_YEAR) != c2.get(Calendar.DAY_OF_YEAR)) {
            return false;
        }
        if (this.trip.getDepartureTime() == null || this.trip.getArrivalTime() == null
                || otherTrip.getDepartureTime() == null || otherTrip.getArrivalTime() == null) {
            return false;
        }
        try {
            String[] d1 = this.trip.getDepartureTime().split(":");
            String[] a1 = this.trip.getArrivalTime().split(":");
            String[] d2 = otherTrip.getDepartureTime().split(":");
            String[] a2 = otherTrip.getArrivalTime().split(":");
            int d1Min = Integer.parseInt(d1[0]) * 60 + Integer.parseInt(d1[1]);
            int a1Min = Integer.parseInt(a1[0]) * 60 + Integer.parseInt(a1[1]);
            int d2Min = Integer.parseInt(d2[0]) * 60 + Integer.parseInt(d2[1]);
            int a2Min = Integer.parseInt(a2[0]) * 60 + Integer.parseInt(a2[1]);
            return d1Min < a2Min && d2Min < a1Min;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isInMonth(String month) {
        if (this.bookingDate == null || month == null) {
            return false;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.bookingDate);
        String[] parts = month.split("-");
        if (parts.length < 2) {
            return false;
        }
        try {
            int year = Integer.parseInt(parts[0]);
            int mon = Integer.parseInt(parts[1]);
            return cal.get(Calendar.YEAR) == year && (cal.get(Calendar.MONTH) + 1) == mon;
        } catch (Exception e) {
            return false;
        }
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

enum Award {
    CASHBACK,
    DISCOUNTS,
    POINTS
}