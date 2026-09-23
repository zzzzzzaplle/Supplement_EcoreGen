import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Airline {
    private List<Flight> flights;

    public Airline() {
        this.flights = new ArrayList<>();
    }

    public List<Flight> getFlights() { return flights; }
    public void setFlights(List<Flight> flights) { this.flights = flights; }

    public void addFlight(Flight f) { flights.add(f); }
    public void removeFlight(Flight f) { flights.remove(f); }
    public boolean publishFlight(Flight f, Date now) { return false; }
    public boolean closeFlight(String flightId, Date now) { return false; }
    public List<Flight> searchFlights(String origin, String dest, Date date) { return new ArrayList<>(); }
}
