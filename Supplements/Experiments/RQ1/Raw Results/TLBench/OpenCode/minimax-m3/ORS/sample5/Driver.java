import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
        if (trip1.getStops() == null || trip1.getStops().isEmpty()) {
            return false;
        }
        if (trip2.getStops() == null || trip2.getStops().isEmpty()) {
            return false;
        }

        Set<String> s1 = trip1.getStopStations();
        Set<String> s2 = trip2.getStopStations();
        for (String stop : s1) {
            if (s2.contains(stop)) {
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
        if (newTrip.getDepartureDate() == null) {
            return false;
        }

        int depTime = parseTime(newTrip.getDepartureTime());
        int arrTime = parseTime(newTrip.getArrivalTime());
        if (depTime >= arrTime) {
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
                if (!sameDay(existing.getDepartureDate(), newTrip.getDepartureDate())) {
                    continue;
                }
                if (existing.isTimeConflicting(newTrip.getDepartureTime(), newTrip.getArrivalTime())) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean sameDay(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    private int parseTime(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0].trim()) * 60 + Integer.parseInt(parts[1].trim());
    }
}
