import java.util.ArrayList;
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
        this.trips.add(trip);
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
        if (newTrip.getDepartureTime() == null || newTrip.getArrivalTime() == null) {
            return false;
        }

        // Validate departure time < arrival time
        try {
            String[] depParts = newTrip.getDepartureTime().split(":");
            String[] arrParts = newTrip.getArrivalTime().split(":");

            int depHour = Integer.parseInt(depParts[0]);
            int depMin = Integer.parseInt(depParts[1]);
            int arrHour = Integer.parseInt(arrParts[0]);
            int arrMin = Integer.parseInt(arrParts[1]);

            int depTotal = depHour * 60 + depMin;
            int arrTotal = arrHour * 60 + arrMin;

            if (depTotal >= arrTotal) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        // Check no overlap with existing trips
        for (Trip existingTrip : trips) {
            if (newTrip.getDepartureDate() != null && existingTrip.getDepartureDate() != null) {
                // Only check overlap if same day
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
                String newDate = sdf.format(newTrip.getDepartureDate());
                String existingDate = sdf.format(existingTrip.getDepartureDate());
                if (!newDate.equals(existingDate)) {
                    continue;
                }
            } else {
                continue;
            }

            if (existingTrip.isTimeConflicting(newTrip.getDepartureTime(), newTrip.getArrivalTime())) {
                return false;
            }
        }

        return true;
    }
}
