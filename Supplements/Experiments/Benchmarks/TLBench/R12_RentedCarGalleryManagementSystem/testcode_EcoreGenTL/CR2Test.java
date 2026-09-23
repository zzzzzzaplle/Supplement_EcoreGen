package edu.carrental.test;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import edu.carrental.CarrentalFactory;
import edu.carrental.Store;
import edu.carrental.Car;
import edu.carrental.Customer;
import edu.carrental.Rental;

public class CR2Test {

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

    private Rental createRental(Store store, Car car, double totalPrice) {
        Rental rental = factory.createRental();
        rental.setCar(car);
        rental.setTotalPrice(totalPrice);
        store.getRentals().add(rental);
        return rental;
    }

    // ---- CR2: Calculate total rental revenue ----

    /**
     * Test Case 1: "Add the revenue of three rentals"
     * Setup:
     *   1. Create a store.
     *   2. Add a rental for car "ABC123" with total price 300.
     *   3. Add a rental for car "XYZ789" with total price 300.
     *   4. Add a rental for car "LMN456" with total price 200.
     * Expected Output: The store returns total revenue 800.0.
     */
    @Test
    public void testCase1_ThreeRentalRevenue() {
        // Setup
        Store store = createStore("Test Store");
        Car car1 = createCar(store, "ABC123", "Sedan", 100);
        Car car2 = createCar(store, "XYZ789", "SUV", 100);
        Car car3 = createCar(store, "LMN456", "Truck", 100);

        createRental(store, car1, 300);
        createRental(store, car2, 300);
        createRental(store, car3, 200);

        // Action
        double totalRevenue = store.calculateTotalRevenue();

        // Expected: 300 + 300 + 200 = 800.0
        assertEquals("Total revenue should be 800.0", 800.0, totalRevenue, 0.001);
    }

    /**
     * Test Case 2: "Return zero when there are no rentals"
     * Setup:
     *   1. Create a store without adding any rental records.
     * Expected Output: The store returns total revenue 0.0.
     */
    @Test
    public void testCase2_ZeroRevenueWhenNoRentals() {
        // Setup
        Store store = createStore("Empty Store");

        // Action
        double totalRevenue = store.calculateTotalRevenue();

        // Expected: 0.0
        assertEquals("Total revenue should be 0.0 when no rentals exist", 0.0, totalRevenue, 0.001);
    }

    /**
     * Test Case 3: "Sum rentals that share the same daily price"
     * Setup:
     *   1. Create a store.
     *   2. Add a rental for car "CAR001" with total price 240.
     *   3. Add a rental for car "CAR002" with total price 480.
     * Expected Output: The store returns total revenue 720.0.
     */
    @Test
    public void testCase3_RentalsWithSameDailyPrice() {
        // Setup
        Store store = createStore("Test Store");
        Car car1 = createCar(store, "CAR001", "Sedan", 80);
        Car car2 = createCar(store, "CAR002", "Sedan", 80);

        createRental(store, car1, 240);
        createRental(store, car2, 480);

        // Action
        double totalRevenue = store.calculateTotalRevenue();

        // Expected: 240 + 480 = 720.0
        assertEquals("Total revenue should be 720.0", 720.0, totalRevenue, 0.001);
    }

    /**
     * Test Case 4: "Combine rentals with mixed prices"
     * Setup:
     *   1. Create a store.
     *   2. Add a rental for car "SED123" with total price 450.
     *   3. Add a rental for car "SUV456" with total price 450.
     *   4. Add a rental for car "TRK789" with total price 250.
     * Expected Output: The store returns total revenue 1150.0.
     */
    @Test
    public void testCase4_MixedPriceRentals() {
        // Setup
        Store store = createStore("Test Store");
        Car car1 = createCar(store, "SED123", "Sedan", 150);
        Car car2 = createCar(store, "SUV456", "SUV", 150);
        Car car3 = createCar(store, "TRK789", "Truck", 125);

        createRental(store, car1, 450);
        createRental(store, car2, 450);
        createRental(store, car3, 250);

        // Action
        double totalRevenue = store.calculateTotalRevenue();

        // Expected: 450 + 450 + 250 = 1150.0
        assertEquals("Total revenue should be 1150.0", 1150.0, totalRevenue, 0.001);
    }

    /**
     * Test Case 5: "Count one-day rentals in the total"
     * Setup:
     *   1. Create a store.
     *   2. Add a rental for car "MINI001" with total price 180.
     *   3. Add a rental for car "MOTO002" with total price 220.
     * Expected Output: The store returns total revenue 400.0.
     */
    @Test
    public void testCase5_OneDayRentals() {
        // Setup
        Store store = createStore("Test Store");
        Car car1 = createCar(store, "MINI001", "Mini", 180);
        Car car2 = createCar(store, "MOTO002", "Moto", 220);

        createRental(store, car1, 180);
        createRental(store, car2, 220);

        // Action
        double totalRevenue = store.calculateTotalRevenue();

        // Expected: 180 + 220 = 400.0
        assertEquals("Total revenue should be 400.0", 400.0, totalRevenue, 0.001);
    }
}
