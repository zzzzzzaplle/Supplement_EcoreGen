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
        List<Car> available = new ArrayList<Car>();
        if (cars == null || cars.isEmpty()) {
            return available;
        }
        for (Car car : cars) {
            if (car == null) {
                continue;
            }
            boolean rentedOut = false;
            if (rentals != null) {
                for (Rental rental : rentals) {
                    if (rental == null || rental.getCar() == null) {
                        continue;
                    }
                    if (rental.getCar() == car && rental.getBackDate() == null) {
                        rentedOut = true;
                        break;
                    }
                }
            }
            if (!rentedOut) {
                available.add(car);
            }
        }
        Collections.sort(available, new Comparator<Car>() {
            public int compare(Car a, Car b) {
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
        for (Rental rental : rentals) {
            if (rental != null) {
                total += rental.getTotalPrice();
            }
        }
        return total;
    }

    public List<Customer> findCustomersWithOverdueRentals(Date currentDate) {
        List<Customer> overdue = new ArrayList<Customer>();
        if (currentDate == null || rentals == null) {
            return overdue;
        }
        for (Rental rental : rentals) {
            if (rental == null) {
                continue;
            }
            if (rental.getBackDate() == null && rental.getDueDate() != null
                    && currentDate.after(rental.getDueDate())) {
                Customer customer = rental.getCustomer();
                if (customer != null && !overdue.contains(customer)) {
                    overdue.add(customer);
                }
            }
        }
        return overdue;
    }

    public double determineAverageDailyPrice() {
        if (cars == null || cars.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        int count = 0;
        for (Car car : cars) {
            if (car != null) {
                sum += car.getDailyPrice();
                count++;
            }
        }
        if (count == 0) {
            return 0.0;
        }
        return sum / count;
    }

    public Map<Customer, Integer> countCarsRentedPerCustomer() {
        Map<Customer, Integer> counts = new HashMap<Customer, Integer>();
        if (rentals == null) {
            return counts;
        }
        for (Rental rental : rentals) {
            if (rental == null) {
                continue;
            }
            Customer customer = rental.getCustomer();
            if (customer == null) {
                continue;
            }
            Integer current = counts.get(customer);
            if (current == null) {
                counts.put(customer, 1);
            } else {
                counts.put(customer, current + 1);
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
