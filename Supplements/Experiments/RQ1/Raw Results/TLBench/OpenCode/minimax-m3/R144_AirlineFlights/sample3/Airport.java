import java.util.ArrayList;
import java.util.List;

public class Airport {
    private String id;
    private List<City> servesForCities;

    public Airport() {
        this.servesForCities = new ArrayList<City>();
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<City> getCities() {
        if (this.servesForCities == null) {
            this.servesForCities = new ArrayList<City>();
        }
        return this.servesForCities;
    }

    public void setCities(List<City> cities) {
        this.servesForCities = cities;
    }

    public void addCity(City c) {
        if (c == null) {
            return;
        }
        if (this.servesForCities == null) {
            this.servesForCities = new ArrayList<City>();
        }
        if (!this.servesForCities.contains(c)) {
            this.servesForCities.add(c);
        }
    }
}
