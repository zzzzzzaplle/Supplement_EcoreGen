import java.util.*;

public class Airport {
    private String id;
    private List<City> servesForCities = new ArrayList<>();

    public Airport() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public List<City> getCities() { return servesForCities; }
    public void setCities(List<City> cities) { this.servesForCities = cities; }

    public void addCity(City c) { servesForCities.add(c); }
}
