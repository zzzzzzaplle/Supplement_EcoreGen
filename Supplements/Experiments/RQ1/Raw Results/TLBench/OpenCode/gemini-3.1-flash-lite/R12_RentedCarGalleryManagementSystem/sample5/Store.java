import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import java.util.stream.Collectors;

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
        List<Car> rentedCars = rentals.stream()
                .filter(r -> r.getBackDate() == null)
                .map(Rental::getCar)
                .collect(Collectors.toList());

        return cars.stream()
                .filter(c -> !rentedCars.contains(c))
                .sorted(Comparator.comparingDouble(Car::getDailyPrice))
                .collect(Collectors.toList());
    }

    public double calculateTotalRevenue() {
        return rentals.stream().mapToDouble(Rental::getTotalPrice).sum();
    }

    public List<Customer> findCustomersWithOverdueRentals(Date currentDate) {
        if (currentDate == null) return new ArrayList<>();
        return rentals.stream()
                .filter(r -> r.getBackDate() == null && r.getDueDate() != null && r.getDueDate().before(currentDate))
                .map(Rental::getCustomer)
                .distinct()
                .collect(Collectors.toList());
    }

    public double determineAverageDailyPrice() {
        if (cars.isEmpty()) return 0.0;
        return cars.stream().mapToDouble(Car::getDailyPrice).average().orElse(0.0);
    }

    public Map<Customer, Integer> countCarsRentedPerCustomer() {
        Map<Customer, Integer> counts = new HashMap<>();
        for (Rental r : rentals) {
            Customer c = r.getCustomer();
            counts.put(c, counts.getOrDefault(c, 0) + 1);
        }
        return counts;
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
}
