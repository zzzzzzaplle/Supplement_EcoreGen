import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Driver extends User {
    private List<Trip> trips = new ArrayList<>();

    public Driver() {}

    public List<Trip> getTrips() { return trips; }
    public void setTrips(List<Trip> trips) { this.trips = trips; }
    public void addTrip(Trip trip) { trips.add(trip); }

    public boolean checkStopOverlap(Trip trip1, Trip trip2) {
        if (trip1 == null || trip2 == null) return false;
        Set<String> s1 = trip1.getStopStations();
        Set<String> s2 = trip2.getStopStations();
        if (s1.isEmpty() || s2.isEmpty()) return false;
        
        for (String s : s1) {
            if (s2.contains(s)) return true;
        }
        return false;
    }

    public boolean canPostTrip(Trip newTrip) {
        return true;
    }
}
