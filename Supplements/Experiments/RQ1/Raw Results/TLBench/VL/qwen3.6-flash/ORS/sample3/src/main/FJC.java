import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

enum Award {
    CASHBACK,
    DISCOUNTS,
    POINTS
}

abstract class User {
    private String id;
    private String email;
    private String phoneNumber;

    public User() {
        this.id = "";
        this.email = "";
        this.phoneNumber = "";
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

class Stop {
    private String stopStation;

    public Stop() {
        this.stopStation = "";
    }

    public String getStopStation() {
        return stopStation;
    }

    public void setStopStation(String stopStation) {
        this.stopStation = stopStation;
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
        this.departureStation = "";
        this.arrivalStation = "";
        this.numberOfSeats = 0;
        this.departureDate = null;
        this.departureTime = "";
        this.arrivalTime = "";
        this.price = 0.0;
        this.bookings = new ArrayList<>();
        this.stops = new ArrayList<>();
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

    public int getBookedSeats() {
        int booked = 0;
        if (bookings != null) {
            for (Booking b : bookings) {
                if (b != null) {
                    booked += b.getNumberOfSeats();
                }
            }
        }
        return booked;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void addBooking(Booking booking) {
        if (booking != null) {
            this.bookings.add(booking);
        }
    }

    public Set<String> getStopStations() {
        Set<String> stations = new HashSet<>();
        if (stops != null) {
            for (Stop stop : stops) {
                if (stop != null && stop.getStopStation() != null) {
                    stations.add(stop.getStopStation());
                }
            }
        }
        return stations;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (newDepartureTime == null || newArrivalTime == null || departureTime == null || arrivalTime == null) {
            return false;
        }

        if (!isTimeValid(departureDate, departureTime, arrivalTime)) {
            return false;
        }

        Date newDeparture = parseDateTime(departureDate, newDepartureTime);
        Date newArrival = parseDateTime(departureDate, newArrivalTime);
        
        if (newDeparture == null || newArrival == null) {
            return false;
        }

        Date existingDeparture = parseDateTime(departureDate, departureTime);
        Date existingArrival = parseDateTime(departureDate, arrivalTime);

        if (existingDeparture == null || existingArrival == null) {
            return false;
        }

        // Adjacent boundaries are allowed, so strict inequality for overlap
        // Overlap exists if: newStart < existingEnd && newEnd > existingStart
        return newDeparture.before(existingArrival) && newArrival.after(existingDeparture);
    }

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null || customer.getMembershipPackage() == null || bookingTime == null) {
            return price;
        }

        Award[] awards = customer.getMembershipPackage().getAwards();
        if (awards == null) {
            return price;
        }

        boolean hasDiscountAward = false;
        for (Award award : awards) {
            if (award == Award.DISCOUNTS) {
                hasDiscountAward = true;
                break;
            }
        }

        if (!hasDiscountAward) {
            return price;
        }

        // Check if booking is made at least 24 hours before departure
        Date depDate = this.departureDate;
        String depTime = this.departureTime;
        if (depDate == null || depTime == null) {
            return price;
        }

        Date bookingDateTime = parseDateTime(depDate, bookingTime);
        Date departureDateTime = parseDateTime(depDate, depTime);

        if (bookingDateTime == null || departureDateTime == null) {
            return price;
        }

        long diffMillis = departureDateTime.getTime() - bookingDateTime.getTime();
        long hourDiff = diffMillis / (1000 * 60 * 60);

        if (hourDiff >= 24) {
            // Calculate with 1 decimal place
            double discount = price * 0.8;
            BigDecimal bd = new BigDecimal(discount).setScale(1, RoundingMode.HALF_UP);
            return bd.doubleValue();
        }

        return price;
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || customer.getMembershipPackage() == null) {
            return 0;
        }

        Award[] awards = customer.getMembershipPackage().getAwards();
        if (awards == null) {
            return 0;
        }

        boolean hasPointsAward = false;
        for (Award award : awards) {
            if (award == Award.POINTS) {
                hasPointsAward = true;
                break;
            }
        }

        if (!hasPointsAward) {
            return 0;
        }

        if (currentMonth == null || bookings == null) {
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

    private Date parseDateTime(Date date, String time) {
        if (date == null || time == null || time.isEmpty()) {
            return null;
        }
        try {
            String[] parts = time.split(":");
            if (parts.length < 2) return null;
            
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, minute);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTime();
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isTimeValid(Date date, String depTime, String arrTime) {
        Date dep = parseDateTime(date, depTime);
        Date arr = parseDateTime(date, arrTime);
        if (dep == null || arr == null) return false;
        return dep.before(arr);
    }
}

class Booking {
    private int numberOfSeats;
    private Customer customer;
    private Trip trip;
    private Date bookingDate;

    public Booking() {
        this.numberOfSeats = 0;
        this.customer = null;
        this.trip = null;
        this.bookingDate = null;
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
        if (this.customer == null || this.trip == null || this.bookingDate == null) {
            return false;
        }

        Trip trip = this.trip;
        Date departureDate = trip.getDepartureDate();
        String departureTime = trip.getDepartureTime();

        if (departureDate == null || departureTime == null) {
            return false;
        }

        int availableSeats = trip.getNumberOfSeats() - trip.getBookedSeats();
        if (this.numberOfSeats > availableSeats) {
            return false;
        }

        // Check for overlapping bookings on the same day
        if (this.customer.getBookings() != null) {
            for (Booking existingBooking : this.customer.getBookings()) {
                if (existingBooking == this) continue; // Skip self
                if (existingBooking.getTrip() != null && existingBooking.getTrip() == trip) {
                    continue; // Same trip, handled by seat count
                }
                if (existingBooking.getTrip() != null && 
                    existingBooking.getTrip().getDepartureDate() != null &&
                    existingBooking.getTrip().getDepartureDate().equals(departureDate)) {
                    
                    if (this.overlapsWith(existingBooking.getTrip()) || 
                        existingBooking.overlapsWith(trip)) {
                        return false;
                    }
                }
            }
        }

        // Check booking time vs departure time (more than 2 hours before)
        Date bookingDateTime = new Date(this.bookingDate.getTime());
        Date departureDateTime = Trip.parseDateTimeStatic(departureDate, departureTime);
        
        // Need to make parseDateTimeStatic accessible or replicate logic
        // Replicating logic here to avoid static access issues if not allowed
        if (!isMoreThanTwoHoursBefore(bookingDate, departureDate, departureTime)) {
            return false;
        }

        return true;
    }

    public void updateTripSeats() {
        if (this.trip != null) {
            // The trip's numberOfSeats is fixed as capacity, but we track booked seats.
            // The requirement says "trip seat inventory must be reduced".
            // Usually, this means decreasing available seats. 
            // Since we have getNumberOfSeats() and getBookedSeats(), 
            // let's assume setNumberOfSeats decreases the available capacity or just track booked.
            // The design model has numberOfSeats. Let's assume it represents total capacity.
            // To "reduce inventory", we typically decrease the available count.
            // However, the class only has numberOfSeats. 
            // Let's assume numberOfSeats is available seats.
            int currentBooked = this.trip.getBookedSeats();
            // We need to manually decrement if we treat numberOfSeats as capacity.
            // But getBookedSeats sums up bookings. 
            // Let's assume setNumberOfSeats is used to reduce available seats directly.
            this.trip.setNumberOfSeats(this.trip.getNumberOfSeats() - this.numberOfSeats);
        }
    }

    private boolean isMoreThanTwoHoursBefore(Date bookingDate, Date departureDate, String departureTime) {
        if (bookingDate == null || departureDate == null || departureTime == null) {
            return false;
        }
        
        Date departureDateTime = parseDateTimeStatic(departureDate, departureTime);
        if (departureDateTime == null) {
            return false;
        }

        long diffMillis = departureDateTime.getTime() - bookingDate.getTime();
        long hours = diffMillis / (1000 * 60 * 60);
        
        // Strictly more than 2 hours
        return hours > 2 || (hours == 2 && diffMillis > 2 * 60 * 60 * 1000);
    }

    public boolean overlapsWith(Trip trip) {
        if (this.trip == null || trip == null) {
            return false;
        }
        
        if (!this.trip.getDepartureDate().equals(trip.getDepartureDate())) {
            // Different days, no overlap
            return false;
        }

        String dep1 = this.trip.getDepartureTime();
        String arr1 = this.trip.getArrivalTime();
        String dep2 = trip.getDepartureTime();
        String arr2 = trip.getArrivalTime();

        Date start1 = parseDateTimeStatic(this.trip.getDepartureDate(), dep1);
        Date end1 = parseDateTimeStatic(this.trip.getDepartureDate(), arr1);
        Date start2 = parseDateTimeStatic(trip.getDepartureDate(), dep2);
        Date end2 = parseDateTimeStatic(trip.getDepartureDate(), arr2);

        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            return false;
        }

        // Overlap if start1 < end2 and start2 < end1
        return start1.before(end2) && start2.before(end1);
    }

    public boolean isInMonth(String month) {
        if (this.bookingDate == null || month == null || month.isEmpty()) {
            return false;
        }
        
        // Assuming month format is "YYYY-MM"
        try {
            SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
            String bookingMonth = monthFormat.format(this.bookingDate);
            return bookingMonth.equals(month);
        } catch (Exception e) {
            return false;
        }
    }

    private static Date parseDateTimeStatic(Date date, String time) {
        if (date == null || time == null || time.isEmpty()) {
            return null;
        }
        try {
            String[] parts = time.split(":");
            if (parts.length < 2) return null;
            
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, minute);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTime();
        } catch (Exception e) {
            return null;
        }
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

class Driver extends User {
    private List<Trip> trips;

    public Driver() {
        super();
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

        Set<String> stations1 = new HashSet<>();
        for (Stop stop : stops1) {
            if (stop != null && stop.getStopStation() != null) {
                stations1.add(stop.getStopStation());
            }
        }

        for (Stop stop : stops2) {
            if (stop != null && stop.getStopStation() != null) {
                if (stations1.contains(stop.getStopStation())) {
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
        Date depDate = newTrip.getDepartureDate();

        if (depTime == null || arrTime == null || depDate == null) {
            return false;
        }

        // Validate time window: departure before arrival
        if (!Trip.isTimeValidStatic(depDate, depTime, arrTime)) {
            return false;
        }

        // Check overlap with existing trips
        for (Trip existingTrip : this.trips) {
            if (existingTrip == null) {
                continue;
            }

            // If indirect trips, check stop overlap
            List<Stop> newStops = newTrip.getStops();
            List<Stop> existingStops = existingTrip.getStops();
            
            boolean newIsIndirect = newStops != null && !newStops.isEmpty();
            boolean existingIsIndirect = existingStops != null && !existingStops.isEmpty();

            if (newIsIndirect || existingIsIndirect) {
                if (checkStopOverlap(newTrip, existingTrip)) {
                    return false;
                }
            }

            // Check time conflict
            if (newTrip.isTimeConflicting(depTime, arrTime)) {
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
        super();
        this.membershipPackage = null;
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
        if (trip == null) return;

        Booking booking = new Booking();
        booking.setCustomer(this);
        booking.setTrip(trip);
        booking.setNumberOfSeats(numberOfSeats);
        booking.setBookingDate(new Date()); // Current time

        if (booking.isBookingEligible()) {
            this.addBooking(booking);
            trip.addBooking(booking);
            booking.updateTripSeats();
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (this.bookings == null || currentMonth == null) {
            return 0;
        }
        
        int totalPoints = 0;
        for (Booking booking : this.bookings) {
            if (booking != null && booking.isInMonth(currentMonth)) {
                // Check if customer has POINTS award
                if (this.membershipPackage != null && this.membershipPackage.hasAward(Award.POINTS)) {
                    totalPoints += booking.getNumberOfSeats() * 5;
                }
            }
        }
        return totalPoints;
    }
}

class TripHelper {
    public static boolean isTimeValidStatic(Date date, String depTime, String arrTime) {
        Date dep = parseDateTimeStatic(date, depTime);
        Date arr = parseDateTimeStatic(date, arrTime);
        if (dep == null || arr == null) return false;
        return dep.before(arr);
    }

    private static Date parseDateTimeStatic(Date date, String time) {
        if (date == null || time == null || time.isEmpty()) {
            return null;
        }
        try {
            String[] parts = time.split(":");
            if (parts.length < 2) return null;
            
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, minute);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTime();
        } catch (Exception e) {
            return null;
        }
    }
}