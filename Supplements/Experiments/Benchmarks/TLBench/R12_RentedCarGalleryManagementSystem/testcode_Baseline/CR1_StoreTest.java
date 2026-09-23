
// Required imports
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.*;

// Other required imports

public class CR1_StoreTest {
    // Test object and data declarations
    private Store store;

    @Before
    public void setUp() {
        // Setup for various test scenarios will be covered in respective test methods
    }

    // Test Case 1: Single Available Car Check
    @Test
    public void testTC1SingleAvailableCar() {
        store = new Store();
        Car car1 = createCar("ABC123", "Toyota Camry", 500);
        Car car2 = createCar("XYZ789", "Honda Accord", 600);
        Car car3 = createCar("DEF456", "Ford Focus", 450);

        store.addCar(car1);
        store.addCar(car2);
        store.addCar(car3);

        Rental rental = createRental(car2, null); // Mark the car as rented
        store.addRental(rental);

        List<Car> availableCars = store.identifyAvailableCars();
        assertEquals("Available cars count mismatch", 2, availableCars.size());

        assertEquals("First car plate mismatch", "DEF456", availableCars.get(0).getPlate());
        assertEquals("First car model mismatch", "Ford Focus", availableCars.get(0).getModel());
        assertEquals("First car daily price mismatch", 450, availableCars.get(0).getDailyPrice(), 0.01);

        assertEquals("Second car plate mismatch", "ABC123", availableCars.get(1).getPlate());
        assertEquals("Second car model mismatch", "Toyota Camry", availableCars.get(1).getModel());
        assertEquals("Second car daily price mismatch", 500, availableCars.get(1).getDailyPrice(), 0.01);
    }

    // Test Case 2: All Cars Rented Check
    @Test
    public void testTC2AllCarsRented() {
        store = new Store();
        Car car1 = createCar("AAA111", "Nissan Altima", 600);
        Car car2 = createCar("BBB222", "Chevy Malibu", 700);
        Car car3 = createCar("CCC333", "Kia Optima", 650);

        store.addCar(car1);
        store.addCar(car2);
        store.addCar(car3);

        Rental rental1 = createRental(car1, null);
        Rental rental2 = createRental(car2, null);
        Rental rental3 = createRental(car3, null);

        store.addRental(rental1);
        store.addRental(rental2);
        store.addRental(rental3);

        List<Car> availableCars = store.identifyAvailableCars();
        assertTrue("Expected no available cars, but found some.", availableCars.isEmpty());
    }

    // Test Case 3: Multiple Cars with Different Rental Status
    @Test
    public void testTC3MultipleCarsDifferentStatus() {
        store = new Store();
        Car car1 = createCar("LMN456", "Porsche 911", 1500);
        Car car2 = createCar("OPQ789", "Mercedes Benz", 1200);
        Car car3 = createCar("RST012", "BMW 5 Series", 1300);

        store.addCar(car1);
        store.addCar(car2);
        store.addCar(car3);

        Rental rental = createRental(car2, null);

        store.addRental(rental);

        List<Car> availableCars = store.identifyAvailableCars();
        assertEquals("Available cars count mismatch", 2, availableCars.size());

        assertEquals("First car plate mismatch", "RST012", availableCars.get(0).getPlate());
        assertEquals("First car model mismatch", "BMW 5 Series", availableCars.get(0).getModel());
        assertEquals("First car daily price mismatch", 1300, availableCars.get(0).getDailyPrice(), 0.01);

        assertEquals("Second car plate mismatch", "LMN456", availableCars.get(1).getPlate());
        assertEquals("Second car model mismatch", "Porsche 911", availableCars.get(1).getModel());
        assertEquals("Second car daily price mismatch", 1500, availableCars.get(1).getDailyPrice(), 0.01);
    }

    // Test Case 4: No Cars in Store
    @Test
    public void testTC4NoCarsInStore() {
        store = new Store();

        List<Car> availableCars = store.identifyAvailableCars();
        assertTrue("Expected no available cars, but found some.", availableCars.isEmpty());
    }

    // Test Case 5: Single Car Rented and One Available
    @Test
    public void testTC5SingleCarRentedOneAvailable() {
        store = new Store();
        Car car1 = createCar("GHI789", "Subaru Impreza", 400);
        Car car2 = createCar("JKL012", "Mazda 3", 350);

        store.addCar(car1);
        store.addCar(car2);

        Rental rental = createRental(car1, null);

        store.addRental(rental);

        List<Car> availableCars = store.identifyAvailableCars();
        assertEquals("Available cars count mismatch", 1, availableCars.size());

        assertEquals("Available car plate mismatch", "JKL012", availableCars.get(0).getPlate());
        assertEquals("Available car model mismatch", "Mazda 3", availableCars.get(0).getModel());
        assertEquals("Available car daily price mismatch", 350, availableCars.get(0).getDailyPrice(), 0.01);
    }

    private Car createCar(String plate, String model, double dailyPrice) {
        Car car = new Car();
        car.setPlate(plate);
        car.setModel(model);
        car.setDailyPrice(dailyPrice);
        return car;
    }

    private Rental createRental(Car car, Date backDate) {
        Rental rental = new Rental();
        rental.setCar(car);
        rental.setBackDate(backDate);
        return rental;
    }
}
