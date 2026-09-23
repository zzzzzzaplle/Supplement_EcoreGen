import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;

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
        this.trips.add(trip);
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
        String newDepTime = newTrip.getDepartureTime();
        String newArrTime = newTrip.getArrivalTime();
        if (newDepTime == null || newArrTime == null) {
            return false;
        }
        if (!isValidTimeFormat(newDepTime) || !isValidTimeFormat(newArrTime)) {
            return false;
        }
        if (!isEarlierTime(newDepTime, newArrTime)) {
            return false;
        }
        for (Trip existingTrip : trips) {
            if (existingTrip == null) continue;
            String existingDepTime = existingTrip.getDepartureTime();
            String existingArrTime = existingTrip.getArrivalTime();
            if (existingDepTime == null || existingArrTime == null) continue;
            if (!isValidTimeFormat(existingDepTime) || !isValidTimeFormat(existingArrTime)) continue;
            // Check for overlap (not adjacent boundaries)
            if (isEarlierTime(newDepTime, existingArrTime) && isEarlierTime(existingDepTime, newArrTime)) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidTimeFormat(String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            sdf.setLenient(false);
            sdf.parse(time);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isEarlierTime(String time1, String time2) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date d1 = sdf.parse(time1);
            Date d2 = sdf.parse(time2);
            return d1.before(d2);
        } catch (ParseException e) {
            return false;
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
        this.bookings.add(booking);
    }

    public void bookTrip(Trip trip, int numberOfSeats) {
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
        if (currentMonth == null || currentMonth.isEmpty()) {
            return 0;
        }
        if (membershipPackage == null) {
            return 0;
        }
        if (!membershipPackage.hasAward(Award.POINTS)) {
            return 0;
        }
        int totalPoints = 0;
        for (Booking booking : bookings) {
            if (booking == null) continue;
            if (booking.isInMonth(currentMonth)) {
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
            return this.price;
        }
        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null) {
            return this.price;
        }
        if (!pkg.hasAward(Award.DISCOUNTS)) {
            return this.price;
        }
        // Check if booking is at least 24 hours before departure
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String departureDateTimeStr = new SimpleDateFormat("yyyy-MM-dd").format(this.departureDate) + " " + this.departureTime;
            Date departureDateTime = sdf.parse(departureDateTimeStr);
            Date bookingDateTime = sdf.parse(bookingTime);
            long diffMillis = departureDateTime.getTime() - bookingDateTime.getTime();
            long hoursDiff = diffMillis / (1000 * 60 * 60);
            if (hoursDiff >= 24) {
                double discounted = this.price * 0.8;
                return Math.round(discounted * 10) / 10.0;
            }
        } catch (ParseException e) {
            return this.price;
        }
        return this.price;
    }

    public int getBookedSeats() {
        int total = 0;
        for (Booking booking : bookings) {
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
        this.bookings.add(booking);
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        return customer.computeMonthlyRewardPoints(currentMonth);
    }

    public Set<String> getStopStations() {
        Set<String> stopStations = new HashSet<>();
        for (Stop stop : stops) {
            if (stop != null && stop.getStopStation() != null) {
                stopStations.add(stop.getStopStation());
            }
        }
        return stopStations;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date newDep = sdf.parse(newDepartureTime);
            Date newArr = sdf.parse(newArrivalTime);
            Date existingDep = sdf.parse(this.departureTime);
            Date existingArr = sdf.parse(this.arrivalTime);
            // Check if intervals overlap (not adjacent boundaries)
            return newDep.before(existingArr) && existingDep.before(newArr);
        } catch (ParseException e) {
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
        this.stops.add(stop);
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
        // Check trip exists (trip is not null already checked)
        // Check enough available seats
        int availableSeats = trip.getNumberOfSeats() - trip.getBookedSeats();
        if (numberOfSeats <= 0 || numberOfSeats > availableSeats) {
            return false;
        }
        // Check no overlapping booking on the same day
        Date tripDate = trip.getDepartureDate();
        if (tripDate == null) {
            return false;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(tripDate);
        int tripYear = cal.get(Calendar.YEAR);
        int tripDayOfYear = cal.get(Calendar.DAY_OF_YEAR);
        for (Booking existing : customer.getBookings()) {
            if (existing == null || existing.getTrip() == null || existing.getTrip().getDepartureDate() == null) continue;
            Calendar existingCal = Calendar.getInstance();
            existingCal.setTime(existing.getTrip().getDepartureDate());
            if (existingCal.get(Calendar.YEAR) == tripYear && existingCal.get(Calendar.DAY_OF_YEAR) == tripDayOfYear) {
                // Check time overlap
                String existingDep = existing.getTrip().getDepartureTime();
                String existingArr = existing.getTrip().getArrivalTime();
                String newDep = trip.getDepartureTime();
                String newArr = trip.getArrivalTime();
                if (existingDep != null && existingArr != null && newDep != null && newArr != null) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        Date existingDepTime = sdf.parse(existingDep);
                        Date existingArrTime = sdf.parse(existingArr);
                        Date newDepTime = sdf.parse(newDep);
                        Date newArrTime = sdf.parse(newArr);
                        if (newDepTime.before(existingArrTime) && existingDepTime.before(newArrTime)) {
                            return false;
                        }
                    } catch (ParseException e) {
                        // If time parse fails, treat as no overlap
                    }
                }
            }
        }
        // Check booking made more than 2 hours before departure
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String departureDateTimeStr = new SimpleDateFormat("yyyy-MM-dd").format(trip.getDepartureDate()) + " " + trip.getDepartureTime();
            Date departureDateTime = sdf.parse(departureDateTimeStr);
            String bookingDateTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(bookingDate);
            Date bookingDateTime = sdf.parse(bookingDateTimeStr);
            long diffMillis = departureDateTime.getTime() - bookingDateTime.getTime();
            long hoursDiff = diffMillis / (1000 * 60 * 60);
            if (hoursDiff <= 2) {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public void updateTripSeats() {
        if (trip != null) {
            int newSeats = trip.getNumberOfSeats() - this.numberOfSeats;
            trip.setNumberOfSeats(newSeats);
        }
    }

    public boolean overlapsWith(Trip trip) {
        if (this.trip == null || trip == null) {
            return false;
        }
        Date thisDate = this.trip.getDepartureDate();
        Date otherDate = trip.getDepartureDate();
        if (thisDate == null || otherDate == null) {
            return false;
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(thisDate);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(otherDate);
        if (cal1.get(Calendar.YEAR) != cal2.get(Calendar.YEAR) || cal1.get(Calendar.DAY_OF_YEAR) != cal2.get(Calendar.DAY_OF_YEAR)) {
            return false;
        }
        String thisDep = this.trip.getDepartureTime();
        String thisArr = this.trip.getArrivalTime();
        String otherDep = trip.getDepartureTime();
        String otherArr = trip.getArrivalTime();
        if (thisDep == null || thisArr == null || otherDep == null || otherArr == null) {
            return false;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date thisDepTime = sdf.parse(thisDep);
            Date thisArrTime = sdf.parse(thisArr);
            Date otherDepTime = sdf.parse(otherDep);
            Date otherArrTime = sdf.parse(otherArr);
            return thisDepTime.before(otherArrTime) && otherDepTime.before(thisArrTime);
        } catch (ParseException e) {
            return false;
        }
    }

    public boolean isInMonth(String month) {
        if (bookingDate == null || month == null || month.isEmpty()) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String bookingMonth = sdf.format(bookingDate);
        return bookingMonth.equals(month);
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