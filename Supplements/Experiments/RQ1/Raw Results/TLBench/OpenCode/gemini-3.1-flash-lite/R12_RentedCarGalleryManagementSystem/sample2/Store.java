import java.util.*;

public class Store {
    private String name;
    private List<Car> cars = new ArrayList<>();
    private List<Rental> rentals = new ArrayList<>();
    private List<OverdueNotice> notices = new ArrayList<>();

    public Store() {}

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
        List<Car> availableCars = new ArrayList<>(cars);
        for (Rental r : rentals) {
            if (r.getBackDate() == null) {
                availableCars.remove(r.getCar());
            }
        }
        availableCars.sort(Comparator.comparingDouble(Car::getDailyPrice));
        return availableCars;
    }

    public double calculateTotalRevenue() {
        double total = 0.0;
        for (Rental r : rentals) {
            total += r.getTotalPrice();
        }
        return total;
    }

    public List<Customer> findCustomersWithOverdueRentals(Date currentDate) {
        if (currentDate == null) return new ArrayList<>();
        List<Customer> overdueCustomers = new ArrayList<>();
        for (Rental r : rentals) {
            if (r.getBackDate() == null && currentDate.after(r.getDueDate())) {
                overdueCustomers.add(r.getCustomer());
            }
        }
        return overdueCustomers;
    }

    public double determineAverageDailyPrice() {
        if (cars.isEmpty()) return 0.0;
        double total = 0.0;
        for (Car c : cars) {
            total += c.getDailyPrice();
        }
        return total / cars.size();
    }

    public Map<Customer, Integer> countCarsRentedPerCustomer() {
        Map<Customer, Integer> counts = new HashMap<>();
        for (Rental r : rentals) {
            Customer c = r.getCustomer();
            counts.put(c, counts.getOrDefault(c, 0) + 1);
        }
        return counts;
    }
}
