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

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    public void addTrip(Trip trip) {
        if (trip == null) {
            return;
        }
        if (trips == null) {
            trips = new ArrayList<Trip>();
        }
        trips.add(trip);
    }

    public boolean checkStopOverlap(Trip trip1, Trip trip2) {
        if (trip1 == null || trip2 == null) {
            return false;
        }
        if (trip1.getStops() == null || trip2.getStops() == null) {
            return false;
        }
        if (trip1.getStops().isEmpty() || trip2.getStops().isEmpty()) {
            return false;
        }
        return !trip1.getStopStations().isEmpty() && !trip2.getStopStations().isEmpty() && hasCommonStop(trip1, trip2);
    }

    private boolean hasCommonStop(Trip trip1, Trip trip2) {
        for (String stop1 : trip1.getStopStations()) {
            if (trip2.getStopStations().contains(stop1)) {
                return true;
            }
        }
        return false;
    }

    public boolean canPostTrip(Trip newTrip) {
        if (newTrip == null) {
            return false;
        }
        if (!newTrip.hasValidTimeWindow()) {
            return false;
        }
        if (trips == null) {
            return true;
        }
        for (Trip trip : trips) {
            if (trip == null) {
                continue;
            }
            if (trip.isTimeConflicting(newTrip.getDepartureTime(), newTrip.getArrivalTime())) {
                return false;
            }
        }
        return true;
    }
}
