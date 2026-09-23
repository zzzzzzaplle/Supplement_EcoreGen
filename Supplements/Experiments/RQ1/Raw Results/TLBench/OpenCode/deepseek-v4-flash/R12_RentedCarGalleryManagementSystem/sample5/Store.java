import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        List<Car> rentedCars = rentals.stream()
                .filter(r -> r.getBackDate() == null)
                .map(Rental::getCar)
                .collect(Collectors.toList());

        return cars.stream()
                .filter(c -> !rentedCars.contains(c))
                .sorted((a, b) -> Double.compare(a.getDailyPrice(), b.getDailyPrice()))
                .collect(Collectors.toList());
    }

    public double calculateTotalRevenue() {
        return rentals.stream()
                .mapToDouble(Rental::getTotalPrice)
                .sum();
    }

    public List<Customer> findCustomersWithOverdueRentals(Date currentDate) {
        if (currentDate == null) {
            return new ArrayList<>();
        }

        return rentals.stream()
                .filter(r -> r.getBackDate() == null && currentDate.after(r.getDueDate()))
                .map(Rental::getCustomer)
                .collect(Collectors.toList());
    }

    public double determineAverageDailyPrice() {
        if (cars.isEmpty()) {
            return 0.0;
        }
        return cars.stream()
                .mapToDouble(Car::getDailyPrice)
                .average()
                .orElse(0.0);
    }

    public Map<Customer, Integer> countCarsRentedPerCustomer() {
        if (rentals.isEmpty()) {
            return new HashMap<>();
        }

        Map<Customer, Integer> counts = new HashMap<>();
        for (Rental rental : rentals) {
            Customer customer = rental.getCustomer();
            counts.put(customer, counts.getOrDefault(customer, 0) + 1);
        }
        return counts;
    }
}
