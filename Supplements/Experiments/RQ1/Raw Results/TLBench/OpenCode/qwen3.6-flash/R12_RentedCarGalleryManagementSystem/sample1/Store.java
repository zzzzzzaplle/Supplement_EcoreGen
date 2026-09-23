import java.util.ArrayList;
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
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Car> getCars() {
        return this.cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public void addCar(Car car) {
        this.cars.add(car);
    }

    public List<Rental> getRentals() {
        return this.rentals;
    }

    public void setRentals(List<Rental> rentals) {
        this.rentals = rentals;
    }

    public void addRental(Rental rental) {
        this.rentals.add(rental);
    }

    public List<OverdueNotice> getNotices() {
        return this.notices;
    }

    public void setNotices(List<OverdueNotice> notices) {
        this.notices = notices;
    }

    public void addNotice(OverdueNotice notice) {
        this.notices.add(notice);
    }

    public List<Car> identifyAvailableCars() {
        List<Car> available = new ArrayList<>();
        for (Car car : this.cars) {
            boolean isRented = false;
            for (Rental rental : this.rentals) {
                if (rental.getCar().equals(car) && rental.getBackDate() == null) {
                    isRented = true;
                    break;
                }
            }
            if (!isRented) {
                available.add(car);
            }
        }
        available.sort((a, b) -> Double.compare(a.getDailyPrice(), b.getDailyPrice()));
        return available;
    }

    public double calculateTotalRevenue() {
        double total = 0.0;
        for (Rental rental : this.rentals) {
            total += rental.getTotalPrice();
        }
        return total;
    }

    public List<Customer> findCustomersWithOverdueRentals(Date currentDate) {
        List<Customer> overdueCustomers = new ArrayList<>();
        if (currentDate == null) {
            return overdueCustomers;
        }
        for (Rental rental : this.rentals) {
            if (rental.getBackDate() == null && rental.getDueDate().before(currentDate)) {
                boolean alreadyAdded = false;
                for (Customer existing : overdueCustomers) {
                    if (existing.equals(rental.getCustomer())) {
                        alreadyAdded = true;
                        break;
                    }
                }
                if (!alreadyAdded) {
                    overdueCustomers.add(rental.getCustomer());
                }
            }
        }
        return overdueCustomers;
    }

    public double determineAverageDailyPrice() {
        if (this.cars.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (Car car : this.cars) {
            total += car.getDailyPrice();
        }
        return total / this.cars.size();
    }

    public Map<Customer, Integer> countCarsRentedPerCustomer() {
        Map<Customer, Integer> countMap = new HashMap<>();
        for (Rental rental : this.rentals) {
            Customer customer = rental.getCustomer();
            int count = countMap.getOrDefault(customer, 0);
            countMap.put(customer, count + 1);
        }
        return countMap;
    }
}
