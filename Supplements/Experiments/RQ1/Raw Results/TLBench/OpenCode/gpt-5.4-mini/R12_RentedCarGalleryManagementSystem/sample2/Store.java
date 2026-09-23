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

    public List<Car> identifyAvailableCars() {
        if (cars == null || cars.isEmpty()) {
            return new ArrayList<Car>();
        }
        List<Car> availableCars = new ArrayList<Car>();
        for (Car car : cars) {
            boolean activeRentalFound = false;
            if (rentals != null) {
                for (Rental rental : rentals) {
                    if (rental != null && rental.getCar() == car && rental.getBackDate() == null) {
                        activeRentalFound = true;
                        break;
                    }
                }
            }
            if (!activeRentalFound) {
                availableCars.add(car);
            }
        }
        Collections.sort(availableCars, new Comparator<Car>() {
            public int compare(Car first, Car second) {
                return Double.compare(first.getDailyPrice(), second.getDailyPrice());
            }
        });
        return availableCars;
    }

    public double calculateTotalRevenue() {
        double total = 0.0;
        if (rentals != null) {
            for (Rental rental : rentals) {
                if (rental != null) {
                    total += rental.getTotalPrice();
                }
            }
        }
        return total;
    }

    public List<Customer> findCustomersWithOverdueRentals(Date currentDate) {
        List<Customer> customers = new ArrayList<Customer>();
        if (currentDate == null || rentals == null) {
            return customers;
        }
        for (Rental rental : rentals) {
            if (rental != null && rental.getBackDate() == null && rental.getDueDate() != null && currentDate.after(rental.getDueDate())) {
                Customer customer = rental.getCustomer();
                if (customer != null && !customers.contains(customer)) {
                    customers.add(customer);
                }
            }
        }
        return customers;
    }

    public double determineAverageDailyPrice() {
        if (cars == null || cars.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (Car car : cars) {
            if (car != null) {
                total += car.getDailyPrice();
            }
        }
        return total / cars.size();
    }

    public Map<Customer, Integer> countCarsRentedPerCustomer() {
        Map<Customer, Integer> counts = new HashMap<Customer, Integer>();
        if (rentals == null) {
            return counts;
        }
        for (Rental rental : rentals) {
            if (rental != null && rental.getCustomer() != null) {
                Customer customer = rental.getCustomer();
                Integer count = counts.get(customer);
                if (count == null) {
                    counts.put(customer, Integer.valueOf(1));
                } else {
                    counts.put(customer, Integer.valueOf(count.intValue() + 1));
                }
            }
        }
        return counts;
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
            this.cars = new ArrayList<Car>();
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
            this.rentals = new ArrayList<Rental>();
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
            this.notices = new ArrayList<OverdueNotice>();
        }
        this.notices.add(notice);
    }
}
