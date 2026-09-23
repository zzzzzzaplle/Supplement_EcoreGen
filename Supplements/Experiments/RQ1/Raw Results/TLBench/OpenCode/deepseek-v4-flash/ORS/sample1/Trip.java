import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class Trip {
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
        if (customer == null || bookingTime == null) {
            return this.price;
        }
        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null || !pkg.hasAward(Award.DISCOUNTS)) {
            return this.price;
        }
        if (!isAtLeast24HoursBefore(bookingTime, getDepartureTime())) {
            return this.price;
        }
        double discounted = this.price * 0.8;
        return Math.round(discounted * 10) / 10.0;
    }

    private boolean isAtLeast24HoursBefore(String bookingTime, String departureTime) {
        if (bookingTime == null || departureTime == null) return false;
        String[] bookingParts = bookingTime.split(":");
        String[] departureParts = departureTime.split(":");
        if (bookingParts.length < 2 || departureParts.length < 2) return false;
        try {
            int bookingHour = Integer.parseInt(bookingParts[0]);
            int bookingMin = Integer.parseInt(bookingParts[1]);
            int departureHour = Integer.parseInt(departureParts[0]);
            int departureMin = Integer.parseInt(departureParts[1]);
            int totalBookingMinutes = bookingHour * 60 + bookingMin;
            int totalDepartureMinutes = departureHour * 60 + departureMin;
            return (totalDepartureMinutes - totalBookingMinutes) >= 1440;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public int getBookedSeats() {
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
        this.bookings.add(booking);
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || currentMonth == null) return 0;
        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null || !pkg.hasAward(Award.POINTS)) return 0;
        int totalPoints = 0;
        for (Booking b : bookings) {
            if (b != null && b.getCustomer() == customer) {
                boolean validMonth = false;
                Date bDate = b.getBookingDate();
                if (bDate != null) {
                    String[] parts = currentMonth.split("-");
                    if (parts.length == 2) {
                        try {
                            int month = Integer.parseInt(parts[0]);
                            int year = Integer.parseInt(parts[1]);
                            java.util.Calendar cal = java.util.Calendar.getInstance();
                            cal.setTime(bDate);
                            int bMonth = cal.get(java.util.Calendar.MONTH) + 1;
                            int bYear = cal.get(java.util.Calendar.YEAR);
                            if (bMonth == month && bYear == year) {
                                validMonth = true;
                            }
                        } catch (NumberFormatException e) {
                        }
                    }
                }
                if (validMonth) {
                    totalPoints += b.getNumberOfSeats() * 5;
                }
            }
        }
        return totalPoints;
    }

    public java.util.Set<String> getStopStations() {
        java.util.Set<String> stationSet = new java.util.HashSet<>();
        if (stops != null) {
            for (Stop s : stops) {
                if (s != null && s.getStopStation() != null) {
                    stationSet.add(s.getStopStation());
                }
            }
        }
        return stationSet;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (newDepartureTime == null || newArrivalTime == null) return false;
        return newDepartureTime.compareTo(newArrivalTime) < 0;
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
