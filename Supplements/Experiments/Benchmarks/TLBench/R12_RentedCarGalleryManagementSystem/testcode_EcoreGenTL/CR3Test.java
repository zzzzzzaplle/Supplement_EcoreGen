package edu.carrental.test;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import edu.carrental.CarrentalFactory;
import edu.carrental.Store;
import edu.carrental.Car;
import edu.carrental.Customer;
import edu.carrental.Rental;
import edu.carrental.OverdueNotice;

import org.eclipse.emf.common.util.EList;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class CR3Test {

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

    private Customer createCustomer(Store store, String name, String surname) {
        Customer customer = factory.createCustomer();
        customer.setName(name);
        customer.setSurname(surname);
        return customer;
    }

    private Rental createRental(Store store, Car car, Customer customer, String dueDateStr, String backDateStr) {
        Rental rental = factory.createRental();
        rental.setCar(car);
        rental.setCustomer(customer);
        try {
            if (dueDateStr != null) {
                rental.setDueDate(dateFormat.parse(dueDateStr));
            }
            if (backDateStr != null) {
                rental.setBackDate(dateFormat.parse(backDateStr));
            }
        } catch (ParseException e) {
            throw new RuntimeException("Date parse error: " + e.getMessage());
        }
        store.getRentals().add(rental);
        return rental;
    }

    private Date parseDate(String dateStr) {
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException("Date parse error: " + e.getMessage());
        }
    }

    // ---- CR3: Find customers with overdue rentals ----

    /**
     * Test Case 1: "Find one customer whose rental is overdue"
     * Setup:
     *   1. Create customer "John Doe".
     *   2. Add one rental for John Doe with due date 2023-10-01 and an empty back date.
     *   3. Create an overdue notice record and add it to the store.
     * Expected Output: The store returns one overdue customer, John Doe.
     */
    @Test
    public void testCase1_OneOverdueCustomer() {
        // Setup
        Store store = createStore("Test Store");
        Car car = createCar(store, "CAR001", "Sedan", 100);
        Customer customer = createCustomer(store, "John", "Doe");

        // Rental with due date 2023-10-01, backDate null (active)
        createRental(store, car, customer, "2023-10-01", null);

        // Create an overdue notice
        OverdueNotice notice = factory.createOverdueNotice();
        notice.setCustomer(customer);
        notice.setNoticeId("N001");
        store.getNotices().add(notice);

        // Action: check on 2023-10-05
        Date currentDate = parseDate("2023-10-05");
        EList<Customer> overdueCustomers = store.findCustomersWithOverdueRentals(currentDate);

        // Expected: one overdue customer, John Doe
        assertEquals("Should return one overdue customer", 1, overdueCustomers.size());
        assertEquals("Overdue customer should be John", "John", overdueCustomers.get(0).getName());
        assertEquals("Overdue customer surname should be Doe", "Doe", overdueCustomers.get(0).getSurname());
    }

    /**
     * Test Case 2: "Ignore rentals whose due date is still in the future"
     * Setup:
     *   1. Create customer "Jane Smith".
     *   2. Add one rental for Jane Smith with due date 2025-10-10 and an empty back date.
     * Expected Output: The store returns an empty list.
     */
    @Test
    public void testCase2_FutureDueDateNotOverdue() {
        // Setup
        Store store = createStore("Test Store");
        Car car = createCar(store, "CAR002", "SUV", 120);
        Customer customer = createCustomer(store, "Jane", "Smith");

        // Rental with due date 2025-10-10, backDate null (active)
        createRental(store, car, customer, "2025-10-10", null);

        // Action: check on 2025-10-01 (before due date)
        Date currentDate = parseDate("2025-10-01");
        EList<Customer> overdueCustomers = store.findCustomersWithOverdueRentals(currentDate);

        // Expected: empty list
        assertTrue("Should return empty list when due date is in the future", overdueCustomers.isEmpty());
    }

    /**
     * Test Case 3: "Return only the customers from active overdue rentals"
     * Setup:
     *   1. Create customer "Alice Johnson".
     *   2. Create customer "John Doe".
     *   3. Add one rental for Alice Johnson with due date 2023-10-03 and an empty back date.
     *   4. Add one rental for John Doe with due date 2023-10-03 and an empty back date.
     *   5. Add one returned rental for Alice Johnson with due date 2024-10-02 and back date 2024-10-01.
     * Expected Output: The store returns Alice Johnson and John Doe as overdue customers.
     */
    @Test
    public void testCase3_ActiveOverdueRentalsOnly() {
        // Setup
        Store store = createStore("Test Store");
        Car car1 = createCar(store, "CAR003", "Sedan", 80);
        Car car2 = createCar(store, "CAR004", "SUV", 90);
        Car car3 = createCar(store, "CAR005", "Truck", 110);

        Customer alice = createCustomer(store, "Alice", "Johnson");
        Customer john = createCustomer(store, "John", "Doe");

        // Alice's active overdue rental
        createRental(store, car1, alice, "2023-10-03", null);
        // John's active overdue rental
        createRental(store, car2, john, "2023-10-03", null);
        // Alice's returned rental (backDate is not null)
        createRental(store, car3, alice, "2024-10-02", "2024-10-01");

        // Action: check on 2023-10-05
        Date currentDate = parseDate("2023-10-05");
        EList<Customer> overdueCustomers = store.findCustomersWithOverdueRentals(currentDate);

        // Expected: Alice Johnson and John Doe (both have active overdue rentals)
        assertEquals("Should return two overdue customers", 2, overdueCustomers.size());
        assertTrue("Should contain Alice Johnson",
            overdueCustomers.stream().anyMatch(c -> "Alice".equals(c.getName()) && "Johnson".equals(c.getSurname())));
        assertTrue("Should contain John Doe",
            overdueCustomers.stream().anyMatch(c -> "John".equals(c.getName()) && "Doe".equals(c.getSurname())));
    }

    /**
     * Test Case 4: "Do not mark a returned rental as overdue"
     * Setup:
     *   1. Create customer "Bob Brown".
     *   2. Add one rental for Bob Brown with due date 2023-10-03 and back date 2023-10-04.
     * Expected Output: The store returns an empty list.
     */
    @Test
    public void testCase4_ReturnedRentalNotOverdue() {
        // Setup
        Store store = createStore("Test Store");
        Car car = createCar(store, "CAR006", "Sedan", 100);
        Customer customer = createCustomer(store, "Bob", "Brown");

        // Rental with due date 2023-10-03, backDate 2023-10-04 (returned)
        createRental(store, car, customer, "2023-10-03", "2023-10-04");

        // Action: check on 2023-10-05
        Date currentDate = parseDate("2023-10-05");
        EList<Customer> overdueCustomers = store.findCustomersWithOverdueRentals(currentDate);

        // Expected: empty list (rental was returned)
        assertTrue("Should return empty list when rental has been returned", overdueCustomers.isEmpty());
    }

    /**
     * Test Case 5: "Do not mark a rental overdue before its due date"
     * Setup:
     *   1. Create customer "Charlie Green".
     *   2. Add one rental for Charlie Green with due date 2025-10-15 and an empty back date.
     * Expected Output: The store returns an empty list.
     */
    @Test
    public void testCase5_NotOverdueBeforeDueDate() {
        // Setup
        Store store = createStore("Test Store");
        Car car = createCar(store, "CAR007", "Sedan", 100);
        Customer customer = createCustomer(store, "Charlie", "Green");

        // Rental with due date 2025-10-15, backDate null (active)
        createRental(store, car, customer, "2025-10-15", null);

        // Action: check on 2025-10-10 (before due date)
        Date currentDate = parseDate("2025-10-10");
        EList<Customer> overdueCustomers = store.findCustomersWithOverdueRentals(currentDate);

        // Expected: empty list
        assertTrue("Should return empty list when current date is before due date", overdueCustomers.isEmpty());
    }

    /**
     * Test Case 6: "Return an empty list when the current date is missing"
     * Setup:
     *   1. Create customer "Emma White".
     *   2. Add one rental for Emma White with due date 2024-01-10 and an empty back date.
     * Expected Output: The store returns an empty list.
     */
    @Test
    public void testCase6_NullCurrentDate() {
        // Setup
        Store store = createStore("Test Store");
        Car car = createCar(store, "CAR008", "Sedan", 100);
        Customer customer = createCustomer(store, "Emma", "White");

        // Rental with due date 2024-01-10, backDate null (active)
        createRental(store, car, customer, "2024-01-10", null);

        // Action: check with null current date
        EList<Customer> overdueCustomers = store.findCustomersWithOverdueRentals(null);

        // Expected: empty list
        assertTrue("Should return empty list when current date is null", overdueCustomers.isEmpty());
    }
}
