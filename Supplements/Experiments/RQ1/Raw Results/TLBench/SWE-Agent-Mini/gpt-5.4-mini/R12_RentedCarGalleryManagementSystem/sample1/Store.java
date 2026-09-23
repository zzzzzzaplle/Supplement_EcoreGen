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
        List<Car> availableCars = new ArrayList<Car>();
        if (cars == null) {
            return availableCars;
        }
        for (Car car : cars) {
            boolean available = true;
            if (rentals != null) {
                for (Rental rental : rentals) {
                    if (rental != null && rental.getCar() != null && car != null
                            && car.getPlate() != null && car.getPlate().equals(rental.getCar().getPlate())
                            && rental.getBackDate() == null) {
                        available = false;
                        break;
                    }
                }
            }
            if (available) {
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
        if (rentals == null) {
            return total;
        }
        for (Rental rental : rentals) {
            if (rental != null) {
                total += rental.getTotalPrice();
            }
        }
        return total;
    }

    public List<Customer> findCustomersWithOverdueRentals(Date currentDate) {
        List<Customer> overdueCustomers = new ArrayList<Customer>();
        if (currentDate == null || rentals == null) {
            return overdueCustomers;
        }
        for (Rental rental : rentals) {
            if (rental != null && rental.getBackDate() == null && rental.getDueDate() != null
                    && currentDate.after(rental.getDueDate()) && rental.getCustomer() != null
                    && !overdueCustomers.contains(rental.getCustomer())) {
                overdueCustomers.add(rental.getCustomer());
            }
        }
        return overdueCustomers;
    }

    public double determineAverageDailyPrice() {
        if (cars == null || cars.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        int count = 0;
        for (Car car : cars) {
            if (car != null) {
                total += car.getDailyPrice();
                count++;
            }
        }
        return count == 0 ? 0.0 : total / count;
    }

    public Map<Customer, Integer> countCarsRentedPerCustomer() {
        Map<Customer, Integer> result = new HashMap<Customer, Integer>();
        if (rentals == null) {
            return result;
        }
        for (Rental rental : rentals) {
            if (rental != null && rental.getCustomer() != null) {
                Customer customer = rental.getCustomer();
                Integer count = result.get(customer);
                result.put(customer, count == null ? 1 : count + 1);
            }
        }
        return result;
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
