import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Date;
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

    public List<Car> identifyAvailableCars() {
        List<Car> availableCars = new ArrayList<>();
        for (Car car : cars) {
            boolean isRentedCurrently = false;
            for (Rental rental : rentals) {
                if (rental.getCar().equals(car)) {
                    if (rental.getBackDate() == null) {
                        isRentedCurrently = true;
                        break;
                    }
                }
            }
            if (!isRentedCurrently) {
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
        double totalRevenue = 0.0;
        for (Rental rental : rentals) {
            totalRevenue += rental.getTotalPrice();
        }
        return totalRevenue;
    }

    public List<Customer> findCustomersWithOverdueRentals(Date currentDate) {
        List<Customer> overdueCustomers = new ArrayList<>();
        if (currentDate == null) {
            return overdueCustomers;
        }
        List<Customer> foundCustomers = new ArrayList<>();
        for (Rental rental : rentals) {
            if (rental.getBackDate() == null && rental.getDueDate() != null) {
                if (currentDate.after(rental.getDueDate())) {
                    Customer customer = rental.getCustomer();
                    boolean alreadyAdded = false;
                    for (Customer c : foundCustomers) {
                        if (c.equals(customer)) {
                            alreadyAdded = true;
                            break;
                        }
                    }
                    if (!alreadyAdded) {
                        foundCustomers.add(customer);
                    }
                }
            }
        }
        return foundCustomers;
    }

    public double determineAverageDailyPrice() {
        if (cars == null || cars.isEmpty()) {
            return 0.0;
        }
        double totalPrice = 0.0;
        for (Car car : cars) {
            totalPrice += car.getDailyPrice();
        }
        return totalPrice / cars.size();
    }

    public Map<Customer, Integer> countCarsRentedPerCustomer() {
        Map<Customer, Integer> customerRentalCount = new HashMap<>();
        if (rentals == null || rentals.isEmpty()) {
            return customerRentalCount;
        }
        for (Rental rental : rentals) {
            Customer customer = rental.getCustomer();
            if (customerRentalCount.containsKey(customer)) {
                int count = customerRentalCount.get(customer);
                customerRentalCount.put(customer, count + 1);
            } else {
                customerRentalCount.put(customer, 1);
            }
        }
        return customerRentalCount;
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
}
