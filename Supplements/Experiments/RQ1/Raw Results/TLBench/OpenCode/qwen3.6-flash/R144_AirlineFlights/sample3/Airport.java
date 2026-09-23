import java.util.Date;
import java.util.List;

public class Airport {
    private String id;
    private List<City> servesForCities;

    public Airport() {
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
        if (c != null && servesForCities != null && !servesForCities.contains(c)) {
            servesForCities.add(c);
        }
    }
}
