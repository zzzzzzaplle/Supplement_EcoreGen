import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        Set<String> stopStations1 = trip1.getStopStations();
        Set<String> stopStations2 = trip2.getStopStations();
        for (String station : stopStations1) {
            if (stopStations2.contains(station)) {
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
        if (!departureTime.isEmpty() && !arrivalTime.isEmpty() && departureTime.compareTo(arrivalTime) >= 0) {
            return false;
        }
        for (Trip existingTrip : this.trips) {
            if (existingTrip.isTimeConflicting(departureTime, arrivalTime)) {
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
        this.bookings.add(booking);
    }

    public void bookTrip(Trip trip, int numberOfSeats) {
        Booking booking = new Booking();
        booking.setTrip(trip);
        booking.setNumberOfSeats(numberOfSeats);
        booking.setCustomer(this);
        booking.setBookingDate(new Date());
        if (booking.isBookingEligible()) {
            this.bookings.add(booking);
            trip.addBooking(booking);
            booking.updateTripSeats();
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (currentMonth == null) {
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
        if (customer == null || bookingTime == null || this.price <= 0) {
            return this.price;
        }
        MembershipPackage membershipPackage = customer.getMembershipPackage();
        if (membershipPackage == null || !membershipPackage.hasAward(Award.DISCOUNTS)) {
            return this.price;
        }
        try {
            String departureTime = this.getDepartureTime();
            String bookingHour = bookingTime.substring(0, 2);
            String departureHour = departureTime.substring(0, 2);
            int bookingH = Integer.parseInt(bookingHour);
            int departureH = Integer.parseInt(departureHour);
            if (departureH - bookingH >= 24) {
                double discountedPrice = this.price * 0.8;
                return Math.round(discountedPrice * 10.0) / 10.0;
            }
        } catch (Exception e) {
            return this.price;
        }
        return this.price;
    }

    public int getBookedSeats() {
        int booked = 0;
        if (this.bookings != null) {
            for (Booking booking : this.bookings) {
                booked += booking.getNumberOfSeats();
            }
        }
        return booked;
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
        if (this.departureTime == null || this.arrivalTime == null || newDepartureTime == null || newArrivalTime == null) {
            return false;
        }
        if (this.departureTime.compareTo(newArrivalTime) >= 0 || newDepartureTime.compareTo(this.arrivalTime) >= 0) {
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
        if (this.customer == null || this.trip == null || this.bookingDate == null) {
            return false;
        }
        Trip trip = this.trip;
        int availableSeats = trip.getNumberOfSeats() - trip.getBookedSeats();
        if (this.numberOfSeats > availableSeats || this.numberOfSeats <= 0) {
            return false;
        }
        if (this.customer.overlapsWith(trip)) {
            return false;
        }
        String departureTimeStr = trip.getDepartureTime();
        if (departureTimeStr == null || departureTimeStr.isEmpty()) {
            return false;
        }
        String bookingTimeStr = String.format("%02d", bookingDate.getHours()) + ":" + String.format("%02d", bookingDate.getMinutes());
        int depH = Integer.parseInt(departureTimeStr.split(":")[0]);
        int depM = Integer.parseInt(departureTimeStr.split(":")[1]);
        int bookH = Integer.parseInt(bookingTimeStr.split(":")[0]);
        int bookM = Integer.parseInt(bookingTimeStr.split(":")[1]);
        int depTotal = depH * 60 + depM;
        int bookTotal = bookH * 60 + bookM;
        if (depTotal - bookTotal <= 120) {
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
        if (this.customer == null || trip == null) {
            return false;
        }
        Date myDate = this.bookingDate;
        Date tripDate = trip.getDepartureDate();
        if (myDate == null || tripDate == null || !myDate.equals(tripDate)) {
            return false;
        }
        String myDep = String.format("%02d", myDate.getHours()) + ":" + String.format("%02d", myDate.getMinutes());
        String myArr = String.format("%02d", myDate.getHours() + 1) + ":" + String.format("%02d", myDate.getMinutes());
        String tripDep = trip.getDepartureTime();
        String tripArr = trip.getArrivalTime();
        if (myDep == null || tripDep == null) return false;
        return myDep.compareTo(tripArr) < 0 && tripDep.compareTo(myArr) < 0;
    }

    public boolean isInMonth(String month) {
        if (this.bookingDate == null || month == null) {
            return false;
        }
        String bookingMonth = String.format("%02d", this.bookingDate.getMonth() + 1);
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