// Required imports
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.*;

// Other required imports

public class CR4_AveragePriceTest {
    // Test object and data declarations
    private Store store;
    private Car car1;
    private Car car2;
    private Car car3;

    @Before
    public void setUp() {
        store = new Store();
    }
    
    @Test
    public void testAveragePriceCalculationWithMultipleCars() {
        // Test logic 
        car1 = createCar("CAR001", "Model1", 50);
        store.getCars().add(car1);
        
        car2 = createCar("CAR002", "Model2", 70);
        store.getCars().add(car2);
        
        car3 = createCar("CAR003", "Model3", 80);
        store.getCars().add(car3);
        
        double averagePrice = store.determineAverageDailyPrice(); // Testing: determineAverageDailyPrice(): double
        assertEquals("Average daily price calculation failed", 66.67, averagePrice, 0.01); // Assertions
    }

    @Test
    public void testAveragePriceCalculationWithNoCars() {
        // Test logic 
        double averagePrice = store.determineAverageDailyPrice(); // Testing: determineAverageDailyPrice(): double
        assertEquals("Average daily price should be 0 for empty store", 0.00, averagePrice, 0.01); // Assertions
    }

    @Test
    public void testAveragePriceCalculationWithOneCar() {
        // Test logic 
        car1 = createCar("CAR001", "Model1", 100);
        store.getCars().add(car1);
        
        double averagePrice = store.determineAverageDailyPrice(); // Testing: determineAverageDailyPrice(): double
        assertEquals("Average daily price should match single car's price", 100.00, averagePrice, 0.01); // Assertions
    }

    @Test
    public void testAveragePriceCalculationWithCarsHavingVaryingPrices() {
        // Test logic 
        car1 = createCar("CAR001", "Model1", 30);
        store.getCars().add(car1);
        
        car2 = createCar("CAR002", "Model2", 150);
        store.getCars().add(car2);
        
        car3 = createCar("CAR003", "Model3", 120);
        store.getCars().add(car3);
        
        double averagePrice = store.determineAverageDailyPrice(); // Testing: determineAverageDailyPrice(): double
        assertEquals("Average daily price calculation failed for varying prices", 100.00, averagePrice, 0.01); // Assertions
    }

    @Test
    public void testAveragePriceCalculationWithDuplicatePriceCars() {
        // Test logic 
        car1 = createCar("CAR001", "Model1", 60);
        store.getCars().add(car1);
        
        car2 = createCar("CAR002", "Model2", 60);
        store.getCars().add(car2);
        
        car3 = createCar("CAR003", "Model3", 60);
        store.getCars().add(car3);
        
        double averagePrice = store.determineAverageDailyPrice(); // Testing: determineAverageDailyPrice(): double
        assertEquals("Average daily price calculation failed for duplicate prices", 60.00, averagePrice, 0.01); // Assertions
    }

    private Car createCar(String plate, String model, double dailyPrice) {
        Car car = new Car();
        car.setPlate(plate);
        car.setModel(model);
        car.setDailyPrice(dailyPrice);
        return car;
    }
}

/*
compile_result:



run_result:
JUnit version 4.13.2
.....
Time: 0.008

OK (5 tests)


*/