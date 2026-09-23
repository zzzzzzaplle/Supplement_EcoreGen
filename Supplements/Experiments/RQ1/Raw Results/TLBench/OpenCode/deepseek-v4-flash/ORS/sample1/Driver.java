import java.util.List;
import java.util.ArrayList;

public class Driver extends User {
    private List<Trip> trips;

    public Driver() {
        this.trips = new ArrayList<>();
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void addTrip(Trip trip) {
        this.trips.add(trip);
    }

    public boolean checkStopOverlap(Trip trip1, Trip trip2) {
        if (trip1 == null || trip2 == null) {
            return false;
        }
        java.util.Set<String> stops1 = trip1.getStopStations();
        java.util.Set<String> stops2 = trip2.getStopStations();
        if (stops1.isEmpty() || stops2.isEmpty()) {
            return false;
        }
        for (String stop : stops1) {
            if (stops2.contains(stop)) {
                return true;
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
        if (!newTrip.isTimeConflicting(depTime, arrTime)) {
            return false;
        }
        for (Trip existing : trips) {
            if (existing == null) continue;
            if (existing.getDepartureTime() == null || existing.getArrivalTime() == null) continue;
            String existDep = existing.getDepartureTime();
            String existArr = existing.getArrivalTime();
            if (depTime.compareTo(existArr) < 0 && arrTime.compareTo(existDep) > 0) {
                return false;
            }
            if (existing instanceof Trip) {
                if (existing.getStops() != null && !existing.getStops().isEmpty() &&
                    newTrip.getStops() != null && !newTrip.getStops().isEmpty()) {
                    if (checkStopOverlap(existing, newTrip)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
