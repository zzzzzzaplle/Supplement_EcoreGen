import java.util.*;
import java.text.SimpleDateFormat;
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
        
        Set<String> stops1 = trip1.getStopStations();
        Set<String> stops2 = trip2.getStopStations();
        
        if (stops1 == null || stops1.isEmpty() || stops2 == null || stops2.isEmpty()) {
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
        if (newTrip == null) {
            return false;
        }
        
        String departureTime = newTrip.getDepartureTime();
        String arrivalTime = newTrip.getArrivalTime();
        
        if (departureTime == null || arrivalTime == null) {
            return false;
        }
        
        if (!departureTime.equals(arrivalTime) && isTimeBefore(departureTime, arrivalTime)) {
            // Valid time window
        } else {
            return false;
        }
        
        for (Trip existingTrip : this.trips) {
            if (existingTrip == null) continue;
            
            if (existingTrip.isTimeConflicting(departureTime, arrivalTime)) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean isTimeBefore(String t1, String t2) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date d1 = sdf.parse(t1);
            Date d2 = sdf.parse(t2);
            return d1.before(d2);
        } catch (Exception e) {
            return t1.compareTo(t2) < 0;
        }
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
            this.bookings.add(booking);
            trip.addBooking(booking);
            booking.updateTripSeats();
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (this == null || currentMonth == null || currentMonth.isEmpty()) {
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
        
        if (customer.getMembershipPackage() == null) {
            return price;
        }
        
        if (!customer.getMembershipPackage().hasAward(Award.DISCOUNTS)) {
            return price;
        }
        
        // Check if booking is at least 24 hours before departure
        if (departureDate == null) {
            return price;
        }
        
        try {
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
            
            String departureDateStr = sdfDate.format(departureDate);
            String departureDateTimeStr = departureDateStr + " " + departureTime;
            Date departureDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(departureDateTimeStr);
            
            Date bookingDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(bookingTime);
            
            long diffInMillies = departureDateTime.getTime() - bookingDateTime.getTime();
            long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillies);
            
            if (diffInHours >= 24) {
                double discountedPrice = price * 0.8;
                return Math.round(discountedPrice * 10.0) / 10.0;
            }
        } catch (Exception e) {
            return price;
        }
        
        return price;
    }

    public int getBookedSeats() {
        int booked = 0;
        for (Booking booking : bookings) {
            if (booking != null) {
                booked += booking.getNumberOfSeats();
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

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || currentMonth == null || currentMonth.isEmpty()) {
            return 0;
        }
        if (customer.getMembershipPackage() == null) {
            return 0;
        }
        if (!customer.getMembershipPackage().hasAward(Award.POINTS)) {
            return 0;
        }
        
        int totalPoints = 0;
        for (Booking booking : bookings) {
            if (booking != null && booking.isInMonth(currentMonth)) {
                totalPoints += booking.getNumberOfSeats() * 5;
            }
        }
        return totalPoints;
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
        if (this.departureTime == null || this.arrivalTime == null || 
            newDepartureTime == null || newArrivalTime == null) {
            return false;
        }
        
        // Parse times
        double start1 = parseTimeToDouble(this.departureTime);
        double end1 = parseTimeToDouble(this.arrivalTime);
        double start2 = parseTimeToDouble(newDepartureTime);
        double end2 = parseTimeToDouble(newArrivalTime);
        
        // Two intervals [s1, e1] and [s2, e2] overlap if s1 < e2 and s2 < e1
        // Adjacent boundaries (end1 == start2 or start1 == end2) are allowed (not overlapping)
        if (start1 < end2 && start2 < end1) {
            return true;
        }
        
        return false;
    }
    
    private double parseTimeToDouble(String time) {
        try {
            String[] parts = time.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            return hours + (minutes / 60.0);
        } catch (Exception e) {
            return 0.0;
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
        
        // Trip must exist (it's not null, checked above)
        // Enough seats must be available
        int availableSeats = this.trip.getNumberOfSeats() - this.trip.getBookedSeats();
        if (this.numberOfSeats > availableSeats) {
            return false;
        }
        
        // Check for overlapping bookings on the same day
        Date tripDepartureDate = this.trip.getDepartureDate();
        if (tripDepartureDate == null) {
            return false;
        }
        
        Calendar bookingCal = Calendar.getInstance();
        bookingCal.setTime(this.bookingDate);
        Calendar tripCal = Calendar.getInstance();
        tripCal.setTime(tripDepartureDate);
        
        // Same day check
        if (bookingCal.get(Calendar.YEAR) != tripCal.get(Calendar.YEAR) ||
            bookingCal.get(Calendar.MONTH) != tripCal.get(Calendar.MONTH) ||
            bookingCal.get(Calendar.DAY_OF_MONTH) != tripCal.get(Calendar.DAY_OF_MONTH)) {
            // Not same day, so no overlap possible on same day
        } else {
            // Same day, check for overlapping bookings
            List<Booking> existingBookings = this.customer.getBookings();
            for (Booking existingBooking : existingBookings) {
                if (existingBooking == this) continue; // Skip current booking
                if (existingBooking.getTrip() != null && existingBooking.getTrip().getDepartureDate() != null) {
                    Calendar existingTripCal = Calendar.getInstance();
                    existingTripCal.setTime(existingBooking.getTrip().getDepartureDate());
                    
                    if (bookingCal.get(Calendar.YEAR) == existingTripCal.get(Calendar.YEAR) &&
                        bookingCal.get(Calendar.MONTH) == existingTripCal.get(Calendar.MONTH) &&
                        bookingCal.get(Calendar.DAY_OF_MONTH) == existingTripCal.get(Calendar.DAY_OF_MONTH)) {
                        
                        // Check if times overlap
                        if (this.overlapsWith(existingBooking.getTrip())) {
                            return false;
                        }
                    }
                }
            }
        }
        
        // Booking must be made more than two hours before departure
        Date tripDepartureDateTime = getTripDepartureDateTime(this.trip);
        if (tripDepartureDateTime != null) {
            long diffInMillies = tripDepartureDateTime.getTime() - this.bookingDate.getTime();
            long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillies);
            // Must be strictly more than 2 hours
            if (diffInHours <= 2) {
                return false;
            }
        }
        
        return true;
    }
    
    private Date getTripDepartureDateTime(Trip trip) {
        if (trip == null || trip.getDepartureDate() == null || trip.getDepartureTime() == null) {
            return null;
        }
        try {
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
            String dateStr = sdfDate.format(trip.getDepartureDate());
            String dateTimeStr = dateStr + " " + trip.getDepartureTime();
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateTimeStr);
        } catch (Exception e) {
            return null;
        }
    }

    public void updateTripSeats() {
        if (this.trip != null) {
            // Seats are reduced when booking is created, handled in Customer.bookTrip
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
        
        // Get departure times
        String thisDepartureTime = this.trip.getDepartureTime();
        String thisArrivalTime = this.trip.getArrivalTime();
        String otherDepartureTime = trip.getDepartureTime();
        String otherArrivalTime = trip.getArrivalTime();
        
        if (thisDepartureTime == null || thisArrivalTime == null || 
            otherDepartureTime == null || otherArrivalTime == null) {
            return false;
        }
        
        double start1 = parseTimeToDouble(thisDepartureTime);
        double end1 = parseTimeToDouble(thisArrivalTime);
        double start2 = parseTimeToDouble(otherDepartureTime);
        double end2 = parseTimeToDouble(otherArrivalTime);
        
        // Overlap if s1 < e2 and s2 < e1
        if (start1 < end2 && start2 < end1) {
            return true;
        }
        
        return false;
    }
    
    private double parseTimeToDouble(String time) {
        try {
            String[] parts = time.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            return hours + (minutes / 60.0);
        } catch (Exception e) {
            return 0.0;
        }
    }

    public boolean isInMonth(String month) {
        if (this.bookingDate == null || month == null || month.isEmpty()) {
            return false;
        }
        
        try {
            SimpleDateFormat sdfMonth = new SimpleDateFormat("yyyy-MM");
            Date monthDate = sdfMonth.parse(month);
            
            Calendar monthCal = Calendar.getInstance();
            monthCal.setTime(monthDate);
            
            Calendar bookingCal = Calendar.getInstance();
            bookingCal.setTime(this.bookingDate);
            
            return (monthCal.get(Calendar.YEAR) == bookingCal.get(Calendar.YEAR) &&
                    monthCal.get(Calendar.MONTH) == bookingCal.get(Calendar.MONTH));
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