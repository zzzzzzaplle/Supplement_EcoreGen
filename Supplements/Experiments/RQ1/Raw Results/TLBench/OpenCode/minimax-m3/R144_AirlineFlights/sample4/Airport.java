import java.util.ArrayList;
import java.util.List;

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

    public void setCities(List<City> servesForCities) {
        this.servesForCities = servesForCities;
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
