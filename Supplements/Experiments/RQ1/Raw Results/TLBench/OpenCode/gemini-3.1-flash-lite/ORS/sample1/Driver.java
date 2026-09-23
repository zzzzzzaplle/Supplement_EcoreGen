import java.util.ArrayList;
import java.util.List;

public class Driver extends User {
    private List<Trip> trips = new ArrayList<>();

    public Driver() {}

    public List<Trip> getTrips() { return trips; }
    public void addTrip(Trip trip) { this.trips.add(trip); }

    public boolean checkStopOverlap(Trip trip1, Trip trip2) {
        if (trip1 == null || trip2 == null) return false;
        List<Stop> stops1 = trip1.getStops();
        List<Stop> stops2 = trip2.getStops();
        if (stops1 == null || stops1.isEmpty() || stops2 == null || stops2.isEmpty()) return false;
        for (Stop s1 : stops1) {
            for (Stop s2 : stops2) {
                if (s1.getStopStation() != null && s1.getStopStation().equals(s2.getStopStation())) return true;
            }
        }
        return false;
    }

    public boolean canPostTrip(Trip newTrip) {
        if (newTrip == null) return false;
        for (Trip existingTrip : trips) {
            if (existingTrip.isTimeConflicting(newTrip.getDepartureTime(), newTrip.getArrivalTime())) return false;
        }
        return true;
    }
}
