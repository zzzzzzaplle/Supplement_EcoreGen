import java.util.Date;
import java.util.List;
import java.util.Map;

public class TestStore {
    public static void main(String[] args) {
        Store store = new Store();
        store.setName("Downtown Rental");

        Car car1 = new Car();
        car1.setPlate("ABC123");
        car1.setModel("Toyota Corolla");
        car1.setDailyPrice(50.0);

        Car car2 = new Car();
        car2.setPlate("XYZ789");
        car2.setModel("Honda Civic");
        car2.setDailyPrice(45.0);

        Car car3 = new Car();
        car3.setPlate("DEF456");
        car3.setModel("Ford Focus");
        car3.setDailyPrice(60.0);

        store.addCar(car1);
        store.addCar(car2);
        store.addCar(car3);

        Customer cust1 = new Customer();
        cust1.setName("John");
        cust1.setSurname("Doe");
        cust1.setAddress("123 Main St");

        Customer cust2 = new Customer();
        cust2.setName("Jane");
        cust2.setSurname("Smith");
        cust2.setAddress("456 Oak Ave");

        // Rent car2 to cust1
        Rental rental1 = new Rental();
        rental1.setRentalDate(new Date());
        rental1.setDueDate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 5)); // 5 days ago
        rental1.setBackDate(null); // not returned yet
        rental1.setTotalPrice(225.0);
        rental1.setLeasingTerms("Standard");
        rental1.setCar(car2);
        rental1.setCustomer(cust1);
        store.addRental(rental1);

        // Rent car3 to cust2 (returned)
        Rental rental2 = new Rental();
        rental2.setRentalDate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 10));
        rental2.setDueDate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 3));
        rental2.setBackDate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 2));
        rental2.setTotalPrice(600.0);
        rental2.setLeasingTerms("Premium");
        rental2.setCar(car3);
        rental2.setCustomer(cust2);
        store.addRental(rental2);

        // Test 1: Available cars
        System.out.println("=== Available Cars ===");
        List<Car> available = store.identifyAvailableCars();
        for (Car c : available) {
            System.out.println(c.getPlate() + " - " + c.getModel() + " - $" + c.getDailyPrice());
        }

        // Test 2: Total revenue
        System.out.println("\n=== Total Revenue ===");
        System.out.println("$" + store.calculateTotalRevenue());

        // Test 3: Overdue customers
        System.out.println("\n=== Overdue Customers ===");
        List<Customer> overdue = store.findCustomersWithOverdueRentals(new Date());
        for (Customer c : overdue) {
            System.out.println(c.getName() + " " + c.getSurname());
        }

        // Test 4: Average daily price
        System.out.println("\n=== Average Daily Price ===");
        System.out.println("$" + store.determineAverageDailyPrice());

        // Test 5: Count per customer
        System.out.println("\n=== Rentals per Customer ===");
        Map<Customer, Integer> counts = store.countCarsRentedPerCustomer();
        for (Map.Entry<Customer, Integer> entry : counts.entrySet()) {
            System.out.println(entry.getKey().getName() + " " + entry.getKey().getSurname() + ": " + entry.getValue());
        }
    }
}
