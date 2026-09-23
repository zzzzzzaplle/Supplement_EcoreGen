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
        if (this.trips == null) {
            this.trips = new ArrayList<Trip>();
        }
        this.trips.add(trip);
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
            if (s1 == null || s1.getStopStation() == null) continue;
            for (Stop s2 : trip2.getStops()) {
                if (s2 == null || s2.getStopStation() == null) continue;
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
        int dep = toMinutes(newTrip.getDepartureTime());
        int arr = toMinutes(newTrip.getArrivalTime());
        if (dep >= arr) {
            return false;
        }
        if (trips != null) {
            for (Trip existing : trips) {
                if (existing == null) continue;
                if (newTrip.isTimeConflicting(existing.getDepartureTime(), existing.getArrivalTime())) {
                    return false;
                }
            }
        }
        return true;
    }

    private int toMinutes(String time) {
        if (time == null) {
            return 0;
        }
        try {
            String[] parts = time.split(":");
            return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
        } catch (Exception e) {
            return 0;
        }
    }
}
