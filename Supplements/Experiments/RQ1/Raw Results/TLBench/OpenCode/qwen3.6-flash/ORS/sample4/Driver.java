import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

class Driver extends User {
    private List<Trip> trips;

    public Driver() {
        trips = new ArrayList<>();
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
        List<Stop> stops1 = trip1.getStops();
        List<Stop> stops2 = trip2.getStops();
        if (stops1 == null || stops1.isEmpty()) {
            return false;
        }
        if (stops2 == null || stops2.isEmpty()) {
            return false;
        }
        for (Stop stop1 : stops1) {
            for (Stop stop2 : stops2) {
                if (stop1.getStopStation().equals(stop2.getStopStation())) {
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
        if (trips != null) {
            for (Trip existingTrip : trips) {
                if (existingTrip.equals(newTrip)) {
                    return false;
                }
            }
        }
        LocalTime departure = newTrip.getDepartureTime();
        LocalTime arrival = newTrip.getArrivalTime();
        if (departure == null || arrival == null) {
            return false;
        }
        if (!departure.isBefore(arrival)) {
            return false;
        }
        if (trips != null) {
            for (Trip existingTrip : trips) {
                if (existingTrip != null) {
                    if (existingTrip.isTimeConflicting(
                            departure.toString(),
                            arrival.toString())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
