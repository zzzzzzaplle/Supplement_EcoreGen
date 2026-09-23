import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.Instant;
import java.time.format.DateTimeParseException;

public class Driver extends User {
    private List<Trip> trips;

    public Driver() {
        this.trips = new ArrayList<>();
    }

    private LocalDate getDateOnly(Date date) {
        if (date == null) return null;
        Instant instant = date.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void addTrip(Trip trip) {
        if (trip != null) {
            this.trips.add(trip);
        }
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
        for (Stop stop1 : stops1) {
            String name1 = stop1.getStopStation();
            if (name1 == null) continue;
            for (Stop stop2 : stops2) {
                String name2 = stop2.getStopStation();
                if (name2 != null && name1.equals(name2)) {
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
        Date departureDate = newTrip.getDepartureDate();
        if (depTime == null || arrTime == null || departureDate == null) {
            return false;
        }
        try {
            LocalTime departure = LocalTime.parse(depTime);
            LocalTime arrival = LocalTime.parse(arrTime);
            if (!departure.isBefore(arrival)) {
                return false;
            }
        } catch (DateTimeParseException e) {
            return false;
        }
        LocalDate depDate = getDateOnly(departureDate);
        String newDepTime = newTrip.getDepartureTime();
        String newArrTime = newTrip.getArrivalTime();
        LocalTime newDep = LocalTime.parse(newDepTime);
        LocalTime newArr = LocalTime.parse(newArrTime);
        for (Trip existing : trips) {
            if (existing == null) continue;
            Date existingDate = existing.getDepartureDate();
            if (existingDate == null) continue;
            LocalDate existingDepDate = getDateOnly(existingDate);
            if (!existingDepDate.equals(depDate)) continue;
            String existingDepTime = existing.getDepartureTime();
            String existingArrTime = existing.getArrivalTime();
            if (existingDepTime == null || existingArrTime == null) continue;
            LocalTime existDep = LocalTime.parse(existingDepTime);
            LocalTime existArr = LocalTime.parse(existingArrTime);
            if (newDep.isAfter(existArr) || existDep.isAfter(newArr)) {
                return false;
            }
        }
        return true;
    }

    public Trip getTrip(int index) {
        if (index >= 0 && index < trips.size()) {
            return trips.get(index);
        }
        return null;
    }

    public int getTripCount() {
        return trips.size();
    }
}
