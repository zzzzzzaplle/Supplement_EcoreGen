import java.util.ArrayList;
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
        if (this.cars == null) {
            return availableCars;
        }
        for (Car car : this.cars) {
            boolean activeRentalExists = false;
            if (this.rentals != null) {
                for (Rental rental : this.rentals) {
                    if (rental != null && car != null && car.equals(rental.getCar()) && rental.getBackDate() == null) {
                        activeRentalExists = true;
                        break;
                    }
                }
            }
            if (!activeRentalExists) {
                availableCars.add(car);
            }
        }
        availableCars.sort((a, b) -> Double.compare(a.getDailyPrice(), b.getDailyPrice()));
        return availableCars;
    }

    public double calculateTotalRevenue() {
        double totalRevenue = 0.0;
        if (this.rentals == null) {
            return totalRevenue;
        }
        for (Rental rental : this.rentals) {
            if (rental != null) {
                totalRevenue += rental.getTotalPrice();
            }
        }
        return totalRevenue;
    }

    public List<Customer> findCustomersWithOverdueRentals(java.util.Date currentDate) {
        List<Customer> overdueCustomers = new ArrayList<Customer>();
        if (currentDate == null || this.rentals == null) {
            return overdueCustomers;
        }
        for (Rental rental : this.rentals) {
            if (rental != null && rental.getBackDate() == null && rental.getDueDate() != null && currentDate.after(rental.getDueDate())) {
                Customer customer = rental.getCustomer();
                if (customer != null && !overdueCustomers.contains(customer)) {
                    overdueCustomers.add(customer);
                }
            }
        }
        return overdueCustomers;
    }

    public double determineAverageDailyPrice() {
        if (this.cars == null || this.cars.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        int count = 0;
        for (Car car : this.cars) {
            if (car != null) {
                total += car.getDailyPrice();
                count++;
            }
        }
        if (count == 0) {
            return 0.0;
        }
        return total / count;
    }

    public Map<Customer, Integer> countCarsRentedPerCustomer() {
        Map<Customer, Integer> counts = new java.util.HashMap<Customer, Integer>();
        if (this.rentals == null) {
            return counts;
        }
        for (Rental rental : this.rentals) {
            if (rental != null && rental.getCustomer() != null) {
                Customer customer = rental.getCustomer();
                Integer current = counts.get(customer);
                if (current == null) {
                    counts.put(customer, Integer.valueOf(1));
                } else {
                    counts.put(customer, Integer.valueOf(current.intValue() + 1));
                }
            }
        }
        return counts;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Car> getCars() {
        return this.cars;
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
        return this.rentals;
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
        return this.notices;
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
