import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

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
        if (trip != null) {
            trips.add(trip);
        }
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
            if (s1 == null || s1.getStopStation() == null) {
                continue;
            }
            for (Stop s2 : stops2) {
                if (s2 == null || s2.getStopStation() == null) {
                    continue;
                }
                if (s1.getStopStation().equals(s2.getStopStation())) {
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
        if (newTrip.getDepartureTime().compareTo(newTrip.getArrivalTime()) >= 0) {
            return false;
        }
        if (newTrip.getDepartureDate() == null) {
            return false;
        }

        if (trips != null) {
            for (Trip existing : trips) {
                if (existing == null) {
                    continue;
                }
                if (existing.getDepartureDate() == null) {
                    continue;
                }

                Calendar c1 = Calendar.getInstance();
                c1.setTime(existing.getDepartureDate());
                Calendar c2 = Calendar.getInstance();
                c2.setTime(newTrip.getDepartureDate());
                boolean sameDay = c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                        && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);

                if (sameDay
                        && existing.isTimeConflicting(newTrip.getDepartureTime(), newTrip.getArrivalTime())) {
                    return false;
                }
            }
        }
        return true;
    }
}
