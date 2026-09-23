import java.util.*;

public class Driver extends User {
    private List<Trip> trips = new ArrayList<>();

    public Driver() {}

    public List<Trip> getTrips() { return trips; }
    public void addTrip(Trip trip) { trips.add(trip); }

    public boolean checkStopOverlap(Trip trip1, Trip trip2) {
        if (trip1 == null || trip2 == null) return false;
        Set<String> stops1 = trip1.getStopStations();
        Set<String> stops2 = trip2.getStopStations();
        if (stops1.isEmpty() || stops2.isEmpty()) return false;
        for (String s : stops1) {
            if (stops2.contains(s)) return true;
        }
        return false;
    }

    public boolean canPostTrip(Trip newTrip) {
        return false; // Implementation needed
    }
}
