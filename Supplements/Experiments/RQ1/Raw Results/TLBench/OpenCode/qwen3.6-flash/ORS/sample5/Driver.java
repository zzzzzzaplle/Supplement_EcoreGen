import java.util.List;
import java.util.ArrayList;
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
        if (trip != null && this.trips != null) {
            this.trips.add(trip);
        }
    }

    public boolean checkStopOverlap(Trip trip1, Trip trip2) {
        if (trip1 == null || trip2 == null) {
            return false;
        }
        List<Stop> stops1 = trip1.getStops();
        List<Stop> stops2 = trip2.getStops();
        if (stops1 == null || stops2 == null || stops1.isEmpty() || stops2.isEmpty()) {
            return false;
        }
        for (Stop s1 : stops1) {
            String station1 = s1.getStopStation();
            if (station1 == null || station1.isEmpty()) {
                continue;
            }
            for (Stop s2 : stops2) {
                String station2 = s2.getStopStation();
                if (station2 != null && station1.equals(station2)) {
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
        String depTimeStr = newTrip.getDepartureTime();
        String arrTimeStr = newTrip.getArrivalTime();
        if (!isValidTripTime(depTimeStr, arrTimeStr)) {
            return false;
        }
        Set<String> newTripStops = newTrip.getStopStations();
        for (Trip existingTrip : this.trips) {
            if (existingTrip.isTimeConflicting(depTimeStr, arrTimeStr)) {
                return false;
            }
            if (newTripStops != null && !newTripStops.isEmpty()
                    && existingTrip.getStops() != null
                    && !existingTrip.getStops().isEmpty()
                    && checkStopOverlap(existingTrip, newTrip)) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidTripTime(String departureTime, String arrivalTime) {
        if (departureTime == null || departureTime.isEmpty()
                || arrivalTime == null || arrivalTime.isEmpty()) {
            return false;
        }
        int depSecs = parseTimeToSeconds(departureTime);
        int arrSecs = parseTimeToSeconds(arrivalTime);
        if (depSecs < 0 || arrSecs < 0) {
            return false;
        }
        return depSecs < arrSecs;
    }

    private int parseTimeToSeconds(String timeStr) {
        try {
            String[] parts = timeStr.split(":");
            if (parts.length != 2) {
                return -1;
            }
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59) {
                return -1;
            }
            return hours * 3600 + minutes * 60;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
