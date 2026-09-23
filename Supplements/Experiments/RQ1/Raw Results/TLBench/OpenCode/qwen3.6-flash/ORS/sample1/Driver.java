import java.util.ArrayList;
import java.util.Date;
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

    public void addTrip(Trip trip) {
        if (trip != null) {
            this.trips.add(trip);
        }
    }

    public boolean checkStopOverlap(Trip trip1, Trip trip2) {
        if (trip1 == null || trip2 == null) {
            return false;
        }
        Set<String> stops1 = trip1.getStopStations();
        Set<String> stops2 = trip2.getStopStations();
        if (stops1 == null || stops1.isEmpty() || stops2 == null || stops2.isEmpty()) {
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
        try {
            String depTime = newTrip.getDepartureTime();
            String arrTime = newTrip.getArrivalTime();
            if (depTime == null || arrTime == null || depTime.compareTo(arrTime) >= 0) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        Date newDepDate = newTrip.getDepartureDate();
        for (Trip existing : trips) {
            if (checkStopOverlap(newTrip, existing)) {
                return false;
            }
            if (newDepDate != null && newDepDate.equals(existing.getDepartureDate())) {
                String newDep = newTrip.getDepartureTime();
                String newArr = newTrip.getArrivalTime();
                if (existing.isTimeConflicting(newDep, newArr)) {
                    return false;
                }
            }
        }
        return true;
    }
}
