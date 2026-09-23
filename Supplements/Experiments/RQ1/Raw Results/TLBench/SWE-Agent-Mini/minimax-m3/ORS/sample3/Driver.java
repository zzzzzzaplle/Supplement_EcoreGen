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
        if (this.trips == null) {
            this.trips = new ArrayList<Trip>();
        }
        this.trips.add(trip);
    }

    public boolean checkStopOverlap(Trip trip1, Trip trip2) {
        if (trip1 == null || trip2 == null) {
            return false;
        }
        java.util.Set<String> stops1 = trip1.getStopStations();
        java.util.Set<String> stops2 = trip2.getStopStations();
        if (stops1 == null || stops1.isEmpty() || stops2 == null || stops2.isEmpty()) {
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
        if (newTrip.getDepartureTime().compareTo(newTrip.getArrivalTime()) >= 0) {
            return false;
        }
        if (this.trips == null) {
            return true;
        }
        for (Trip existing : this.trips) {
            if (existing == null) {
                continue;
            }
            if (existing.getDepartureTime() == null || existing.getArrivalTime() == null) {
                continue;
            }
            if (newTrip.getDepartureTime().equals(existing.getDepartureTime())
                    && newTrip.getArrivalTime().equals(existing.getArrivalTime())) {
                return false;
            }
            if (newTrip.getDepartureTime().compareTo(existing.getArrivalTime()) < 0
                    && existing.getDepartureTime().compareTo(newTrip.getArrivalTime()) < 0) {
                return false;
            }
        }
        return true;
    }
}
