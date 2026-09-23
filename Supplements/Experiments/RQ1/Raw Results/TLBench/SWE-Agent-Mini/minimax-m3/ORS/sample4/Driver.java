import java.util.ArrayList;
import java.util.List;

public class Driver extends User {
    private List<Trip> trips;

    public Driver() {
        this.trips = new ArrayList<Trip>();
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void addTrip(Trip trip) {
        if (trips == null) {
            trips = new ArrayList<Trip>();
        }
        trips.add(trip);
    }

    public boolean checkStopOverlap(Trip trip1, Trip trip2) {
        if (trip1 == null || trip2 == null) {
            return false;
        }
        if (trip1.getStops() == null || trip1.getStops().isEmpty()) {
            return false;
        }
        if (trip2.getStops() == null || trip2.getStops().isEmpty()) {
            return false;
        }
        for (Stop s1 : trip1.getStops()) {
            if (s1 == null || s1.getStopStation() == null) {
                continue;
            }
            for (Stop s2 : trip2.getStops()) {
                if (s2 == null || s2.getStopStation() == null) {
                    continue;
                }
                if (s1.getStopStation().equals(s2.getStopStation())) {
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
        if (newTrip.getDepartureTime() == null || newTrip.getArrivalTime() == null) {
            return false;
        }
        if (!isValidTime(newTrip.getDepartureTime()) || !isValidTime(newTrip.getArrivalTime())) {
            return false;
        }
        // Check departure time < arrival time
        try {
            int dep = timeToMinutes(newTrip.getDepartureTime());
            int arr = timeToMinutes(newTrip.getArrivalTime());
            if (dep >= arr) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        if (trips != null) {
            for (Trip existing : trips) {
                if (existing == null) {
                    continue;
                }
                if (existing.getDepartureTime() == null || existing.getArrivalTime() == null) {
                    continue;
                }
                if (existing.isTimeConflicting(newTrip.getDepartureTime(), newTrip.getArrivalTime())) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValidTime(String time) {
        if (time == null) {
            return false;
        }
        try {
            String[] parts = time.split(":");
            if (parts.length < 2) {
                return false;
            }
            int h = Integer.parseInt(parts[0]);
            int m = Integer.parseInt(parts[1]);
            return h >= 0 && h < 24 && m >= 0 && m < 60;
        } catch (Exception e) {
            return false;
        }
    }

    private int timeToMinutes(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        return hours * 60 + minutes;
    }
}
