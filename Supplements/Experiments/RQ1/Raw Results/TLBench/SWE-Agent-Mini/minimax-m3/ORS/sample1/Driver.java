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
        List<Stop> stops1 = trip1.getStops();
        List<Stop> stops2 = trip2.getStops();
        if (stops1 == null || stops2 == null || stops1.isEmpty() || stops2.isEmpty()) {
            return false;
        }
        for (Stop s1 : stops1) {
            if (s1 == null) continue;
            for (Stop s2 : stops2) {
                if (s2 == null) continue;
                if (s1.getStopStation() != null && s1.getStopStation().equals(s2.getStopStation())) {
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
        // Validate time window: departure before arrival
        try {
            String[] depParts = newTrip.getDepartureTime().split(":");
            String[] arrParts = newTrip.getArrivalTime().split(":");
            int depMin = Integer.parseInt(depParts[0]) * 60
                    + (depParts.length > 1 ? Integer.parseInt(depParts[1]) : 0);
            int arrMin = Integer.parseInt(arrParts[0]) * 60
                    + (arrParts.length > 1 ? Integer.parseInt(arrParts[1]) : 0);
            if (depMin >= arrMin) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        if (trips != null) {
            for (Trip existing : trips) {
                if (existing == null) continue;
                if (existing.isTimeConflicting(newTrip.getDepartureTime(), newTrip.getArrivalTime())) {
                    return false;
                }
            }
        }
        return true;
    }
}
