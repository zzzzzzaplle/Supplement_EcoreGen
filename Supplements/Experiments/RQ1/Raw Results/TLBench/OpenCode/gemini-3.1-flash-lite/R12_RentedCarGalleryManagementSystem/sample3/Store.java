import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;

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

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Car> getCars() { return cars; }
    public void setCars(List<Car> cars) { this.cars = cars; }
    public void addCar(Car car) { this.cars.add(car); }
    public List<Rental> getRentals() { return rentals; }
    public void setRentals(List<Rental> rentals) { this.rentals = rentals; }
    public void addRental(Rental rental) { this.rentals.add(rental); }
    public List<OverdueNotice> getNotices() { return notices; }
    public void setNotices(List<OverdueNotice> notices) { this.notices = notices; }
    public void addNotice(OverdueNotice notice) { this.notices.add(notice); }

    public List<Car> identifyAvailableCars() {
        List<Car> available = new ArrayList<>();
        for (Car car : cars) {
            boolean isRented = false;
            for (Rental rental : rentals) {
                if (rental.getCar().equals(car) && rental.getBackDate() == null) {
                    isRented = true;
                    break;
                }
            }
            if (!isRented) {
                available.add(car);
            }
        }
        Collections.sort(available, new Comparator<Car>() {
            public int compare(Car c1, Car c2) {
                return Double.compare(c1.getDailyPrice(), c2.getDailyPrice());
            }
        });
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
            if (rental.getBackDate() == null && rental.getDueDate() != null && currentDate.after(rental.getDueDate())) {
                if (!overdue.contains(rental.getCustomer())) {
                    overdue.add(rental.getCustomer());
                }
            }
        }
        return overdue;
    }

    public double determineAverageDailyPrice() {
        if (cars.isEmpty()) return 0.0;
        double total = 0.0;
        for (Car car : cars) {
            total += car.getDailyPrice();
        }
        return total / cars.size();
    }

    public Map<Customer, Integer> countCarsRentedPerCustomer() {
        Map<Customer, Integer> map = new HashMap<>();
        for (Rental rental : rentals) {
            Customer c = rental.getCustomer();
            map.put(c, map.getOrDefault(c, 0) + 1);
        }
        return map;
    }
}
