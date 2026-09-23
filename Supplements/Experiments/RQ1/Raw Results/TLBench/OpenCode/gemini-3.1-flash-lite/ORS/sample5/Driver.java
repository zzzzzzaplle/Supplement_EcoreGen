import java.util.List;
import java.util.ArrayList;

public class Driver extends User {
    private List<Trip> trips;

    public Driver() {
        this.trips = new ArrayList<>();
    }

    public List<Trip> getTrips() { return trips; }
    public void addTrip(Trip trip) { this.trips.add(trip); }

    public boolean checkStopOverlap(Trip trip1, Trip trip2) {
        if (trip1 == null || trip2 == null || trip1.getStops().isEmpty() || trip2.getStops().isEmpty()) {
            return false;
        }
        for (Stop s1 : trip1.getStops()) {
            for (Stop s2 : trip2.getStops()) {
                if (s1.getStopStation().equals(s2.getStopStation())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canPostTrip(Trip newTrip) {
        if (newTrip == null) return false;
        if (newTrip.getDepartureTime().compareTo(newTrip.getArrivalTime()) >= 0) return false;
        for (Trip t : trips) {
            if (t.isTimeConflicting(newTrip.getDepartureTime(), newTrip.getArrivalTime())) {
                return false;
            }
        }
        return true;
    }
}
