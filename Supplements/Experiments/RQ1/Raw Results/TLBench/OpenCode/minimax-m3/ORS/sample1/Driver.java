import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

public class Driver extends User {
    private List<Trip> trips;

    public Driver() {
        this.trips = new ArrayList<Trip>();
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void addTrip(Trip trip) {
        if (this.trips == null) {
            this.trips = new ArrayList<Trip>();
        }
        this.trips.add(trip);
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
        if (newTrip.getDepartureDate() == null) {
            return false;
        }
        String dep = newTrip.getDepartureTime();
        String arr = newTrip.getArrivalTime();
        if (dep == null || arr == null) {
            return false;
        }
        if (toMinutes(dep) >= toMinutes(arr)) {
            return false;
        }
        if (trips != null) {
            for (Trip existing : trips) {
                if (existing == null) {
                    continue;
                }
                if (!isSameDay(newTrip.getDepartureDate(), existing.getDepartureDate())) {
                    continue;
                }
                if (existing.isTimeConflicting(dep, arr)) {
                    return false;
                }
            }
        }
        return true;
    }

    private int toMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    private boolean isSameDay(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(d1).equals(sdf.format(d2));
    }
}
