import java.util.*;

public class Driver extends User {
    private List<Trip> trips = new ArrayList<>();

    public Driver() {}

    public List<Trip> getTrips() { return trips; }
    public void addTrip(Trip t) { trips.add(t); }

    public boolean canPostTrip(Trip newTrip) {
        if (newTrip == null || newTrip.getDepartureTime() == null || newTrip.getArrivalTime() == null ||
            newTrip.getDepartureTime().compareTo(newTrip.getArrivalTime()) >= 0) return false;
        
        for (Trip existing : trips) {
            if (existing.getDepartureDate().equals(newTrip.getDepartureDate()) &&
                existing.isTimeConflicting(newTrip.getDepartureTime(), newTrip.getArrivalTime())) {
                return false;
            }
        }
        return true;
    }

    public boolean checkStopOverlap(Trip t1, Trip t2) {
        if (t1 == null || t2 == null) return false;
        Set<String> s1 = t1.getStopStations();
        Set<String> s2 = t2.getStopStations();
        if (s1.isEmpty() || s2.isEmpty()) return false;
        
        for (String s : s1) {
            if (s2.contains(s)) return true;
        }
        return false;
    }
}
