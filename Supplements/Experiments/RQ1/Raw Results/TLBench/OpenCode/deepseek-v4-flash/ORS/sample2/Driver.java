import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        Set<String> stops1 = trip1.getStopStations();
        Set<String> stops2 = trip2.getStopStations();
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
        String depTime = newTrip.getDepartureTime();
        String arrTime = newTrip.getArrivalTime();
        Date depDate = newTrip.getDepartureDate();

        if (depTime == null || arrTime == null || depDate == null) {
            return false;
        }

        if (!isValidTimeFormat(depTime) || !isValidTimeFormat(arrTime)) {
            return false;
        }

        long depMs = timeStringToMillis(depTime);
        long arrMs = timeStringToMillis(arrTime);
        if (depMs >= arrMs) {
            return false;
        }

        if (trips == null) {
            return true;
        }

        for (Trip existingTrip : trips) {
            if (existingTrip == null) {
                continue;
            }
            Date existingDate = existingTrip.getDepartureDate();
            if (existingDate == null) {
                continue;
            }
            if (!depDate.equals(existingDate)) {
                continue;
            }
            String existingDep = existingTrip.getDepartureTime();
            String existingArr = existingTrip.getArrivalTime();
            if (existingDep == null || existingArr == null) {
                continue;
            }
            long existingDepMs = timeStringToMillis(existingDep);
            long existingArrMs = timeStringToMillis(existingArr);

            if (depMs < existingArrMs && existingDepMs < arrMs) {
                return false;
            }
        }

        return true;
    }

    private boolean isValidTimeFormat(String time) {
        if (time == null) {
            return false;
        }
        return time.matches("\\d{2}:\\d{2}");
    }

    private long timeStringToMillis(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return (long) hours * 3600000 + (long) minutes * 60000;
    }
}
