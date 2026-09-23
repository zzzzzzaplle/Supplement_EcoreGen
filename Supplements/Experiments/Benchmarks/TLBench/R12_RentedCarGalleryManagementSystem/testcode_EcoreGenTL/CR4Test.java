package edu.carrental.test;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import edu.carrental.CarrentalFactory;
import edu.carrental.Store;
import edu.carrental.Car;

public class CR4Test {

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

    // ---- CR4: Determine the average daily price of cars ----

    /**
     * Test Case 1: "Average three different car prices"
     * Setup:
     *   1. Create a store.
     *   2. Add a car priced at 50.
     *   3. Add a car priced at 70.
     *   4. Add a car priced at 80.
     * Expected Output: The store returns average daily price 66.67.
     */
    @Test
    public void testCase1_AverageThreePrices() {
        // Setup
        Store store = createStore("Test Store");
        createCar(store, "CAR001", "Sedan", 50);
        createCar(store, "CAR002", "SUV", 70);
        createCar(store, "CAR003", "Truck", 80);

        // Action
        double avgPrice = store.determineAverageDailyPrice();

        // Expected: (50 + 70 + 80) / 3 = 66.67
        assertEquals("Average daily price should be 66.67", 66.67, avgPrice, 0.01);
    }

    /**
     * Test Case 2: "Return zero for an empty store"
     * Setup:
     *   1. Create a store without adding any cars.
     * Expected Output: The store returns 0.0.
     */
    @Test
    public void testCase2_EmptyStoreReturnsZero() {
        // Setup
        Store store = createStore("Empty Store");

        // Action
        double avgPrice = store.determineAverageDailyPrice();

        // Expected: 0.0
        assertEquals("Average daily price should be 0.0 for empty store", 0.0, avgPrice, 0.001);
    }

    /**
     * Test Case 3: "Use the only car price when one car exists"
     * Setup:
     *   1. Create a store.
     *   2. Add one car priced at 100.
     * Expected Output: The store returns 100.0.
     */
    @Test
    public void testCase3_SingleCarPrice() {
        // Setup
        Store store = createStore("Test Store");
        createCar(store, "CAR004", "Sedan", 100);

        // Action
        double avgPrice = store.determineAverageDailyPrice();

        // Expected: 100.0
        assertEquals("Average daily price should be 100.0 for single car", 100.0, avgPrice, 0.001);
    }

    /**
     * Test Case 4: "Average a store with varied prices"
     * Setup:
     *   1. Create a store.
     *   2. Add a car priced at 30.
     *   3. Add a car priced at 150.
     *   4. Add a car priced at 120.
     * Expected Output: The store returns 100.0.
     */
    @Test
    public void testCase4_VariedPrices() {
        // Setup
        Store store = createStore("Test Store");
        createCar(store, "CAR005", "Mini", 30);
        createCar(store, "CAR006", "SUV", 150);
        createCar(store, "CAR007", "Sedan", 120);

        // Action
        double avgPrice = store.determineAverageDailyPrice();

        // Expected: (30 + 150 + 120) / 3 = 100.0
        assertEquals("Average daily price should be 100.0", 100.0, avgPrice, 0.001);
    }

    /**
     * Test Case 5: "Average cars that all share the same price"
     * Setup:
     *   1. Create a store.
     *   2. Add three cars priced at 60.
     * Expected Output: The store returns 60.0.
     */
    @Test
    public void testCase5_SamePriceCars() {
        // Setup
        Store store = createStore("Test Store");
        createCar(store, "CAR008", "Sedan", 60);
        createCar(store, "CAR009", "SUV", 60);
        createCar(store, "CAR010", "Truck", 60);

        // Action
        double avgPrice = store.determineAverageDailyPrice();

        // Expected: (60 + 60 + 60) / 3 = 60.0
        assertEquals("Average daily price should be 60.0", 60.0, avgPrice, 0.001);
    }
}
