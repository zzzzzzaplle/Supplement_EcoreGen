import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class Driver extends User {
    private List<Trip> trips;

    public Driver() {
        this.trips = new ArrayList<>();
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void addTrip(Trip trip) {
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
        if (newTrip.getDepartureDate() == null) {
            return false;
        }

        // Validate time window: departure time must be earlier than arrival time
        String decompStart = toComparableTime(newTrip.getDepartureDate(), newTrip.getDepartureTime());
        String decompEnd = toComparableTime(newTrip.getDepartureDate(), newTrip.getArrivalTime());
        if (decompStart.isEmpty() || decompEnd.isEmpty()) {
            return false;
        }
        if (decompStart.compareTo(decompEnd) >= 0) {
            return false;
        }

        // Check no overlap with existing trips
        for (Trip existing : trips) {
            if (existing == null) {
                continue;
            }
            if (existing.getDepartureTime() == null || existing.getArrivalTime() == null) {
                continue;
            }

            // Check time overlap using isTimeConflicting
            if (newTrip.isTimeConflicting(existing.getDepartureTime(), existing.getArrivalTime())) {
                return false;
            }

            // Check stop overlap for indirect trips
            if (checkStopOverlap(newTrip, existing)) {
                return false;
            }
        }

        return true;
    }

    private String toComparableTime(Date date, String time) {
        if (date == null || time == null) {
            return "";
        }
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMddHHmm");
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            String[] parts = time.split(":");
            if (parts.length >= 2) {
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
                if (parts.length >= 3) {
                    cal.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
                }
            }
            return sdf.format(cal.getTime());
        } catch (Exception e) {
            return "";
        }
    }
}
