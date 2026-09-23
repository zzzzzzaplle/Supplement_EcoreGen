import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.text.ParseException;

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
        this.bookings = new ArrayList<Booking>();
        this.stops = new ArrayList<Stop>();
    }

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null) {
            return price;
        }
        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null) {
            return price;
        }
        if (!pkg.hasAward(Award.DISCOUNTS)) {
            return price;
        }
        if (bookingTime == null) {
            return price;
        }
        if (departureDate == null || departureTime == null) {
            return price;
        }

        Date bookingDateTime = null;
        String[] formats = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy/MM/dd HH:mm", "HH:mm"};
        for (String fmt : formats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(fmt);
                Date parsed = sdf.parse(bookingTime);
                if (fmt.equals("HH:mm")) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(departureDate);
                    Calendar bookCal = Calendar.getInstance();
                    bookCal.setTime(parsed);
                    cal.set(Calendar.HOUR_OF_DAY, bookCal.get(Calendar.HOUR_OF_DAY));
                    cal.set(Calendar.MINUTE, bookCal.get(Calendar.MINUTE));
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    parsed = cal.getTime();
                }
                bookingDateTime = parsed;
                break;
            } catch (ParseException e) {
                // try next format
            }
        }

        if (bookingDateTime == null) {
            return price;
        }

        Date departureDateTime = combineDateTime(departureDate, departureTime);
        if (departureDateTime == null) {
            return price;
        }

        long diff = departureDateTime.getTime() - bookingDateTime.getTime();
        long twentyFourHours = 24L * 60L * 60L * 1000L;
        if (diff < twentyFourHours) {
            return price;
        }

        double discounted = price * 0.8;
        return Math.round(discounted * 10.0) / 10.0;
    }

    public int getBookedSeats() {
        int total = 0;
        if (bookings != null) {
            for (Booking b : bookings) {
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
        if (bookings == null) {
            bookings = new ArrayList<Booking>();
        }
        if (booking != null) {
            bookings.add(booking);
        }
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || currentMonth == null) {
            return 0;
        }
        MembershipPackage pkg = customer.getMembershipPackage();
        if (pkg == null) {
            return 0;
        }
        if (!pkg.hasAward(Award.POINTS)) {
            return 0;
        }

        List<Booking> custBookings = customer.getBookings();
        if (custBookings == null) {
            return 0;
        }

        int totalPoints = 0;
        for (Booking b : custBookings) {
            if (b == null) {
                continue;
            }
            if (b.getTrip() == this && b.isInMonth(currentMonth)) {
                totalPoints += b.getNumberOfSeats() * 5;
            }
        }
        return totalPoints;
    }

    public Set<String> getStopStations() {
        Set<String> stations = new HashSet<String>();
        if (stops != null) {
            for (Stop s : stops) {
                if (s != null && s.getStopStation() != null) {
                    stations.add(s.getStopStation());
                }
            }
        }
        return stations;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (departureTime == null || arrivalTime == null) {
            return false;
        }
        if (newDepartureTime == null || newArrivalTime == null) {
            return false;
        }

        int depMins = timeToMinutes(departureTime);
        int arrMins = timeToMinutes(arrivalTime);
        int newDepMins = timeToMinutes(newDepartureTime);
        int newArrMins = timeToMinutes(newArrivalTime);

        return !(newArrMins <= depMins || newDepMins >= arrMins);
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
        if (stops == null) {
            stops = new ArrayList<Stop>();
        }
        if (stop != null) {
            stops.add(stop);
        }
    }

    private int timeToMinutes(String time) {
        if (time == null) {
            return 0;
        }
        String[] parts = time.split(":");
        if (parts.length < 2) {
            return 0;
        }
        try {
            return Integer.parseInt(parts[0].trim()) * 60 + Integer.parseInt(parts[1].trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private Date combineDateTime(Date date, String time) {
        if (date == null || time == null) {
            return null;
        }
        String[] parts = time.split(":");
        if (parts.length < 2) {
            return null;
        }
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0].trim()));
            cal.set(Calendar.MINUTE, Integer.parseInt(parts[1].trim()));
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTime();
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
