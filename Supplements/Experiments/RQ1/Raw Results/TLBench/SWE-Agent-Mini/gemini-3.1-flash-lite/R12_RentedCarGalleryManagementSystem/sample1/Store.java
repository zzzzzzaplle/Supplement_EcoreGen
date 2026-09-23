import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    public List<Car> identifyAvailableCars() {
        List<Car> available = new ArrayList<>(cars);
        for (Rental rental : rentals) {
            if (rental.getBackDate() == null) {
                available.remove(rental.getCar());
            }
        }
        available.sort(Comparator.comparingDouble(Car::getDailyPrice));
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
        List<Customer> overdue = new ArrayList<>();
        if (currentDate == null) return overdue;

        for (Rental rental : rentals) {
            if (rental.getBackDate() == null && currentDate.after(rental.getDueDate())) {
                if (!overdue.contains(rental.getCustomer())) {
                    overdue.add(rental.getCustomer());
                }
            }
        }
        return overdue;
    }

    public double determineAverageDailyPrice() {
        if (cars.isEmpty()) return 0.0;
        double sum = 0.0;
        for (Car car : cars) {
            sum += car.getDailyPrice();
        }
        return sum / cars.size();
    }

    public Map<Customer, Integer> countCarsRentedPerCustomer() {
        Map<Customer, Integer> counts = new HashMap<>();
        for (Rental rental : rentals) {
            Customer customer = rental.getCustomer();
            counts.put(customer, counts.getOrDefault(customer, 0) + 1);
        }
        return counts;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Car> getCars() { return cars; }
    public void setCars(List<Car> cars) { this.cars = cars; }
    public void addCar(Car car) { cars.add(car); }
    public List<Rental> getRentals() { return rentals; }
    public void setRentals(List<Rental> rentals) { this.rentals = rentals; }
    public void addRental(Rental rental) { rentals.add(rental); }
    public List<OverdueNotice> getNotices() { return notices; }
    public void setNotices(List<OverdueNotice> notices) { this.notices = notices; }
    public void addNotice(OverdueNotice notice) { notices.add(notice); }
}
