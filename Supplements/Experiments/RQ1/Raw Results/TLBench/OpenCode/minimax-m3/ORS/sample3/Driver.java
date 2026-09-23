import java.util.ArrayList;
import java.util.List;

public class Driver extends User {
    private List<Trip> trips;

    public Driver() {
        this.trips = new ArrayList<>();
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
        List<Stop> stops1 = trip1.getStops();
        List<Stop> stops2 = trip2.getStops();
        if (stops1 == null || stops1.isEmpty() || stops2 == null || stops2.isEmpty()) {
            return false;
        }
        for (Stop s1 : stops1) {
            for (Stop s2 : stops2) {
                if (s1.getStopStation() != null && s1.getStopStation().equals(s2.getStopStation())) {
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
        try {
            String[] depParts = newTrip.getDepartureTime().split(":");
            int depHour = Integer.parseInt(depParts[0]);
            int depMin = Integer.parseInt(depParts[1]);

            String[] arrParts = newTrip.getArrivalTime().split(":");
            int arrHour = Integer.parseInt(arrParts[0]);
            int arrMin = Integer.parseInt(arrParts[1]);

            int depMinutes = depHour * 60 + depMin;
            int arrMinutes = arrHour * 60 + arrMin;
            if (depMinutes >= arrMinutes) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        for (Trip existingTrip : trips) {
            if (existingTrip.isTimeConflicting(newTrip.getDepartureTime(), newTrip.getArrivalTime())) {
                return false;
            }
            if (checkStopOverlap(existingTrip, newTrip)) {
                return false;
            }
        }
        return true;
    }
}