import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public List<Booking> getBookings() {
        return bookings;
    }

    public void addBooking(Booking booking) {
        this.bookings.add(booking);
    }

    public List<Stop> getStops() {
        return stops;
    }

    public void addStop(Stop stop) {
        this.stops.add(stop);
    }

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null || bookingTime == null) {
            return price;
        }
        MembershipPackage mp = customer.getMembershipPackage();
        if (mp == null || !mp.hasAward(Award.DISCOUNTS)) {
            return price;
        }
        if (!isMoreThan24HoursBeforeDeparture(bookingTime)) {
            return price;
        }
        return Math.round(price * 0.8 * 10.0) / 10.0;
    }

    private boolean isMoreThan24HoursBeforeDeparture(String bookingTimeStr) {
        if (departureDate == null || departureTime == null || bookingTimeStr == null) {
            return false;
        }
        try {
            String[] depParts = departureTime.split(":");
            int depHour = Integer.parseInt(depParts[0]);
            int depMin = Integer.parseInt(depParts[1]);

            String[] bookParts = bookingTimeStr.split(":");
            int bookHour = Integer.parseInt(bookParts[0]);
            int bookMin = Integer.parseInt(bookParts[1]);

            Calendar depCal = Calendar.getInstance();
            depCal.setTime(departureDate);
            depCal.set(Calendar.HOUR_OF_DAY, depHour);
            depCal.set(Calendar.MINUTE, depMin);
            depCal.set(Calendar.SECOND, 0);
            depCal.set(Calendar.MILLISECOND, 0);

            Calendar bookCal = Calendar.getInstance();
            bookCal.setTime(departureDate);
            bookCal.set(Calendar.HOUR_OF_DAY, bookHour);
            bookCal.set(Calendar.MINUTE, bookMin);
            bookCal.set(Calendar.SECOND, 0);
            bookCal.set(Calendar.MILLISECOND, 0);

            long diffMillis = depCal.getTimeInMillis() - bookCal.getTimeInMillis();
            long diffHours = diffMillis / (1000 * 60 * 60);
            return diffHours > 24;
        } catch (Exception e) {
            return false;
        }
    }

    public int getBookedSeats() {
        int total = 0;
        for (Booking b : bookings) {
            total += b.getNumberOfSeats();
        }
        return total;
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || currentMonth == null) {
            return 0;
        }
        MembershipPackage mp = customer.getMembershipPackage();
        if (mp == null || !mp.hasAward(Award.POINTS)) {
            return 0;
        }
        int points = 0;
        for (Booking b : bookings) {
            if (b.getCustomer() == customer && b.isInMonth(currentMonth)) {
                points += b.getNumberOfSeats() * 5;
            }
        }
        return points;
    }

    public Set<String> getStopStations() {
        Set<String> stationSet = new HashSet<>();
        if (stops != null) {
            for (Stop s : stops) {
                stationSet.add(s.getStopStation());
            }
        }
        return stationSet;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (this.departureTime == null || this.arrivalTime == null ||
            newDepartureTime == null || newArrivalTime == null) {
            return false;
        }
        try {
            String[] dep1Parts = this.departureTime.split(":");
            int dep1Hour = Integer.parseInt(dep1Parts[0]);
            int dep1Min = Integer.parseInt(dep1Parts[1]);

            String[] arr1Parts = this.arrivalTime.split(":");
            int arr1Hour = Integer.parseInt(arr1Parts[0]);
            int arr1Min = Integer.parseInt(arr1Parts[1]);

            String[] dep2Parts = newDepartureTime.split(":");
            int dep2Hour = Integer.parseInt(dep2Parts[0]);
            int dep2Min = Integer.parseInt(dep2Parts[1]);

            String[] arr2Parts = newArrivalTime.split(":");
            int arr2Hour = Integer.parseInt(arr2Parts[0]);
            int arr2Min = Integer.parseInt(arr2Parts[1]);

            int thisDepMinutes = dep1Hour * 60 + dep1Min;
            int thisArrMinutes = arr1Hour * 60 + arr1Min;
            int newDepMinutes = dep2Hour * 60 + dep2Min;
            int newArrMinutes = arr2Hour * 60 + arr2Min;

            if (newDepMinutes < thisDepMinutes) {
                if (newArrMinutes > thisDepMinutes) {
                    return true;
                }
            } else if (newDepMinutes > thisArrMinutes) {
                return true;
            } else {
                if (newDepMinutes >= thisDepMinutes && newDepMinutes < thisArrMinutes) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}