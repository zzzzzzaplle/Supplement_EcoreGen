import java.util.*;

public class Airport {
    private String id;
    private List<City> servesForCities;

    public Airport() {
        this.servesForCities = new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public List<City> getCities() { return servesForCities; }

    public void addCity(City c) {
        this.servesForCities.add(c);
    }
}
