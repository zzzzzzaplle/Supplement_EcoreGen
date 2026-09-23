import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public List<Car> identifyAvailableCars() {
        List<Car> available = new ArrayList<>();
        Set<Car> rentedCars = new HashSet<>();
        for (Rental rental : rentals) {
            if (rental != null && rental.getBackDate() == null && rental.getCar() != null) {
                rentedCars.add(rental.getCar());
            }
        }
        for (Car car : cars) {
            if (car != null && !rentedCars.contains(car)) {
                available.add(car);
            }
        }
        available.sort((a, b) -> Double.compare(a.getDailyPrice(), b.getDailyPrice()));
        return available;
    }

    public double calculateTotalRevenue() {
        double total = 0.0;
        for (Rental rental : rentals) {
            if (rental != null) {
                total += rental.getTotalPrice();
            }
        }
        return total;
    }

    public List<Customer> findCustomersWithOverdueRentals(Date currentDate) {
        List<Customer> overdue = new ArrayList<>();
        if (currentDate == null) {
            return overdue;
        }
        Set<Customer> seen = new HashSet<>();
        for (Rental rental : rentals) {
            if (rental != null && rental.getBackDate() == null && rental.getDueDate() != null && currentDate.after(rental.getDueDate()) && rental.getCustomer() != null) {
                if (seen.add(rental.getCustomer())) {
                    overdue.add(rental.getCustomer());
                }
            }
        }
        return overdue;
    }

    public double determineAverageDailyPrice() {
        if (cars == null || cars.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        int count = 0;
        for (Car car : cars) {
            if (car != null) {
                total += car.getDailyPrice();
                count++;
            }
        }
        return count == 0 ? 0.0 : total / count;
    }

    public Map<Customer, Integer> countCarsRentedPerCustomer() {
        Map<Customer, Integer> result = new HashMap<>();
        for (Rental rental : rentals) {
            if (rental != null && rental.getCustomer() != null) {
                Customer customer = rental.getCustomer();
                Integer current = result.get(customer);
                result.put(customer, current == null ? 1 : current + 1);
            }
        }
        return result;
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
            this.cars = new ArrayList<>();
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
            this.rentals = new ArrayList<>();
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
            this.notices = new ArrayList<>();
        }
        this.notices.add(notice);
    }
}
