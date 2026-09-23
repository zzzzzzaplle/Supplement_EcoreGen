import static org.junit.Assert.*;
import org.junit.Test;
import java.util.*;

public class CR5_CountRentalsTest {
        private Store store;

        @Test
        public void testTC1CountRentalsForSingleCustomer() {
                store = new Store();
                Customer customer = createCustomer("John", "Doe");

                Car car1 = createCar("ABC123");
                Car car2 = createCar("XYZ456");
                Car car3 = createCar("LMN789");

                Rental rental1 = createRental(customer, car1);
                Rental rental2 = createRental(customer, car2);
                Rental rental3 = createRental(customer, car3);
 
                store.addRental(rental1);
                store.addRental(rental2);
                store.addRental(rental3);

                Map<Customer, Integer> rentalCount = store.countCarsRentedPerCustomer();
                assertEquals("Count mismatch for single customer rentals",
                                3, rentalCount.get(customer).intValue());
        }

        @Test
        public void testTC2CountRentalsForMultipleCustomers() {
                store = new Store();
                Customer customer1 = createCustomer("John", "Doe");
                Customer customer2 = createCustomer("Jane", "Smith");

                Car car1 = createCar("ABC123");
                Car car2 = createCar("XYZ456");
                Car car3 = createCar("LMN789");

                Rental rental1 = createRental(customer1, car1);
                Rental rental2 = createRental(customer1, car2);
                Rental rental3 = createRental(customer2, car3);

                store.addRental(rental1);
                store.addRental(rental2);
                store.addRental(rental3);

                Map<Customer, Integer> rentalCount = store.countCarsRentedPerCustomer();
                assertEquals("Count mismatch for customer1 rentals",
                                2, rentalCount.get(customer1).intValue());
                assertEquals("Count mismatch for customer2 rentals",
                                1, rentalCount.get(customer2).intValue());
        }

        @Test
        public void testTC3CountRentalsWithNoRecords() {
                store = new Store();
                Customer customer = createCustomer("Alex", "Brown");

                Map<Customer, Integer> rentalCount = store.countCarsRentedPerCustomer();
                assertEquals("Count should be 0 for customer with no rentals",
                                0, rentalCount.getOrDefault(customer, 0).intValue());
        }

        @Test
        public void testTC4CountRentalsIncludingReturnedCars() {
                store = new Store();
                Customer customer = createCustomer("Charlie", "Johnson");

                Car car1 = createCar("ABC123");
                Car car2 = createCar("XYZ456");

                Rental rental1 = createRental(customer, car1, new Date()); // Mark as returned
                Rental rental2 = createRental(customer, car2); // Not returned
 
                store.addRental(rental1);
                store.addRental(rental2);

                Map<Customer, Integer> rentalCount = store.countCarsRentedPerCustomer();
                assertEquals("Returned cars should still be counted",
                                2, rentalCount.get(customer).intValue());
        }

        @Test
        public void testTC5CountRentalsForCustomerWithOverdueCars() {
                store = new Store();
                Customer customer = createCustomer("Eva", "Davis");

                Car car = createCar("ABC123");

                // Set due date to past
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_YEAR, -1);
                Rental rental = createRental(customer, car, cal.getTime(), null); // Not returned
 
                store.addRental(rental);

                Map<Customer, Integer> rentalCount = store.countCarsRentedPerCustomer();
                assertEquals("Overdue rental should still be counted",
                                1, rentalCount.get(customer).intValue());

                // Verify overdue status
                assertTrue("Rental should be overdue",
                                rental.getBackDate() == null && new Date().after(rental.getDueDate()));
        }

        private Customer createCustomer(String name, String surname) {
                Customer customer = new Customer();
                customer.setName(name);
                customer.setSurname(surname);
                return customer;
        }

        private Car createCar(String plate) {
                Car car = new Car();
                car.setPlate(plate);
                return car;
        }

        private Rental createRental(Customer customer, Car car) {
                Rental rental = new Rental();
                rental.setCustomer(customer);
                rental.setCar(car);
                return rental;
        }

        private Rental createRental(Customer customer, Car car, Date backDate) {
                Rental rental = new Rental();
                rental.setCustomer(customer);
                rental.setCar(car);
                rental.setBackDate(backDate);
                return rental;
        }

        private Rental createRental(Customer customer, Car car, Date dueDate, Date backDate) {
                Rental rental = new Rental();
                rental.setCustomer(customer);
                rental.setCar(car);
                rental.setDueDate(dueDate);
                rental.setBackDate(backDate);
                return rental;
        }
}

/*
 * compile_result:
 * 
 * 
 * 
 * run_result:
 * JUnit version 4.13.2
 * .....
 * Time: 0.037
 * 
 * OK (5 tests)
 * 
 * 
 */