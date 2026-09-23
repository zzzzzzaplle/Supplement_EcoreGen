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
        trips.add(trip);
    }

    public boolean checkStopOverlap(Trip trip1, Trip trip2) {
        if (trip1 == null || trip2 == null) {
            return false;
        }
        java.util.Set<String> stops1 = trip1.getStopStations();
        java.util.Set<String> stops2 = trip2.getStopStations();
        if (stops1.isEmpty() || stops2.isEmpty()) {
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
        String departureTime = newTrip.getDepartureTime();
        String arrivalTime = newTrip.getArrivalTime();
        if (departureTime == null || arrivalTime == null) {
            return false;
        }
        if (!isValidTime(departureTime) || !isValidTime(arrivalTime)) {
            return false;
        }
        if (!isEarlier(departureTime, arrivalTime)) {
            return false;
        }
        if (newTrip.getDepartureDate() == null) {
            return false;
        }
        for (Trip existingTrip : trips) {
            if (isTimeConflicting(existingTrip, newTrip)) {
                return false;
            }
        }
        return true;
    }

    private boolean isEarlier(String time1, String time2) {
        String[] parts1 = time1.split(":");
        String[] parts2 = time2.split(":");
        int h1 = Integer.parseInt(parts1[0]);
        int m1 = Integer.parseInt(parts1[1]);
        int h2 = Integer.parseInt(parts2[0]);
        int m2 = Integer.parseInt(parts2[1]);
        if (h1 != h2) {
            return h1 < h2;
        }
        return m1 < m2;
    }

    private boolean isValidTime(String time) {
        if (time == null || !time.matches("\\d{2}:\\d{2}")) {
            return false;
        }
        String[] parts = time.split(":");
        int h = Integer.parseInt(parts[0]);
        int m = Integer.parseInt(parts[1]);
        return h >= 0 && h <= 23 && m >= 0 && m <= 59;
    }

    private boolean isTimeConflicting(Trip existingTrip, Trip newTrip) {
        if (!existingTrip.getDepartureDate().equals(newTrip.getDepartureDate())) {
            return false;
        }
        String extDep = existingTrip.getDepartureTime();
        String extArr = existingTrip.getArrivalTime();
        String newDep = newTrip.getDepartureTime();
        String newArr = newTrip.getArrivalTime();
        if (extDep == null || extArr == null || newDep == null || newArr == null) {
            return false;
        }
        if (isEarlierOrEqual(newArr, newDep) || isEarlierOrEqual(extArr, extDep)) {
            return false;
        }
        if (isEarlierOrEqual(newArr, extDep) || isEarlierOrEqual(extArr, newDep)) {
            return false;
        }
        return true;
    }

    private boolean isEarlierOrEqual(String time1, String time2) {
        String[] parts1 = time1.split(":");
        String[] parts2 = time2.split(":");
        int h1 = Integer.parseInt(parts1[0]);
        int m1 = Integer.parseInt(parts1[1]);
        int h2 = Integer.parseInt(parts2[0]);
        int m2 = Integer.parseInt(parts2[1]);
        if (h1 != h2) {
            return h1 < h2;
        }
        return m1 <= m2;
    }
}
