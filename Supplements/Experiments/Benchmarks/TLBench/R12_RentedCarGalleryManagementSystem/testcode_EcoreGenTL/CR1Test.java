package edu.carrental.test;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import edu.carrental.CarrentalFactory;
import edu.carrental.Store;
import edu.carrental.Car;
import edu.carrental.Customer;
import edu.carrental.Rental;

import org.eclipse.emf.common.util.EList;

public class CR1Test {

    private CarrentalFactory factory;

    @Before
    public void setUp() {
        factory = CarrentalFactory.eINSTANCE;
    }

    // ---- Helper methods ----

    private Store createStore(String name) {
        Store store = factory.createStore();
        store.setName(name);
        return store;
    }

    private Car createCar(Store store, String plate, String model, double dailyPrice) {
        Car car = factory.createCar();
        car.setPlate(plate);
        car.setModel(model);
        car.setDailyPrice(dailyPrice);
        store.getCars().add(car);
        return car;
    }

    private Rental createActiveRental(Store store, Car car) {
        Rental rental = factory.createRental();
        rental.setCar(car);
        // backDate is null by default, meaning the rental is active (car not returned)
        store.getRentals().add(rental);
        return rental;
    }

    // ---- CR1: List available cars in ascending daily price ----

    /**
     * Test Case 1: "Show the two cars that are still available"
     * Setup:
     *   1. Create a store named "City Car Rentals".
     *   2. Add car "ABC123", model "Toyota Camry", daily price 500.
     *   3. Add car "XYZ789", model "Honda Accord", daily price 600.
     *   4. Add car "DEF456", model "Ford Focus", daily price 450.
     *   5. Add one active rental for car "XYZ789" by leaving its back date empty.
     * Expected Output: The store returns two available cars ordered by price:
     *   "DEF456" first and "ABC123" second.
     */
    @Test
    public void testCase1_ShowTwoAvailableCars() {
        // Setup
        Store store = createStore("City Car Rentals");
        Car car1 = createCar(store, "ABC123", "Toyota Camry", 500);
        Car car2 = createCar(store, "XYZ789", "Honda Accord", 600);
        Car car3 = createCar(store, "DEF456", "Ford Focus", 450);

        // Add one active rental for car "XYZ789" (backDate is null)
        createActiveRental(store, car2);

        // Action
        EList<Car> availableCars = store.identifyAvailableCars();

        // Expected: two available cars ordered by price: DEF456 (450) first, ABC123 (500) second
        assertEquals("Should return two available cars", 2, availableCars.size());
        assertEquals("First car should be DEF456", "DEF456", availableCars.get(0).getPlate());
        assertEquals("Second car should be ABC123", "ABC123", availableCars.get(1).getPlate());
    }

    /**
     * Test Case 2: "Return an empty list when every car is rented"
     * Setup:
     *   1. Create a store named "Downtown Rentals".
     *   2. Add car "AAA111", model "Nissan Altima", daily price 600.
     *   3. Add car "BBB222", model "Chevy Malibu", daily price 700.
     *   4. Add car "CCC333", model "Kia Optima", daily price 650.
     *   5. Add active rentals for all three cars by leaving each back date empty.
     * Expected Output: The store returns an empty list.
     */
    @Test
    public void testCase2_EmptyListWhenAllCarsRented() {
        // Setup
        Store store = createStore("Downtown Rentals");
        Car car1 = createCar(store, "AAA111", "Nissan Altima", 600);
        Car car2 = createCar(store, "BBB222", "Chevy Malibu", 700);
        Car car3 = createCar(store, "CCC333", "Kia Optima", 650);

        // Add active rentals for all three cars
        createActiveRental(store, car1);
        createActiveRental(store, car2);
        createActiveRental(store, car3);

        // Action
        EList<Car> availableCars = store.identifyAvailableCars();

        // Expected: empty list
        assertTrue("Should return an empty list when all cars are rented", availableCars.isEmpty());
    }

    /**
     * Test Case 3: "Keep only the cars without active rentals"
     * Setup:
     *   1. Create a store named "Luxury Car Rentals".
     *   2. Add car "LMN456", model "Porsche 911", daily price 1500.
     *   3. Add car "OPQ789", model "Mercedes Benz", daily price 1200.
     *   4. Add car "RST012", model "BMW 5 Series", daily price 1300.
     *   5. Add one active rental for car "OPQ789" by leaving its back date empty.
     * Expected Output: The store returns "RST012" first and "LMN456" second.
     */
    @Test
    public void testCase3_KeepCarsWithoutActiveRentals() {
        // Setup
        Store store = createStore("Luxury Car Rentals");
        Car car1 = createCar(store, "LMN456", "Porsche 911", 1500);
        Car car2 = createCar(store, "OPQ789", "Mercedes Benz", 1200);
        Car car3 = createCar(store, "RST012", "BMW 5 Series", 1300);

        // Add one active rental for car "OPQ789"
        createActiveRental(store, car2);

        // Action
        EList<Car> availableCars = store.identifyAvailableCars();

        // Expected: RST012 (1300) first, LMN456 (1500) second (ascending by daily price)
        assertEquals("Should return two available cars", 2, availableCars.size());
        assertEquals("First car should be RST012", "RST012", availableCars.get(0).getPlate());
        assertEquals("Second car should be LMN456", "LMN456", availableCars.get(1).getPlate());
    }

    /**
     * Test Case 4: "Handle a store that has no car records"
     * Setup:
     *   1. Create a store named "Empty Rentals" without adding any cars.
     * Expected Output: The store returns an empty list.
     */
    @Test
    public void testCase4_StoreWithNoCars() {
        // Setup
        Store store = createStore("Empty Rentals");

        // Action
        EList<Car> availableCars = store.identifyAvailableCars();

        // Expected: empty list
        assertTrue("Should return an empty list when store has no cars", availableCars.isEmpty());
    }

    /**
     * Test Case 5: "Show the single car that remains available"
     * Setup:
     *   1. Create a store named "Coastal Rentals".
     *   2. Add car "GHI789", model "Subaru Impreza", daily price 400.
     *   3. Add car "JKL012", model "Mazda 3", daily price 350.
     *   4. Add one active rental for car "GHI789" by leaving its back date empty.
     * Expected Output: The store returns a single available car, "JKL012".
     */
    @Test
    public void testCase5_SingleAvailableCar() {
        // Setup
        Store store = createStore("Coastal Rentals");
        Car car1 = createCar(store, "GHI789", "Subaru Impreza", 400);
        Car car2 = createCar(store, "JKL012", "Mazda 3", 350);

        // Add one active rental for car "GHI789"
        createActiveRental(store, car1);

        // Action
        EList<Car> availableCars = store.identifyAvailableCars();

        // Expected: single available car, "JKL012"
        assertEquals("Should return a single available car", 1, availableCars.size());
        assertEquals("The available car should be JKL012", "JKL012", availableCars.get(0).getPlate());
    }
}
