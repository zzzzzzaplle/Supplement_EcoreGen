import java.util.ArrayList;
import java.util.List;

public class Car {
    private String plate;
    private String model;
    private double dailyPrice;
    
    public Car() {
    }
    
    public String getPlate() {
        return plate;
    }
    
    public void setPlate(String plate) {
        this.plate = plate;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public double getDailyPrice() {
        return dailyPrice;
    }
    
    public void setDailyPrice(double dailyPrice) {
        this.dailyPrice = dailyPrice;
    }
}
