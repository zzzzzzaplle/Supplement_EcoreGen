import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

    public List<Car> identifyAvailableCars() {
        List<Car> available = new ArrayList<>();
        for (Car car : cars) {
            boolean isRented = false;
            for (Rental rental : rentals) {
                if (rental.getCar() == car && rental.getBackDate() == null) {
                    isRented = true;
                    break;
                }
            }
            if (!isRented) {
                available.add(car);
            }
        }
        available.sort((c1, c2) -> Double.compare(c1.getDailyPrice(), c2.getDailyPrice()));
        return available;
    }

    public double calculateTotalRevenue() {
        double total = 0.0;
        for (Rental rental : rentals) {
            total += rental.getTotalPrice();
        }
        return total;
    }

    public List<Customer> findCustomersWithOverdueRentals(Date currentDate) {
        List<Customer> result = new ArrayList<>();
        if (currentDate == null) {
            return result;
        }
        List<Customer> foundCustomers = new ArrayList<>();
        for (Rental rental : rentals) {
            if (rental.getBackDate() == null) {
                if (rental.getDueDate().getTime() < currentDate.getTime()) {
                    Customer cust = rental.getCustomer();
                    boolean alreadyFound = false;
                    for (Customer existing : foundCustomers) {
                        if (existing.getName().equals(cust.getName()) && existing.getSurname().equals(cust.getSurname())) {
                            alreadyFound = true;
                            break;
                        }
                    }
                    if (!alreadyFound) {
                        foundCustomers.add(cust);
                        result.add(cust);
                    }
                }
            }
        }
        return result;
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
        Map<Customer, Integer> result = new LinkedHashMap<>();
        for (Rental rental : rentals) {
            Customer customer = rental.getCustomer();
            int count = result.getOrDefault(customer, 0);
            result.put(customer, count + 1);
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
}
