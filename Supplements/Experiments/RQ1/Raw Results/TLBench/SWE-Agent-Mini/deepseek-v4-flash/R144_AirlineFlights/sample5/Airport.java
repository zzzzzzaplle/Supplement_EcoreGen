import java.util.ArrayList;
import java.util.List;

public class Airport {
    private String id;
    private List<City> servesForCities;
    
    public Airport() {
        this.servesForCities = new ArrayList<>();
    }
    
    public Airport(String id) {
        this.id = id;
        this.servesForCities = new ArrayList<>();
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public List<City> getCities() {
        return servesForCities;
    }
    
    public void setCities(List<City> cities) {
        this.servesForCities = cities;
    }
    
    public void addCity(City c) {
        if (servesForCities == null) {
            servesForCities = new ArrayList<>();
        }
        servesForCities.add(c);
    }
}
