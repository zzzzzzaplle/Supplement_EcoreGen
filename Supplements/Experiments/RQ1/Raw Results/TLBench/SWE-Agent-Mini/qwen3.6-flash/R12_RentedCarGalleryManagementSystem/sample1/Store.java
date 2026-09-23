import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Store {
    private String name;
    private List<Car> cars;
    private List<Rental> rentals;
    private List<OverdueNotice> notices;
    
    public Store() {
        this.cars = new ArrayList<>();
        this.rentals = new ArrayList<>();
        this.notices = new ArrayList<>();
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
        this.cars.add(car);
    }
    
    public List<Rental> getRentals() {
        return rentals;
    }
    
    public void setRentals(List<Rental> rentals) {
        this.rentals = rentals;
    }
    
    public void addRental(Rental rental) {
        this.rentals.add(rental);
    }
    
    public List<OverdueNotice> getNotices() {
        return notices;
    }
    
    public void setNotices(List<OverdueNotice> notices) {
        this.notices = notices;
    }
    
    public void addNotice(OverdueNotice notice) {
        this.notices.add(notice);
    }
    
    public List<Car> identifyAvailableCars() {
        List<Car> availableCars = new ArrayList<>();
        for (Car car : cars) {
            boolean isRenting = false;
            for (Rental rental : rentals) {
                if (rental.getCar() != null && rental.getCar().equals(car) && rental.getBackDate() == null) {
                    isRenting = true;
                    break;
                }
            }
            if (!isRenting) {
                availableCars.add(car);
            }
        }
        Collections.sort(availableCars, (c1, c2) -> Double.compare(c1.getDailyPrice(), c2.getDailyPrice()));
        return availableCars;
    }
    
    public double calculateTotalRevenue() {
        double total = 0.0;
        for (Rental rental : rentals) {
            total += rental.getTotalPrice();
        }
        return total;
    }
    
    public List<Customer> findCustomersWithOverdueRentals(Date currentDate) {
        List<Customer> overdueCustomers = new ArrayList<>();
        if (currentDate == null) {
            return overdueCustomers;
        }
        for (Rental rental : rentals) {
            if (rental.getBackDate() == null && rental.getDueDate() != null && rental.getDueDate().before(currentDate)) {
                Customer customer = rental.getCustomer();
                if (customer != null) {
                    boolean alreadyAdded = false;
                    for (Customer existing : overdueCustomers) {
                        if (existing.equals(customer)) {
                            alreadyAdded = true;
                            break;
                        }
                    }
                    if (!alreadyAdded) {
                        overdueCustomers.add(customer);
                    }
                }
            }
        }
        return overdueCustomers;
    }
    
    public double determineAverageDailyPrice() {
        if (cars == null || cars.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (Car car : cars) {
            total += car.getDailyPrice();
        }
        return total / cars.size();
    }
    
    public Map<Customer, Integer> countCarsRentedPerCustomer() {
        Map<Customer, Integer> customerCounts = new HashMap<>();
        for (Rental rental : rentals) {
            Customer customer = rental.getCustomer();
            if (customer != null) {
                if (customerCounts.containsKey(customer)) {
                    customerCounts.put(customer, customerCounts.get(customer) + 1);
                } else {
                    customerCounts.put(customer, 1);
                }
            }
        }
        return customerCounts;
    }
}
