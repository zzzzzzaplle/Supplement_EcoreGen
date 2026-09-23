import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;

public class Store {
    private String name;
    private List<Car> cars;
    private List<Rental> rentals;
    private List<OverdueNotice> notices;

    public Store() {
        this.cars = new ArrayList<Car>();
        this.rentals = new ArrayList<Rental>();
        this.notices = new ArrayList<OverdueNotice>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public void addCar(Car car) {
        if (this.cars == null) {
            this.cars = new ArrayList<Car>();
        }
        this.cars.add(car);
    }

    public List<Rental> getRentals() {
        return rentals;
    }

    public void setRentals(List<Rental> rentals) {
        this.rentals = rentals;
    }

    public void addRental(Rental rental) {
        if (this.rentals == null) {
            this.rentals = new ArrayList<Rental>();
        }
        this.rentals.add(rental);
    }

    public List<OverdueNotice> getNotices() {
        return notices;
    }

    public void setNotices(List<OverdueNotice> notices) {
        this.notices = notices;
    }

    public void addNotice(OverdueNotice notice) {
        if (this.notices == null) {
            this.notices = new ArrayList<OverdueNotice>();
        }
        this.notices.add(notice);
    }

    // 1. Identify available cars, sorted by daily price ascending
    public List<Car> identifyAvailableCars() {
        List<Car> availableCars = new ArrayList<Car>();
        if (cars == null || cars.isEmpty()) {
            return availableCars;
        }

        for (Car car : cars) {
            if (isCarAvailable(car)) {
                availableCars.add(car);
            }
        }

        Collections.sort(availableCars, new Comparator<Car>() {
            public int compare(Car c1, Car c2) {
                return Double.compare(c1.getDailyPrice(), c2.getDailyPrice());
            }
        });

        return availableCars;
    }

    private boolean isCarAvailable(Car car) {
        if (rentals == null || rentals.isEmpty()) {
            return true;
        }
        for (Rental rental : rentals) {
            if (rental.getCar() != null && rental.getCar().equals(car) && rental.getBackDate() == null) {
                return false;
            }
        }
        return true;
    }

    // 2. Calculate total revenue
    public double calculateTotalRevenue() {
        double total = 0.0;
        if (rentals == null || rentals.isEmpty()) {
            return total;
        }
        for (Rental rental : rentals) {
            total += rental.getTotalPrice();
        }
        return total;
    }

    // 3. Find customers with overdue rentals
    public List<Customer> findCustomersWithOverdueRentals(Date currentDate) {
        List<Customer> overdueCustomers = new ArrayList<Customer>();
        if (currentDate == null || rentals == null || rentals.isEmpty()) {
            return overdueCustomers;
        }

        for (Rental rental : rentals) {
            if (rental.getBackDate() == null && rental.getDueDate() != null && currentDate.after(rental.getDueDate())) {
                if (rental.getCustomer() != null && !overdueCustomers.contains(rental.getCustomer())) {
                    overdueCustomers.add(rental.getCustomer());
                }
            }
        }

        return overdueCustomers;
    }

    // 4. Determine average daily price
    public double determineAverageDailyPrice() {
        if (cars == null || cars.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (Car car : cars) {
            sum += car.getDailyPrice();
        }
        return sum / cars.size();
    }

    // 5. Count rentals per customer
    public Map<Customer, Integer> countCarsRentedPerCustomer() {
        Map<Customer, Integer> rentalCounts = new HashMap<Customer, Integer>();
        if (rentals == null || rentals.isEmpty()) {
            return rentalCounts;
        }

        for (Rental rental : rentals) {
            Customer customer = rental.getCustomer();
            if (customer != null) {
                Integer count = rentalCounts.get(customer);
                if (count == null) {
                    rentalCounts.put(customer, 1);
                } else {
                    rentalCounts.put(customer, count + 1);
                }
            }
        }

        return rentalCounts;
    }
}
