import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class Driver extends User {
    private List<Trip> trips;

    public Driver() { }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    public void addTrip(Trip trip) {
        if (trips == null) {
            trips = new ArrayList<>();
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

        Set<String> stations1 = trip1.getStopStations();
        Set<String> stations2 = trip2.getStopStations();

        for (String station : stations1) {
            if (stations2.contains(station)) {
                return true;
            }
        }
        return false;
    }

    public boolean canPostTrip(Trip newTrip) {
        if (newTrip == null || trips == null) {
            return false;
        }

        Date depDate = newTrip.getDepartureDate();
        String depTime = newTrip.getDepartureTime();
        String arrTime = newTrip.getArrivalTime();

        if (depDate == null || depTime == null || arrTime == null) {
            return false;
        }

        int depMin = parseTimeString(depTime);
        int arrMin = parseTimeString(arrTime);

        if (depMin < 0 || arrMin < 0 || depMin >= arrMin) {
            return false;
        }

        for (Trip existing : trips) {
            if (existing == null) {
                continue;
            }

            Date existingDate = existing.getDepartureDate();

            if (existingDate != null && existingDate.equals(depDate)) {
                if (existing.isTimeConflicting(newTrip.getDepartureTime(), newTrip.getArrivalTime())) {
                    return false;
                }
            }
        }

        return true;
    }

    private int parseTimeString(String timeStr) {
        if (timeStr == null) {
            return -1;
        }
        try {
            String[] parts = timeStr.split(":");
            return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
        } catch (Exception e) {
            return -1;
        }
    }
}
