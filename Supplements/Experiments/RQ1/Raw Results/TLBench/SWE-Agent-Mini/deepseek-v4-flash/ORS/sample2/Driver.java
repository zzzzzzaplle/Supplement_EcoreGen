import java.util.ArrayList;
import java.util.HashSet;
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
        this.trips.add(trip);
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
        // Departure and arrival times must be valid
        String depTime = newTrip.getDepartureTime();
        String arrTime = newTrip.getArrivalTime();
        if (depTime == null || arrTime == null) {
            return false;
        }
        // Departure time must be earlier than arrival time
        if (depTime.compareTo(arrTime) >= 0) {
            return false;
        }
        // Check no overlap with existing trips
        if (trips != null) {
            for (Trip existing : trips) {
                if (existing != null) {
                    // Check if they are on the same date
                    if (newTrip.getDepartureDate() != null && existing.getDepartureDate() != null) {
                        if (!newTrip.getDepartureDate().equals(existing.getDepartureDate())) {
                            continue; // Different dates, no conflict
                        }
                    }
                    if (existing.isTimeConflicting(depTime, arrTime)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
