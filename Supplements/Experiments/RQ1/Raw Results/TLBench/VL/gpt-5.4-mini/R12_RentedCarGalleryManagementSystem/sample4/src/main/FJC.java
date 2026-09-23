import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a car available in a store's rental gallery.
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
 * Represents a rental record connecting a car with a customer.
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
     * @return a human-readable confirmation message
     */
    public String sendNoticeTo(Customer customer) {
        this.customer = customer;
        return "Overdue notice " + noticeId + " sent to " + formatCustomer(customer);
    }

    private String formatCustomer(Customer customer) {
        if (customer == null) {
            return "unknown customer";
        }
        String name = customer.getName() == null ? "" : customer.getName();
        String surname = customer.getSurname() == null ? "" : customer.getSurname();
        String fullName = (name + " " + surname).trim();
        return fullName.isEmpty() ? "unknown customer" : fullName;
    }
}

/**
 * Represents a store that maintains cars, rentals, and overdue notices.
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

    public List<Car> identifyAvailableCars() {
        if (cars == null || cars.isEmpty()) {
            return new ArrayList<>();
        }

        List<Car> availableCars = new ArrayList<>();
        for (Car car : cars) {
            if (car == null) {
                continue;
            }
            boolean isTiedToActiveRental = false;
            if (rentals != null) {
                for (Rental rental : rentals) {
                    if (rental == null || rental.getCar() == null) {
                        continue;
                    }
                    if (sameCar(car, rental.getCar()) && rental.getBackDate() == null) {
                        isTiedToActiveRental = true;
                        break;
                    }
                }
            }
            if (!isTiedToActiveRental) {
                availableCars.add(car);
            }
        }

        availableCars.sort(Comparator.comparingDouble(Car::getDailyPrice));
        return availableCars;
    }

    public double calculateTotalRevenue() {
        if (rentals == null || rentals.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (Rental rental : rentals) {
            if (rental != null) {
                total += rental.getTotalPrice();
            }
        }
        return total;
    }

    public List<Customer> findCustomersWithOverdueRentals(Date currentDate) {
        if (currentDate == null || rentals == null || rentals.isEmpty()) {
            return new ArrayList<>();
        }

        List<Customer> overdueCustomers = new ArrayList<>();
        for (Rental rental : rentals) {
            if (rental == null) {
                continue;
            }
            if (rental.getBackDate() == null && rental.getDueDate() != null && currentDate.after(rental.getDueDate())) {
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
        if (rentals == null || rentals.isEmpty()) {
            return new HashMap<>();
        }

        Map<Customer, Integer> result = new HashMap<>();
        for (Rental rental : rentals) {
            if (rental == null || rental.getCustomer() == null) {
                continue;
            }
            Customer customer = rental.getCustomer();
            result.put(customer, result.getOrDefault(customer, 0) + 1);
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

    private boolean sameCar(Car a, Car b) {
        return a == b || (a != null && b != null && Objects.equals(a.getPlate(), b.getPlate()));
    }
}