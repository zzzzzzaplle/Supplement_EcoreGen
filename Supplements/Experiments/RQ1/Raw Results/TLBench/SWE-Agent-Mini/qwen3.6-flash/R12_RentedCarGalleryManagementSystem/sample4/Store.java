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
        if (this.cars == null) {
            this.cars = new ArrayList<>();
        }
        this.cars.add(car);
    }

    public List<Rental> getRentals() {
        return rentals;
    }

    public void setRentals(List<Rental> rentals) {
        this.rentals = rentals;
    }

    public void addRental(Rental rental) {
        if (this.rentals == null) {
            this.rentals = new ArrayList<>();
        }
        this.rentals.add(rental);
    }

    public List<OverdueNotice> getNotices() {
        return notices;
    }

    public void setNotices(List<OverdueNotice> notices) {
        this.notices = notices;
    }

    public void addNotice(OverdueNotice notice) {
        if (this.notices == null) {
            this.notices = new ArrayList<>();
        }
        this.notices.add(notice);
    }

    public List<Car> identifyAvailableCars() {
        List<Car> available = new ArrayList<>();
        for (Car car : cars) {
            boolean isRented = false;
            for (Rental rental : rentals) {
                if (rental.getCar() != null && rental.getCar().equals(car)) {
                    if (rental.getBackDate() == null) {
                        isRented = true;
                        break;
                    }
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
        List<Customer> overdueCustomers = new ArrayList<>();

        if (currentDate == null) {
            return overdueCustomers;
        }

        for (Rental rental : rentals) {
            if (rental.getDueDate() != null && rental.getBackDate() == null
                    && currentDate.after(rental.getDueDate())) {
                Customer customer = rental.getCustomer();
                boolean alreadyAdded = false;
                for (Customer c : overdueCustomers) {
                    if (c.equals(customer)) {
                        alreadyAdded = true;
                        break;
                    }
                }
                if (!alreadyAdded) {
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
        Map<Customer, Integer> counts = new HashMap<>();
        for (Rental rental : rentals) {
            Customer customer = rental.getCustomer();
            if (customer == null) {
                continue;
            }
            if (counts.containsKey(customer)) {
                counts.put(customer, counts.get(customer) + 1);
            } else {
                counts.put(customer, 1);
            }
        }
        return counts;
    }
}
