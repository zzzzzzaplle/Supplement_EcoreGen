import java.util.*;

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
            boolean isRentedActive = false;
            for (Rental rental : rentals) {
                if (rental.getCar() != null && rental.getCar().equals(car) && rental.getBackDate() == null) {
                    isRentedActive = true;
                    break;
                }
            }
            if (!isRentedActive) {
                available.add(car);
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
        List<Customer> overdueCustomers = new ArrayList<>();
        if (currentDate == null) {
            return overdueCustomers;
        }
        for (Rental rental : rentals) {
            if (rental.getBackDate() == null && currentDate.after(rental.getDueDate())) {
                Customer customer = rental.getCustomer();
                if (customer != null && !overdueCustomers.contains(customer)) {
                    overdueCustomers.add(customer);
                }
            }
        }
        return overdueCustomers;
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
        Map<Customer, Integer> customerCount = new HashMap<>();
        for (Rental rental : rentals) {
            Customer customer = rental.getCustomer();
            if (customer != null) {
                customerCount.put(customer, customerCount.getOrDefault(customer, 0) + 1);
            }
        }
        return customerCount;
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
        cars.add(car);
    }

    public List<Rental> getRentals() {
        return rentals;
    }

    public void setRentals(List<Rental> rentals) {
        this.rentals = rentals;
    }

    public void addRental(Rental rental) {
        rentals.add(rental);
    }

    public List<OverdueNotice> getNotices() {
        return notices;
    }

    public void setNotices(List<OverdueNotice> notices) {
        this.notices = notices;
    }

    public void addNotice(OverdueNotice notice) {
        notices.add(notice);
    }
}
