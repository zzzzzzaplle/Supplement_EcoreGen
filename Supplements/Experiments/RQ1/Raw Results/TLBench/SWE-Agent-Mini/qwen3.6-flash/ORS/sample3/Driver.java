import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class Driver extends User {
    private List<Trip> trips;

    public Driver() {
        this.trips = new ArrayList<>();
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void addTrip(Trip trip) {
        if (trip != null && trips != null) {
            trips.add(trip);
        }
    }

    public boolean checkStopOverlap(Trip trip1, Trip trip2) {
        if (trip1 == null || trip2 == null) {
            return false;
        }
        Set<String> stations1 = trip1.getStopStations();
        Set<String> stations2 = trip2.getStopStations();
        if (stations1 == null || stations1.isEmpty() || stations2 == null || stations2.isEmpty()) {
            return false;
        }
        for (String station : stations1) {
            if (stations2.contains(station)) {
                return true;
            }
        }
        return false;
    }

    public boolean canPostTrip(Trip newTrip) {
        if (newTrip == null) {
            return false;
        }
        if (newTrip.getDepartureDate() == null || newTrip.getDepartureTime() == null || newTrip.getArrivalTime() == null) {
            return false;
        }
        if (!isValidTimeWindow(newTrip.getDepartureTime(), newTrip.getArrivalTime())) {
            return false;
        }
        if (compareTime(newTrip.getDepartureTime(), newTrip.getArrivalTime()) >= 0) {
            return false;
        }
        if (trips != null) {
            for (Trip existingTrip : trips) {
                if (existingTrip == null) {
                    continue;
                }
                if (existingTrip.getDepartureDate() == null || existingTrip.getDepartureTime() == null || existingTrip.getArrivalTime() == null) {
                    continue;
                }
                if (!isSameDay(existingTrip.getDepartureDate(), newTrip.getDepartureDate())) {
                    continue;
                }
                if (existingTrip.isTimeConflicting(newTrip.getDepartureTime(), newTrip.getArrivalTime())) {
                    return false;
                }
                if (newTrip.getStops() != null && !newTrip.getStops().isEmpty() && existingTrip.getStops() != null && !existingTrip.getStops().isEmpty()) {
                    if (checkStopOverlap(newTrip, existingTrip)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean isSameDay(java.util.Date d1, java.util.Date d2) {
        java.util.Calendar c1 = java.util.Calendar.getInstance();
        c1.setTime(d1);
        java.util.Calendar c2 = java.util.Calendar.getInstance();
        c2.setTime(d2);
        return c1.get(java.util.Calendar.YEAR) == c2.get(java.util.Calendar.YEAR)
            && c1.get(java.util.Calendar.MONTH) == c2.get(java.util.Calendar.MONTH)
            && c1.get(java.util.Calendar.DAY_OF_MONTH) == c2.get(java.util.Calendar.DAY_OF_MONTH);
    }

    private boolean isValidTimeWindow(String departureTime, String arrivalTime) {
        if (departureTime == null || arrivalTime == null) {
            return false;
        }
        return departureTime.matches("^([01]\\d|2[0-3]):[0-5]\\d$") && arrivalTime.matches("^([01]\\d|2[0-3]):[0-5]\\d$");
    }

    private int compareTime(String time1, String time2) {
        if (time1 == null || time2 == null) {
            return 0;
        }
        return time1.compareTo(time2);
    }
}
