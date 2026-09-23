import java.util.*;

public class Driver extends User {
    private List<Trip> trips = new ArrayList<>();

    public Driver() {}

    public List<Trip> getTrips() { return trips; }
    public void setTrips(List<Trip> trips) { this.trips = trips; }
    public void addTrip(Trip trip) { trips.add(trip); }
    
    public boolean checkStopOverlap(Trip trip1, Trip trip2) {
        if (trip1 == null || trip2 == null) return false;
        if (trip1.getStops().isEmpty() || trip2.getStops().isEmpty()) return false;
        
        Set<String> s1 = trip1.getStopStations();
        for (Stop s : trip2.getStops()) {
            if (s1.contains(s.getStopStation())) return true;
        }
        return false;
    }

    public boolean canPostTrip(Trip newTrip) {
        if (newTrip == null) return false;
        if (newTrip.getDepartureTime() == null || newTrip.getArrivalTime() == null) return false;
        if (newTrip.getDepartureTime().compareTo(newTrip.getArrivalTime()) >= 0) return false;
        
        for (Trip existingTrip : trips) {
            if (existingTrip.isTimeConflicting(newTrip.getDepartureTime(), newTrip.getArrivalTime())) {
                return false;
            }
        }
        return true;
    }
}
