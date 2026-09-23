import java.util.List;
import java.util.ArrayList;

public class Airport {
    private String id;
    private List<City> servesForCities;
    
    public Airport() {
        this.servesForCities = new ArrayList<City>();
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
        if (this.servesForCities == null) {
            this.servesForCities = new ArrayList<City>();
        }
        this.servesForCities.add(c);
    }
}
