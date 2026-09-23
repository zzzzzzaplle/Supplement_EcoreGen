import java.util.*;

public class Store {
    private String name;
    private List<Car> cars = new ArrayList<>();
    private List<Rental> rentals = new ArrayList<>();
    private List<OverdueNotice> notices = new ArrayList<>();

    public Store() {}

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
        for (Rental r : rentals) {
            total += r.getTotalPrice();
        }
        return total;
    }

    public List<Customer> findCustomersWithOverdueRentals(Date currentDate) {
        if (currentDate == null) return new ArrayList<>();
        List<Customer> overdue = new ArrayList<>();
        for (Rental r : rentals) {
            if (r.getBackDate() == null && currentDate.after(r.getDueDate())) {
                if (!overdue.contains(r.getCustomer())) {
                    overdue.add(r.getCustomer());
                }
            }
        }
        return overdue;
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
