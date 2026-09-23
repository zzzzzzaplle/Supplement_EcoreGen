import java.util.ArrayList;
import java.util.List;

public class Airport {
    private String id;
    private List<City> cities;

    public Airport() {
        this.cities = new ArrayList<City>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<City> getCities() {
        if (cities == null) {
            cities = new ArrayList<City>();
        }
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    public void addCity(City c) {
        if (c != null) {
            getCities().add(c);
        }
    }
}
