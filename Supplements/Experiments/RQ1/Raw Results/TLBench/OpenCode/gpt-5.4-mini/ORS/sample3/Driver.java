import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Driver extends User {
    private List<Trip> trips;

    public Driver() {
        this.trips = new ArrayList<>();
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    public void addTrip(Trip trip) {
        if (this.trips == null) {
            this.trips = new ArrayList<>();
        }
        if (trip != null) {
            this.trips.add(trip);
        }
    }

    public boolean checkStopOverlap(Trip trip1, Trip trip2) {
        if (trip1 == null || trip2 == null) {
            return false;
        }
        Set<String> stopStations1 = trip1.getStopStations();
        Set<String> stopStations2 = trip2.getStopStations();
        if (stopStations1 == null || stopStations2 == null || stopStations1.isEmpty() || stopStations2.isEmpty()) {
            return false;
        }
        for (String stop : stopStations1) {
            if (stopStations2.contains(stop)) {
                return true;
            }
        }
        return false;
    }

    public boolean canPostTrip(Trip newTrip) {
        if (newTrip == null || newTrip.getDepartureTime() == null || newTrip.getArrivalTime() == null) {
            return false;
        }
        if (!Trip.isValidTime(newTrip.getDepartureTime()) || !Trip.isValidTime(newTrip.getArrivalTime())) {
            return false;
        }
        if (Trip.compareTimes(newTrip.getDepartureTime(), newTrip.getArrivalTime()) >= 0) {
            return false;
        }
        if (trips == null) {
            return true;
        }
        for (Trip trip : trips) {
            if (trip == null) {
                continue;
            }
            if (trip.isTimeConflicting(newTrip.getDepartureTime(), newTrip.getArrivalTime()) || newTrip.isTimeConflicting(trip.getDepartureTime(), trip.getArrivalTime())) {
                return false;
            }
        }
        return true;
    }
}
