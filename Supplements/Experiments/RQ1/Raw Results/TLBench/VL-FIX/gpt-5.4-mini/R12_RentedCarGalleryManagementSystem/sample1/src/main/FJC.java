import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a car available in the store.
 */
class Car {
    private String plate;
    private String model;
    private double dailyPrice;

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
 * Represents a customer renting cars from the store.
 */
class Customer {
    private String name;
    private String surname;
    private String address;

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
}

/**
 * Represents a rental record in the store.
 */
class Rental {
    private Date rentalDate;
    private Date dueDate;
    private Date backDate;
    private double totalPrice;
    private String leasingTerms;
    private Car car;
    private Customer customer;

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
 * Represents an overdue notice issued for a customer.
 */
class OverdueNotice {
    private String noticeId;
    private Customer customer;

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
     * Sends a notice to the target customer.
     *
     * @param customer target customer
     * @return a simple message describing the action
     */
    public String sendNoticeTo(Customer customer) {
        this.customer = customer;
        if (customer == null) {
            return "Notice not sent: customer is null.";
        }
        return "Notice " + (noticeId == null ? "" : noticeId) + " sent to "
                + safe(customer.getName()) + " " + safe(customer.getSurname()).trim();
    }

    private String safe(String value) {
        return value == null ? "" : value;
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
            if (car == null) {
                continue;
            }
            if (!isCarTiedToActiveRental(car)) {
                availableCars.add(car);
            }
        }

        if (availableCars.isEmpty()) {
            return availableCars;
        }

        Collections.sort(availableCars, new Comparator<Car>() {
            @Override
            public int compare(Car a, Car b) {
                return Double.compare(a.getDailyPrice(), b.getDailyPrice());
            }
        });
        return availableCars;
    }

    private boolean isCarTiedToActiveRental(Car car) {
        if (rentals == null || rentals.isEmpty()) {
            return false;
        }
        for (Rental rental : rentals) {
            if (rental == null) {
                continue;
            }
            if (rental.getCar() != null && sameCar(rental.getCar(), car) && rental.getBackDate() == null) {
                return true;
            }
        }
        return false;
    }

    private boolean sameCar(Car a, Car b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return Objects.equals(a.getPlate(), b.getPlate());
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
            return new ArrayList<Customer>();
        }

        List<Customer> overdueCustomers = new ArrayList<Customer>();
        for (Rental rental : rentals) {
            if (rental == null) {
                continue;
            }
            if (rental.getBackDate() == null
                    && rental.getDueDate() != null
                    && currentDate.after(rental.getDueDate())
                    && rental.getCustomer() != null
                    && !containsCustomer(overdueCustomers, rental.getCustomer())) {
                overdueCustomers.add(rental.getCustomer());
            }
        }
        return overdueCustomers;
    }

    private boolean containsCustomer(List<Customer> customers, Customer target) {
        for (Customer customer : customers) {
            if (sameCustomer(customer, target)) {
                return true;
            }
        }
        return false;
    }

    private boolean sameCustomer(Customer a, Customer b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return Objects.equals(a.getName(), b.getName())
                && Objects.equals(a.getSurname(), b.getSurname())
                && Objects.equals(a.getAddress(), b.getAddress());
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
            return new HashMap<Customer, Integer>();
        }

        Map<Customer, Integer> counts = new LinkedHashMap<Customer, Integer>();
        for (Rental rental : rentals) {
            if (rental == null || rental.getCustomer() == null) {
                continue;
            }
            Customer key = rental.getCustomer();
            Customer existingKey = findEquivalentCustomerKey(counts, key);
            if (existingKey == null) {
                counts.put(key, 1);
            } else {
                counts.put(existingKey, counts.get(existingKey) + 1);
            }
        }
        return counts;
    }

    private Customer findEquivalentCustomerKey(Map<Customer, Integer> map, Customer target) {
        for (Customer customer : map.keySet()) {
            if (sameCustomer(customer, target)) {
                return customer;
            }
        }
        return null;
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