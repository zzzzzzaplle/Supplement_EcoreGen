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
        this.cars = new ArrayList<Car>();
        this.rentals = new ArrayList<Rental>();
        this.notices = new ArrayList<OverdueNotice>();
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
        List<Car> availableCars = new ArrayList<Car>();
        for (Car car : cars) {
            boolean isRented = false;
            for (Rental rental : rentals) {
                if (rental.getCar().equals(car) && rental.getBackDate() == null) {
                    isRented = true;
                    break;
                }
            }
            if (!isRented) {
                availableCars.add(car);
            }
        }
        Collections.sort(availableCars, new Comparator<Car>() {
            public int compare(Car c1, Car c2) {
                return Double.compare(c1.getDailyPrice(), c2.getDailyPrice());
            }
        });
        return availableCars;
    }

    public double calculateTotalRevenue() {
        double total = 0.0;
        for (Rental rental : rentals) {
            total += rental.getTotalPrice();
        }
        return total;
    }

    public List<Customer> findCustomersWithOverdueRentals(Date currentDate) {
        List<Customer> overdueCustomers = new ArrayList<Customer>();
        if (currentDate == null) {
            return overdueCustomers;
        }
        for (Rental rental : rentals) {
            if (rental.getBackDate() == null && rental.getDueDate() != null && currentDate.after(rental.getDueDate())) {
                if (!overdueCustomers.contains(rental.getCustomer())) {
                    overdueCustomers.add(rental.getCustomer());
                }
            }
        }
        return overdueCustomers;
    }

    public double determineAverageDailyPrice() {
        if (cars.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (Car car : cars) {
            sum += car.getDailyPrice();
        }
        return sum / cars.size();
    }

    public Map<Customer, Integer> countCarsRentedPerCustomer() {
        Map<Customer, Integer> customerCountMap = new HashMap<Customer, Integer>();
        for (Rental rental : rentals) {
            Customer customer = rental.getCustomer();
            Integer count = customerCountMap.get(customer);
            if (count == null) {
                customerCountMap.put(customer, 1);
            } else {
                customerCountMap.put(customer, count + 1);
            }
        }
        return customerCountMap;
    }
}
