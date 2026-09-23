import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        List<Car> available = new ArrayList<Car>();
        if (cars == null) {
            return available;
        }
        Set<Car> rentedCars = new HashSet<Car>();
        if (rentals != null) {
            for (Rental r : rentals) {
                if (r != null && r.getBackDate() == null && r.getCar() != null) {
                    rentedCars.add(r.getCar());
                }
            }
        }
        for (Car c : cars) {
            if (c != null && !rentedCars.contains(c)) {
                available.add(c);
            }
        }
        Collections.sort(available, new Comparator<Car>() {
            public int compare(Car a, Car b) {
                if (a == null && b == null) return 0;
                if (a == null) return -1;
                if (b == null) return 1;
                return Double.compare(a.getDailyPrice(), b.getDailyPrice());
            }
        });
        return available;
    }

    public double calculateTotalRevenue() {
        double total = 0.0;
        if (rentals == null) {
            return total;
        }
        for (Rental r : rentals) {
            if (r != null) {
                total += r.getTotalPrice();
            }
        }
        return total;
    }

    public List<Customer> findCustomersWithOverdueRentals(Date currentDate) {
        List<Customer> overdueCustomers = new ArrayList<Customer>();
        if (currentDate == null || rentals == null) {
            return overdueCustomers;
        }
        Set<Customer> seen = new HashSet<Customer>();
        for (Rental r : rentals) {
            if (r != null
                    && r.getBackDate() == null
                    && r.getDueDate() != null
                    && currentDate.after(r.getDueDate())) {
                Customer cust = r.getCustomer();
                if (cust != null && !seen.contains(cust)) {
                    seen.add(cust);
                    overdueCustomers.add(cust);
                }
            }
        }
        return overdueCustomers;
    }

    public double determineAverageDailyPrice() {
        if (cars == null || cars.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        int count = 0;
        for (Car c : cars) {
            if (c != null) {
                sum += c.getDailyPrice();
                count++;
            }
        }
        if (count == 0) {
            return 0.0;
        }
        return sum / count;
    }

    public Map<Customer, Integer> countCarsRentedPerCustomer() {
        Map<Customer, Integer> result = new HashMap<Customer, Integer>();
        if (rentals == null) {
            return result;
        }
        for (Rental r : rentals) {
            if (r != null && r.getCustomer() != null) {
                Customer cust = r.getCustomer();
                Integer current = result.get(cust);
                if (current == null) {
                    current = 0;
                }
                result.put(cust, current + 1);
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
