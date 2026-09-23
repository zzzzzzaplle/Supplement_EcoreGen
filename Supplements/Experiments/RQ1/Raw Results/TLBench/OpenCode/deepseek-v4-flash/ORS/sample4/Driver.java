import java.util.ArrayList;
import java.util.List;

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
        java.util.Set<String> stops1 = trip1.getStopStations();
        java.util.Set<String> stops2 = trip2.getStopStations();
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
        String depTime = newTrip.getDepartureTime();
        String arrTime = newTrip.getArrivalTime();
        if (depTime == null || arrTime == null) {
            return false;
        }
        String[] depParts = depTime.split(":");
        String[] arrParts = arrTime.split(":");
        if (depParts.length != 2 || arrParts.length != 2) {
            return false;
        }
        try {
            int depMin = Integer.parseInt(depParts[0]) * 60 + Integer.parseInt(depParts[1]);
            int arrMin = Integer.parseInt(arrParts[0]) * 60 + Integer.parseInt(arrParts[1]);
            if (depMin >= arrMin) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        for (Trip existing : trips) {
            if (existing.isTimeConflicting(depTime, arrTime)) {
                return false;
            }
        }
        return true;
    }
}
