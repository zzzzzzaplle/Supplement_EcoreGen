import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a car in the store inventory.
 */
class Car {
    private String plate;
    private String model;
    private double dailyPrice;

    /**
     * Unparameterized constructor.
     */
    public Car() {
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getDailyPrice() {
        return dailyPrice;
    }

    public void setDailyPrice(double dailyPrice) {
        this.dailyPrice = dailyPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Car)) return false;
        Car car = (Car) o;
        return Objects.equals(plate, car.plate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plate);
    }
}

/**
 * Represents a customer who rents cars from the store.
 */
class Customer {
    private String name;
    private String surname;
    private String address;

    /**
     * Unparameterized constructor.
     */
    public Customer() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer customer = (Customer) o;
        return Objects.equals(name, customer.name)
                && Objects.equals(surname, customer.surname)
                && Objects.equals(address, customer.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, address);
    }
}

/**
 * Represents a rental transaction.
 */
class Rental {
    private Date rentalDate;
    private Date dueDate;
    private Date backDate;
    private double totalPrice;
    private String leasingTerms;
    private Car car;
    private Customer customer;

    /**
     * Unparameterized constructor.
     */
    public Rental() {
    }

    public Date getRentalDate() {
        return rentalDate;
    }

    public void setRentalDate(Date rentalDate) {
        this.rentalDate = rentalDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getBackDate() {
        return backDate;
    }

    public void setBackDate(Date backDate) {
        this.backDate = backDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getLeasingTerms() {
        return leasingTerms;
    }

    public void setLeasingTerms(String leasingTerms) {
        this.leasingTerms = leasingTerms;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}

/**
 * Represents an overdue notice issued to a customer.
 */
class OverdueNotice {
    private String noticeId;
    private Customer customer;

    /**
     * Unparameterized constructor.
     */
    public OverdueNotice() {
    }

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * Sends a notice to the specified customer.
     *
     * @param customer target customer
     * @return a textual confirmation message
     */
    public String sendNoticeTo(Customer customer) {
        this.customer = customer;
        if (customer == null) {
            return "No customer specified.";
        }
        String fullName = (customer.getName() == null ? "" : customer.getName()) +
                (customer.getSurname() == null ? "" : (" " + customer.getSurname()));
        return "Overdue notice " + (noticeId == null ? "" : noticeId) + " sent to " + fullName.trim();
    }
}

/**
 * Represents a store that manages cars, rentals, and overdue notices.
 */
class Store {
    private String name;
    private List<Car> cars;
    private List<Rental> rentals;
    private List<OverdueNotice> notices;

    /**
     * Unparameterized constructor.
     */
    public Store() {
        this.cars = new ArrayList<>();
        this.rentals = new ArrayList<>();
        this.notices = new ArrayList<>();
    }

    /**
     * Identifies currently available cars, sorted by ascending daily price.
     *
     * A car is considered unavailable if it is tied to an active rental
     * whose backDate is still empty (null).
     *
     * @return list of available cars, or an empty list if none are available
     */
    public List<Car> identifyAvailableCars() {
        List<Car> availableCars = new ArrayList<>();
        if (cars == null || cars.isEmpty()) {
            return availableCars;
        }

        for (Car car : cars) {
            boolean activeRentalExists = false;
            if (rentals != null) {
                for (Rental rental : rentals) {
                    if (rental != null
                            && rental.getCar() != null
                            && car != null
                            && Objects.equals(car.getPlate(), rental.getCar().getPlate())
                            && rental.getBackDate() == null) {
                        activeRentalExists = true;
                        break;
                    }
                }
            }
            if (!activeRentalExists) {
                availableCars.add(car);
            }
        }

        availableCars.sort(Comparator.comparingDouble(car -> car == null ? Double.MAX_VALUE : car.getDailyPrice()));
        return availableCars;
    }

    /**
     * Calculates the total revenue from all rental records.
     *
     * @return total revenue
     */
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

    /**
     * Finds customers with overdue rentals.
     *
     * A rental is overdue when its backDate is null and currentDate is later than dueDate.
     *
     * @param currentDate current date
     * @return list of overdue customers, or an empty list if none exist or currentDate is null
     */
    public List<Customer> findCustomersWithOverdueRentals(Date currentDate) {
        List<Customer> overdueCustomers = new ArrayList<>();
        if (currentDate == null || rentals == null || rentals.isEmpty()) {
            return overdueCustomers;
        }

        for (Rental rental : rentals) {
            if (rental == null) {
                continue;
            }
            Date dueDate = rental.getDueDate();
            if (rental.getBackDate() == null && dueDate != null && currentDate.after(dueDate)) {
                Customer customer = rental.getCustomer();
                if (customer != null && !overdueCustomers.contains(customer)) {
                    overdueCustomers.add(customer);
                }
            }
        }
        return overdueCustomers;
    }

    /**
     * Determines the average daily price of all cars stored in the store.
     *
     * @return average daily price, or 0.0 when there are no car records
     */
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
        return count == 0 ? 0.0 : sum / count;
    }

    /**
     * Counts how many rental records belong to each customer.
     *
     * @return a mapping from customer to rental count, or an empty map if no rentals exist
     */
    public Map<Customer, Integer> countCarsRentedPerCustomer() {
        Map<Customer, Integer> countMap = new HashMap<>();
        if (rentals == null || rentals.isEmpty()) {
            return countMap;
        }

        for (Rental rental : rentals) {
            if (rental == null || rental.getCustomer() == null) {
                continue;
            }
            Customer customer = rental.getCustomer();
            countMap.put(customer, countMap.getOrDefault(customer, 0) + 1);
        }
        return countMap;
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
}