import java.util.List;
import java.util.ArrayList;
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
        for (String s : stops1) {
            if (stops2.contains(s)) {
                return true;
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
        String dt = newTrip.getDepartureTime();
        String at = newTrip.getArrivalTime();
        int dtInt = Integer.parseInt(dt.replace(":", ""));
        int atInt = Integer.parseInt(at.replace(":", ""));
        if (dtInt >= atInt) {
            return false;
        }
        for (Trip existing : trips) {
            if (existing.isTimeConflicting(dt, at)) {
                return false;
            }
        }
        return true;
    }
}
