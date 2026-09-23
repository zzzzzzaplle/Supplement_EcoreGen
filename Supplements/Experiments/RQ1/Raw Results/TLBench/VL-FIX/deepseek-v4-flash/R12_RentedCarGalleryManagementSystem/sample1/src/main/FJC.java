import java.util.*;
import java.util.stream.*;

class Store {
    private String name;
    private List<Car> cars;
    private List<Rental> rentals;
    private List<OverdueNotice> notices;

    public Store() {
        this.name = null;
        this.cars = new ArrayList<>();
        this.rentals = new ArrayList<>();
        this.notices = new ArrayList<>();
    }

    // 1. Identify available cars sorted by daily price ascending
    public List<Car> identifyAvailableCars() {
        Set<Car> rentedCars = rentals.stream()
                .filter(r -> r.getBackDate() == null)
                .map(Rental::getCar)
                .collect(Collectors.toSet());
        return cars.stream()
                .filter(c -> !rentedCars.contains(c))
                .sorted(Comparator.comparingDouble(Car::getDailyPrice))
                .collect(Collectors.toList());
    }

    // 2. Calculate total revenue from all rentals
    public double calculateTotalRevenue() {
        return rentals.stream()
                .mapToDouble(Rental::getTotalPrice)
                .sum();
    }

    // 3. Find customers with overdue rentals based on currentDate
    public List<Customer> findCustomersWithOverdueRentals(Date currentDate) {
        if (currentDate == null) {
            return new ArrayList<>();
        }
        return rentals.stream()
                .filter(r -> r.getBackDate() == null && r.getDueDate() != null && currentDate.after(r.getDueDate()))
                .map(Rental::getCustomer)
                .distinct()
                .collect(Collectors.toList());
    }

    // 4. Determine average daily price of all cars
    public double determineAverageDailyPrice() {
        if (cars.isEmpty()) {
            return 0.0;
        }
        return cars.stream()
                .mapToDouble(Car::getDailyPrice)
                .average()
                .orElse(0.0);
    }

    // 5. Count rentals per customer
    public Map<Customer, Integer> countCarsRentedPerCustomer() {
        if (rentals.isEmpty()) {
            return new HashMap<>();
        }
        return rentals.stream()
                .collect(Collectors.groupingBy(Rental::getCustomer, Collectors.summingInt(r -> 1)));
    }

    // Getters and Setters
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

class Car {
    private String plate;
    private String model;
    private double dailyPrice;

    public Car() {
        this.plate = null;
        this.model = null;
        this.dailyPrice = 0.0;
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

class Customer {
    private String name;
    private String surname;
    private String address;

    public Customer() {
        this.name = null;
        this.surname = null;
        this.address = null;
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

class Rental {
    private Date rentalDate;
    private Date dueDate;
    private Date backDate;       // return date
    private double totalPrice;
    private String leasingTerms;
    private Car car;
    private Customer customer;

    public Rental() {
        this.rentalDate = null;
        this.dueDate = null;
        this.backDate = null;
        this.totalPrice = 0.0;
        this.leasingTerms = null;
        this.car = null;
        this.customer = null;
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

class OverdueNotice {
    private String noticeId;
    private Customer customer;

    public OverdueNotice() {
        this.noticeId = null;
        this.customer = null;
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

    public String sendNoticeTo(Customer customer) {
        // Placeholder implementation
        return "Overdue notice sent to " + customer.getName() + " " + customer.getSurname();
    }
}