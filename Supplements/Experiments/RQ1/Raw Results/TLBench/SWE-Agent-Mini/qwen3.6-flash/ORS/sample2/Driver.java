import java.util.*;

public class Driver extends User {
    private List<Trip> trips;

    public Driver() {
        this.trips = new ArrayList<>();
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void addTrip(Trip trip) {
        if (trip != null && !trips.contains(trip)) {
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
        
        Set<String> stationStops1 = new HashSet<>();
        for (Stop stop : stops1) {
            if (stop != null && stop.getStopStation() != null) {
                stationStops1.add(stop.getStopStation());
            }
        }
        
        for (Stop stop : stops2) {
            if (stop != null && stop.getStopStation() != null) {
                if (stationStops1.contains(stop.getStopStation())) {
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
        
        String depTime = newTrip.getDepartureTime();
        String arrTime = newTrip.getArrivalTime();
        
        if (depTime == null || arrTime == null) {
            return false;
        }
        
        if (!isTimeValid(depTime) || !isTimeValid(arrTime)) {
            return false;
        }
        
        if (!isTimeBefore(depTime, arrTime)) {
            return false;
        }
        
        for (Trip existingTrip : trips) {
            if (existingTrip == null) {
                continue;
            }
            if (existingTrip.isTimeConflicting(depTime, arrTime)) {
                return false;
            }
        }
        
        return true;
    }

    private boolean isTimeValid(String time) {
        if (time == null || time.isEmpty()) {
            return false;
        }
        try {
            String[] parts = time.split(":");
            if (parts.length != 2) {
                return false;
            }
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isTimeBefore(String time1, String time2) {
        String[] parts1 = time1.split(":");
        String[] parts2 = time2.split(":");
        int h1 = Integer.parseInt(parts1[0]);
        int m1 = Integer.parseInt(parts1[1]);
        int h2 = Integer.parseInt(parts2[0]);
        int m2 = Integer.parseInt(parts2[1]);
        
        int total1 = h1 * 60 + m1;
        int total2 = h2 * 60 + m2;
        
        return total1 < total2;
    }
}
