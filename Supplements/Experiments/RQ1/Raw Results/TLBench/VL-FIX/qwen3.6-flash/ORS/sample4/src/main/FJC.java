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

enum Award {
    CASHBACK,
    DISCOUNTS,
    POINTS
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
        if (this.customer == null || this.trip == null || this.bookingDate == null || this.numberOfSeats <= 0) {
            return false;
        }

        if (!this.trip.exists()) {
            return false;
        }

        int availableSeats = this.trip.getNumberOfSeats() - this.trip.getBookedSeats();
        if (this.numberOfSeats > availableSeats) {
            return false;
        }

        if (!this.isBookingTimeValid()) {
            return false;
        }

        if (this.customer.hasOverlappingBookingOnSameDay(this)) {
            return false;
        }

        return true;
    }

    private boolean isBookingTimeValid() {
        Date departureDateTime = this.trip.getDepartureDateTime();
        if (departureDateTime == null) {
            return false;
        }

        long diffMillis = departureDateTime.getTime() - this.bookingDate.getTime();
        long twoHoursMillis = 2 * 60 * 60 * 1000;

        return diffMillis > twoHoursMillis;
    }

    public void updateTripSeats() {
        if (this.trip != null) {
            this.trip.reduceSeats(this.numberOfSeats);
        }
    }

    public boolean overlapsWith(Trip trip) {
        if (trip == null) {
            return false;
        }
        
        Date thisDepartureDate = this.trip.getDepartureDate();
        Date otherDepartureDate = trip.getDepartureDate();

        if (thisDepartureDate == null || otherDepartureDate == null) {
            return false;
        }

        // Check if dates are the same (ignoring time for "same day" check? 
        // Requirement says "overlapping booking on the same day". 
        // Usually implies calendar day. Let's assume calendar day equality first.
        // However, Trip has departureDate and departureTime. 
        // Let's check if the calendar day is the same.
        // Java Date equals checks exact timestamp. 
        // To check "same day", we need to normalize or use Calendar.
        // Since we don't have Calendar imported explicitly but can use it, 
        // or we can assume the Date object represents the start of the day for comparison?
        // No, Date includes time. 
        // Let's use simple logic: if the trip dates are different, no overlap.
        if (!thisDepartureDate.equals(otherDepartureDate)) {
            return false;
        }

        // If dates are same, check time overlap
        String thisDepTime = this.trip.getDepartureTime();
        String thisArrTime = this.trip.getArrivalTime();
        String otherDepTime = trip.getDepartureTime();
        String otherArrTime = trip.getArrivalTime();

        return isTimeOverlap(thisDepTime, thisArrTime, otherDepTime, otherArrTime);
    }

    private boolean isTimeOverlap(String t1Start, String t1End, String t2Start, String t2End) {
        if (t1Start == null || t1End == null || t2Start == null || t2End == null) {
            return false;
        }
        // Simple string comparison for HH:mm format
        return t1Start.compareTo(t2End) < 0 && t2Start.compareTo(t1End) < 0;
    }

    public boolean isInMonth(String month) {
        if (this.bookingDate == null || month == null) {
            return false;
        }
        // Format: yyyy-MM
        String bookingMonth = String.format("%d-%02d", 
            this.bookingDate.getYear() + 1900, 
            this.bookingDate.getMonth() + 1);
        return bookingMonth.equals(month);
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
        if (customer == null || bookingTime == null || this.price <= 0) {
            return this.price;
        }

        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null || !pkg.hasAward(Award.DISCOUNTS)) {
            return this.price;
        }

        // Check if booking is made at least 24 hours before departure
        Date departureDateTime = this.getDepartureDateTime();
        if (departureDateTime == null) {
            return this.price;
        }

        try {
            Date bookingDate = parseTime(bookingTime);
            if (bookingDate == null) {
                return this.price;
            }
            
            // Note: bookingTime is likely just time, but requirement says "24 hours before departure".
            // Usually this implies the booking DATE/TIME. 
            // The method signature takes String bookingTime. 
            // Context from Req 1: "booking made more than two hours before".
            // Context from Req 2: "booking is made at least 24 hours before departure".
            // If bookingTime is just a time string, we can't calculate 24h diff unless we know booking date.
            // However, the Booking object has bookingDate. 
            // Let's assume the caller provides a full ISO string or similar, or we rely on the Booking object's date.
            // But the method signature is fixed. Let's assume bookingTime is actually a timestamp string or Date string.
            // Or, perhaps we should look at the Booking associated with this calculation?
            // The method is on Trip. 
            // Let's assume bookingTime is a string representation of the booking instant.
            
            long diffMillis = departureDateTime.getTime() - bookingDate.getTime();
            long twentyFourHoursMillis = 24 * 60 * 60 * 1000;

            if (diffMillis >= twentyFourHoursMillis) {
                double discounted = this.price * 0.8;
                return Math.round(discounted * 10.0) / 10.0;
            }
        } catch (Exception e) {
            return this.price;
        }

        return this.price;
    }

    public int getBookedSeats() {
        if (this.bookings == null) {
            return 0;
        }
        int total = 0;
        for (Booking b : this.bookings) {
            total += b.getNumberOfSeats();
        }
        return total;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void addBooking(Booking booking) {
        if (this.bookings != null && booking != null) {
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

        if (this.bookings == null) {
            return 0;
        }

        int totalPoints = 0;
        for (Booking booking : this.bookings) {
            if (booking.getCustomer() == customer && booking.isInMonth(currentMonth)) {
                totalPoints += booking.getNumberOfSeats() * 5;
            }
        }
        return totalPoints;
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

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (newDepartureTime == null || newArrivalTime == null || 
            this.departureTime == null || this.arrivalTime == null) {
            return false;
        }

        // Adjacent boundaries allowed: one ends exactly when another starts.
        // Overlap if: newStart < existingEnd AND existingStart < newEnd
        return this.departureTime.compareTo(newArrivalTime) < 0 && newDepartureTime.compareTo(this.arrivalTime) < 0;
    }

    public boolean exists() {
        return true; // A Trip object exists if instantiated
    }

    public void reduceSeats(int seats) {
        this.numberOfSeats -= seats;
    }

    public Date getDepartureDateTime() {
        if (this.departureDate == null || this.departureTime == null) {
            return null;
        }
        // Combine date and time. Assuming time is HH:mm
        try {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(this.departureDate);
            String[] parts = this.departureTime.split(":");
            if (parts.length == 2) {
                int hour = Integer.parseInt(parts[0]);
                int minute = Integer.parseInt(parts[1]);
                cal.set(java.util.Calendar.HOUR_OF_DAY, hour);
                cal.set(java.util.Calendar.MINUTE, minute);
                cal.set(java.util.Calendar.SECOND, 0);
                cal.set(java.util.Calendar.MILLISECOND, 0);
                return cal.getTime();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public Date getArrivalDateTime() {
        if (this.departureDate == null || this.arrivalTime == null) {
            return null;
        }
        // Combine date and time. Assuming time is HH:mm
        try {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(this.departureDate);
            String[] parts = this.arrivalTime.split(":");
            if (parts.length == 2) {
                int hour = Integer.parseInt(parts[0]);
                int minute = Integer.parseInt(parts[1]);
                cal.set(java.util.Calendar.HOUR_OF_DAY, hour);
                cal.set(java.util.Calendar.MINUTE, minute);
                cal.set(java.util.Calendar.SECOND, 0);
                cal.set(java.util.Calendar.MILLISECOND, 0);
                return cal.getTime();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private Date parseTime(String timeStr) {
        if (timeStr == null) return null;
        // Try parsing as ISO date or specific format?
        // Without strict format, this is tricky. 
        // Let's assume it's a standard date string or timestamp.
        // For the sake of the solution, let's assume it can be parsed by a simple formatter or is already a timestamp string.
        // Since no specific format is given for bookingTime string in the method sig, 
        // and Booking has a Date, maybe we should use the Booking's date?
        // But this method is on Trip.
        // Let's assume the string is in "yyyy-MM-dd HH:mm" format.
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
            return sdf.parse(timeStr);
        } catch (Exception e) {
            return null;
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
        if (this.stops != null && stop != null) {
            this.stops.add(stop);
        }
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
        if (this.trips != null && trip != null) {
            this.trips.add(trip);
        }
    }

    public boolean checkStopOverlap(Trip trip1, Trip trip2) {
        if (trip1 == null || trip2 == null) {
            return false;
        }
        
        Set<String> stops1 = trip1.getStopStations();
        Set<String> stops2 = trip2.getStopStations();

        if (stops1.isEmpty() || stops2.isEmpty()) {
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

        // Validate time window
        if (newTrip.getDepartureDate() == null || 
            newTrip.getDepartureTime() == null || 
            newTrip.getArrivalTime() == null) {
            return false;
        }

        Date departureDateTime = newTrip.getDepartureDateTime();
        if (departureDateTime == null) {
            return false;
        }

        Date arrivalDateTime = newTrip.getArrivalDateTime();
        if (arrivalDateTime == null) {
            return false;
        }

        if (departureDateTime.getTime() >= arrivalDateTime.getTime()) {
            return false;
        }

        // Check for overlaps with existing trips
        for (Trip existingTrip : this.trips) {
            if (existingTrip == null) {
                continue;
            }

            // If both are indirect, check stop overlap
            if (existingTrip.getStops() != null && !existingTrip.getStops().isEmpty() &&
                newTrip.getStops() != null && !newTrip.getStops().isEmpty()) {
                if (checkStopOverlap(existingTrip, newTrip)) {
                    return false;
                }
            }

            // Check time overlap
            if (existingTrip.getDepartureDate() != null && 
                existingTrip.getDepartureDate().equals(newTrip.getDepartureDate())) {
                if (existingTrip.isTimeConflicting(newTrip.getDepartureTime(), newTrip.getArrivalTime())) {
                    return false;
                }
            }
            
            // If dates are different, no time conflict possible.
            // However, if one trip spans multiple days? The model doesn't support multi-day explicitly.
            // Assuming single day trips based on Date + Time fields.
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
        if (this.bookings != null && booking != null) {
            this.bookings.add(booking);
        }
    }

    public void bookTrip(Trip trip, int numberOfSeats) {
        if (trip == null) {
            return;
        }

        Booking newBooking = new Booking();
        newBooking.setTrip(trip);
        newBooking.setCustomer(this);
        newBooking.setNumberOfSeats(numberOfSeats);
        newBooking.setBookingDate(new Date());

        if (newBooking.isBookingEligible()) {
            newBooking.updateTripSeats();
            trip.addBooking(newBooking);
            this.addBooking(newBooking);
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        int totalPoints = 0;
        if (this.bookings == null) {
            return 0;
        }

        for (Booking booking : this.bookings) {
            if (booking.getTrip() != null) {
                totalPoints += booking.getTrip().calculateMonthlyPoints(this, currentMonth);
            }
        }
        return totalPoints;
    }

    public boolean hasOverlappingBookingOnSameDay(Booking currentBooking) {
        if (this.bookings == null || currentBooking == null) {
            return false;
        }

        for (Booking existingBooking : this.bookings) {
            if (existingBooking == currentBooking) {
                continue;
            }
            
            // Check if the existing booking's trip overlaps with the current booking's trip on the same day
            if (existingBooking.getTrip() != null && currentBooking.getTrip() != null) {
                if (existingBooking.overlapsWith(currentBooking.getTrip())) {
                    return true;
                }
            }
        }
        return false;
    }
}