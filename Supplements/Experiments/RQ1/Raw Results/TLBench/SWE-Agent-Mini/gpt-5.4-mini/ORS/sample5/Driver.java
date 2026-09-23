import java.util.ArrayList;
import java.util.List;

public class Driver extends User {
    private List<Trip> trips = new ArrayList<>();

    public Driver() {
    }

    public List<Trip> getTrips() { return trips; }
    public void addTrip(Trip trip) { if (trip != null) trips.add(trip); }
    public boolean checkStopOverlap(Trip trip1, Trip trip2) { return false; }
    public boolean canPostTrip(Trip newTrip) { return false; }
}
