import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        Set<String> stops1 = trip1.getStopStations();
        Set<String> stops2 = trip2.getStopStations();
        if (stops1.isEmpty() || stops2.isEmpty()) {
            return false;
        }
        for (String s : stops1) {
            if (stops2.contains(s)) {
                return true;
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
        if (!isValidTimeFormat(newTrip.getDepartureTime()) || !isValidTimeFormat(newTrip.getArrivalTime())) {
            return false;
        }
        int dep = timeToMinutes(newTrip.getDepartureTime());
        int arr = timeToMinutes(newTrip.getArrivalTime());
        if (dep >= arr) {
            return false;
        }
        if (trips != null) {
            for (Trip existing : trips) {
                if (!newTrip.getDepartureDate().equals(existing.getDepartureDate())) {
                    continue;
                }
                if (existing.isTimeConflicting(newTrip.getDepartureTime(), newTrip.getArrivalTime())) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValidTimeFormat(String time) {
        return time.matches("\\d{2}:\\d{2}");
    }

    private int timeToMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }
}
