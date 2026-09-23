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
        if (trips == null) {
            trips = new ArrayList<Trip>();
        }
        trips.add(trip);
    }

    public boolean checkStopOverlap(Trip trip1, Trip trip2) {
        if (trip1 == null || trip2 == null) {
            return false;
        }
        if (trip1.getStops() == null || trip1.getStops().isEmpty()) {
            return false;
        }
        if (trip2.getStops() == null || trip2.getStops().isEmpty()) {
            return false;
        }
        for (Stop s1 : trip1.getStops()) {
            for (Stop s2 : trip2.getStops()) {
                if (s1 != null && s2 != null && s1.getStopStation() != null
                        && s1.getStopStation().equals(s2.getStopStation())) {
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
        if (newTrip.getDepartureTime().compareTo(newTrip.getArrivalTime()) >= 0) {
            return false;
        }
        if (trips == null) {
            return true;
        }
        for (Trip existing : trips) {
            if (existing == null) {
                continue;
            }
            if (existing.isTimeConflicting(newTrip.getDepartureTime(), newTrip.getArrivalTime())) {
                return false;
            }
        }
        return true;
    }
}
