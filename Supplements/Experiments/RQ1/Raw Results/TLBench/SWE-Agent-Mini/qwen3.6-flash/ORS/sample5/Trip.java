import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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

        String bookingTimeLower = bookingTime.toLowerCase().trim();
        boolean hasDiscountAward = false;
        if (customer.getMembershipPackage() != null) {
            hasDiscountAward = customer.getMembershipPackage().hasAward(Award.DISCOUNTS);
        }

        if (!hasDiscountAward) {
            return price;
        }

        // Check if booking is at least 24 hours before departure
        if (!isAtLeast24HoursBeforeDeparture(bookingTimeLower)) {
            return price;
        }

        // Apply 20 percent discount, keep one decimal place
        double discountedPrice = price * 0.8;
        return Math.round(discountedPrice * 10.0) / 10.0;
    }

    private boolean isAtLeast24HoursBeforeDeparture(String bookingTimeLower) {
        if (departureDate == null || departureTime == null) {
            return false;
        }
        try {
            Calendar departureCal = Calendar.getInstance();
            departureCal.setTime(departureDate);
            String[] parts = departureTime.split(":");
            departureCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
            if (parts.length >= 2) {
                departureCal.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
            } else {
                departureCal.set(Calendar.MINUTE, 0);
            }
            departureCal.set(Calendar.SECOND, 0);
            departureCal.set(Calendar.MILLISECOND, 0);

            Calendar cutoffCal = (Calendar) departureCal.clone();
            cutoffCal.add(Calendar.HOUR_OF_DAY, -24);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            Date bookingDate = sdf.parse(bookingTimeLower);
            if (bookingDate == null) {
                return false;
            }

            return bookingDate.getTime() <= cutoffCal.getTimeInMillis();
        } catch (Exception e) {
            return false;
        }
    }

    public int getBookedSeats() {
        int count = 0;
        if (bookings != null) {
            for (Booking b : bookings) {
                if (b != null) {
                    count += b.getNumberOfSeats();
                }
            }
        }
        return count;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void addBooking(Booking booking) {
        if (booking != null) {
            if (!bookings.contains(booking)) {
                bookings.add(booking);
            }
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
        if (bookings != null) {
            for (Booking b : bookings) {
                if (b != null && b.isInMonth(currentMonth)) {
                    totalPoints += b.getNumberOfSeats() * 5;
                }
            }
        }
        return totalPoints;
    }

    public Set<String> getStopStations() {
        Set<String> stations = new HashSet<>();
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
        if (this.departureTime == null || this.arrivalTime == null ||
            newDepartureTime == null || newArrivalTime == null) {
            return false;
        }
        String thisStart = toComparableTime(this.departureDate, this.departureTime);
        String thisEnd = toComparableTime(this.departureDate, this.arrivalTime);
        String newStart = toComparableTime(this.departureDate, newDepartureTime);
        String newEnd = toComparableTime(this.departureDate, newArrivalTime);

        if (thisStart.isEmpty() || thisEnd.isEmpty() || newStart.isEmpty() || newEnd.isEmpty()) {
            return false;
        }

        // Adjacent boundaries are allowed (not conflicting)
        // Conflict if: newStart < thisEnd AND newEnd > thisStart
        boolean newStartBeforeThisEnd = newStart.compareTo(thisEnd) < 0;
        boolean newEndAfterThisStart = newEnd.compareTo(thisStart) > 0;

        return newStartBeforeThisEnd && newEndAfterThisStart;
    }

    private String toComparableTime(Date date, String time) {
        if (date == null || time == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm", Locale.US);
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            String[] parts = time.split(":");
            if (parts.length >= 2) {
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
                if (parts.length >= 3) {
                    cal.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
                }
            }
            return sdf.format(cal.getTime());
        } catch (Exception e) {
            return "";
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
            stops.add(stop);
        }
    }
}
