import java.util.ArrayList;
import java.util.List;

public class Driver extends User {
    private List<Trip> trips;

    public Driver() {
        trips = new ArrayList<Trip>();
    }

    public List<Trip> getTrips() { return trips; }
    public void addTrip(Trip trip) { if (trip != null) trips.add(trip); }
    public boolean checkStopOverlap(Trip trip1, Trip trip2) {
        if (trip1 == null || trip2 == null || trip1.getStops() == null || trip2.getStops() == null || trip1.getStops().isEmpty() || trip2.getStops().isEmpty()) return false;
        return !java.util.Collections.disjoint(trip1.getStopStations(), trip2.getStopStations());
    }
    public boolean canPostTrip(Trip newTrip) {
        if (newTrip == null || newTrip.getDepartureTime() == null || newTrip.getArrivalTime() == null) return false;
        if (newTrip.getDepartureTime().compareTo(newTrip.getArrivalTime()) >= 0) return false;
        if (trips != null) for (Trip t : trips) if (t != null && t.isTimeConflicting(newTrip.getDepartureTime(), newTrip.getArrivalTime())) return false;
        return true;
    }
}
