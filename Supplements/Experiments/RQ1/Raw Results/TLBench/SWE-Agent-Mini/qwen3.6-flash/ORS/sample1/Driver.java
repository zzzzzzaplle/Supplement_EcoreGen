import java.util.List;
import java.util.ArrayList;

public class Driver extends User {
    private List<Trip> trips;

    public Driver() {
        trips = new ArrayList<>();
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void addTrip(Trip trip) {
        if (trip != null && !trips.contains(trip)) {
            trips.add(trip);
        }
    }

    public boolean checkStopOverlap(Trip trip1, Trip trip2) {
        if (trip1 == null || trip2 == null) {
            return false;
        }
        List<Stop> stops1 = trip1.getStops();
        List<Stop> stops2 = trip2.getStops();
        if (stops1 == null ||stops1.isEmpty() || stops2 == null || stops2.isEmpty()) {
            return false;
        }
        for (Stop s1 : stops1) {
            String station1 = s1 != null ? s1.getStopStation() : null;
            if (station1 == null) continue;
            for (Stop s2 : stops2) {
                String station2 = s2 != null ? s2.getStopStation() : null;
                if (station2 == null) continue;
                if (station1.equals(station2)) {
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
        String depTime = newTrip.getDepartureTime();
        String arrTime = newTrip.getArrivalTime();
        if (depTime == null || arrTime == null) {
            return false;
        }
        if (depTime.compareTo(arrTime) >= 0) {
            return false;
        }
        for (Trip existing : trips) {
            if (existing == null) continue;
            String existingDep = existing.getDepartureTime();
            String existingArr = existing.getArrivalTime();
            if (existingDep == null || existingArr == null) continue;
            if (newTrip.isTimeConflicting(existingDep, existingArr)) {
                return false;
            }
            if (checkStopOverlap(newTrip, existing)) {
                return false;
            }
        }
        return true;
    }
}
