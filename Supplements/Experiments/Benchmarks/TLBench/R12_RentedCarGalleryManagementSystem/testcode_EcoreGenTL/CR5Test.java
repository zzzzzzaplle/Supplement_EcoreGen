package edu.carrental.test;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import edu.carrental.CarrentalFactory;
import edu.carrental.Store;
import edu.carrental.Car;
import edu.carrental.Customer;
import edu.carrental.Rental;

import java.util.Date;
import java.util.Map;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class CR5Test {

    private CarrentalFactory factory;
    private SimpleDateFormat dateFormat;

    @Before
    public void setUp() {
        factory = CarrentalFactory.eINSTANCE;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
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

    private Customer createCustomer(String name, String surname) {
        Customer customer = factory.createCustomer();
        customer.setName(name);
        customer.setSurname(surname);
        return customer;
    }

    private Rental createRental(Store store, Car car, Customer customer) {
        Rental rental = factory.createRental();
        rental.setCar(car);
        rental.setCustomer(customer);
        store.getRentals().add(rental);
        return rental;
    }

    // ---- CR5: Count rentals per customer ----

    /**
     * Test Case 1: "Count three rentals for one customer"
     * Setup:
     *   1. Create customer "John Doe".
     *   2. Add three rental records for John Doe linked to cars "ABC123", "XYZ456", and "LMN789".
     * Expected Output: The result map stores John Doe with count 3.
     */
    @Test
    public void testCase1_ThreeRentalsForOneCustomer() {
        // Setup
        Store store = createStore("Test Store");
        Car car1 = createCar(store, "ABC123", "Sedan", 100);
        Car car2 = createCar(store, "XYZ456", "SUV", 120);
        Car car3 = createCar(store, "LMN789", "Truck", 150);

        Customer john = createCustomer("John", "Doe");

        createRental(store, car1, john);
        createRental(store, car2, john);
        createRental(store, car3, john);

        // Action
        Map<Customer, Integer> rentalCounts = store.countCarsRentedPerCustomer();

        // Expected: John Doe -> 3
        assertEquals("Should have one customer in the map", 1, rentalCounts.size());
        assertEquals("John Doe should have 3 rentals", Integer.valueOf(3), rentalCounts.get(john));
    }

    /**
     * Test Case 2: "Count rentals for two different customers"
     * Setup:
     *   1. Create customer "John Doe".
     *   2. Create customer "Jane Smith".
     *   3. Add two rental records for John Doe linked to cars "ABC123" and "XYZ456".
     *   4. Add one rental record for Jane Smith linked to car "LMN789".
     * Expected Output: The result map stores John Doe with count 2 and Jane Smith with count 1.
     */
    @Test
    public void testCase2_TwoCustomersWithDifferentCounts() {
        // Setup
        Store store = createStore("Test Store");
        Car car1 = createCar(store, "ABC123", "Sedan", 100);
        Car car2 = createCar(store, "XYZ456", "SUV", 120);
        Car car3 = createCar(store, "LMN789", "Truck", 150);

        Customer john = createCustomer("John", "Doe");
        Customer jane = createCustomer("Jane", "Smith");

        createRental(store, car1, john);
        createRental(store, car2, john);
        createRental(store, car3, jane);

        // Action
        Map<Customer, Integer> rentalCounts = store.countCarsRentedPerCustomer();

        // Expected: John Doe -> 2, Jane Smith -> 1
        assertEquals("Should have two customers in the map", 2, rentalCounts.size());
        assertEquals("John Doe should have 2 rentals", Integer.valueOf(2), rentalCounts.get(john));
        assertEquals("Jane Smith should have 1 rental", Integer.valueOf(1), rentalCounts.get(jane));
    }

    /**
     * Test Case 3: "Return an empty map when no rentals exist"
     * Setup:
     *   1. Create a store and customer "Alex Brown" without adding any rental records.
     * Expected Output: The store returns an empty map, so Alex Brown resolves to count 0 by default.
     */
    @Test
    public void testCase3_EmptyMapWhenNoRentals() {
        // Setup
        Store store = createStore("Test Store");
        Customer alex = createCustomer("Alex", "Brown");

        // Action
        Map<Customer, Integer> rentalCounts = store.countCarsRentedPerCustomer();

        // Expected: empty map
        assertTrue("Should return an empty map when no rentals exist", rentalCounts.isEmpty());
    }

    /**
     * Test Case 4: "Keep returned rentals in the historical count"
     * Setup:
     *   1. Create customer "Charlie Johnson".
     *   2. Add one returned rental for Charlie Johnson linked to car "ABC123".
     *   3. Add one active rental for Charlie Johnson linked to car "XYZ456".
     * Expected Output: The result map stores Charlie Johnson with count 2.
     */
    @Test
    public void testCase4_ReturnedRentalsCounted() {
        // Setup
        Store store = createStore("Test Store");
        Car car1 = createCar(store, "ABC123", "Sedan", 100);
        Car car2 = createCar(store, "XYZ456", "SUV", 120);

        Customer charlie = createCustomer("Charlie", "Johnson");

        // Returned rental (backDate is set)
        Rental returnedRental = createRental(store, car1, charlie);
        try {
            returnedRental.setBackDate(dateFormat.parse("2024-10-01"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // Active rental (backDate is null)
        createRental(store, car2, charlie);

        // Action
        Map<Customer, Integer> rentalCounts = store.countCarsRentedPerCustomer();

        // Expected: Charlie Johnson -> 2 (both returned and active rentals counted)
        assertEquals("Should have one customer in the map", 1, rentalCounts.size());
        assertEquals("Charlie Johnson should have 2 rentals (including returned)",
            Integer.valueOf(2), rentalCounts.get(charlie));
    }

    /**
     * Test Case 5: "Count an overdue rental like any other rental"
     * Setup:
     *   1. Create customer "Eva Davis".
     *   2. Add one active rental for Eva Davis linked to car "ABC123" with a due date set one day in the past.
     * Expected Output: The result map stores Eva Davis with count 1.
     */
    @Test
    public void testCase5_OverdueRentalCounted() {
        // Setup
        Store store = createStore("Test Store");
        Car car = createCar(store, "ABC123", "Sedan", 100);

        Customer eva = createCustomer("Eva", "Davis");

        // Active overdue rental (backDate null, dueDate in the past)
        Rental rental = createRental(store, car, eva);
        try {
            rental.setDueDate(dateFormat.parse("2023-10-01"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // Action
        Map<Customer, Integer> rentalCounts = store.countCarsRentedPerCustomer();

        // Expected: Eva Davis -> 1
        assertEquals("Should have one customer in the map", 1, rentalCounts.size());
        assertEquals("Eva Davis should have 1 rental (overdue counted)",
            Integer.valueOf(1), rentalCounts.get(eva));
    }
}
