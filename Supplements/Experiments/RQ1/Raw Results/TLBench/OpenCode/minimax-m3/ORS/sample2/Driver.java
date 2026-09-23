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
        if (trip != null) {
            trips.add(trip);
        }
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
        String dep = newTrip.getDepartureTime();
        String arr = newTrip.getArrivalTime();
        if (dep == null || arr == null) {
            return false;
        }
        if (parseTime(dep) < 0 || parseTime(arr) < 0) {
            return false;
        }
        if (parseTime(dep) >= parseTime(arr)) {
            return false;
        }
        if (trips == null) {
            return true;
        }
        for (Trip existing : trips) {
            if (existing == null) {
                continue;
            }
            if (newTrip.isTimeConflicting(existing.getDepartureTime(), existing.getArrivalTime())) {
                return false;
            }
        }
        return true;
    }

    private static int parseTime(String t) {
        if (t == null) {
            return -1;
        }
        try {
            String[] parts = t.split(":");
            if (parts.length < 2) {
                return -1;
            }
            int h = Integer.parseInt(parts[0].trim());
            int m = Integer.parseInt(parts[1].trim());
            return h * 60 + m;
        } catch (Exception e) {
            return -1;
        }
    }
}
