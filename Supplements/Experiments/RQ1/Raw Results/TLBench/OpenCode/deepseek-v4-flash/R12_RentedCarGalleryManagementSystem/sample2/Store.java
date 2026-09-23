import java.util.*;
import java.util.stream.*;

public class Store {
    private String name;
    private List<Car> cars;
    private List<Rental> rentals;
    private List<OverdueNotice> notices;

    public Store() {
    }

    public List<Car> identifyAvailableCars() {
        List<Car> unavailable = rentals.stream()
            .filter(r -> r.getBackDate() == null)
            .map(Rental::getCar)
            .collect(Collectors.toList());
        List<Car> available = new ArrayList<>();
        for (Car car : cars) {
            if (!unavailable.contains(car)) {
                available.add(car);
            }
        }
        available.sort(Comparator.comparingDouble(Car::getDailyPrice));
        return available;
    }

    public double calculateTotalRevenue() {
        double total = 0.0;
        for (Rental r : rentals) {
            total += r.getTotalPrice();
        }
        return total;
    }

    public List<Customer> findCustomersWithOverdueRentals(Date currentDate) {
        if (currentDate == null) {
            return new ArrayList<>();
        }
        List<Customer> overdue = new ArrayList<>();
        for (Rental r : rentals) {
            if (r.getBackDate() == null && currentDate.after(r.getDueDate())) {
                overdue.add(r.getCustomer());
            }
        }
        return overdue;
    }

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

    public Map<Customer, Integer> countCarsRentedPerCustomer() {
        Map<Customer, Integer> map = new HashMap<>();
        for (Rental r : rentals) {
            Customer c = r.getCustomer();
            map.put(c, map.getOrDefault(c, 0) + 1);
        }
        return map;
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
        if (cars == null) {
            cars = new ArrayList<>();
        }
        cars.add(car);
    }

    public List<Rental> getRentals() {
        return rentals;
    }

    public void setRentals(List<Rental> rentals) {
        this.rentals = rentals;
    }

    public void addRental(Rental rental) {
        if (rentals == null) {
            rentals = new ArrayList<>();
        }
        rentals.add(rental);
    }

    public List<OverdueNotice> getNotices() {
        return notices;
    }

    public void setNotices(List<OverdueNotice> notices) {
        this.notices = notices;
    }

    public void addNotice(OverdueNotice notice) {
        if (notices == null) {
            notices = new ArrayList<>();
        }
        notices.add(notice);
    }
}
