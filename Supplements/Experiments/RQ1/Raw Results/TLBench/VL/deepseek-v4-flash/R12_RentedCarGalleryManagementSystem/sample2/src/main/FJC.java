import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

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

    public String sendNoticeTo(Customer customer) {
        if (customer != null) {
            return "Overdue notice sent to " + customer.getName() + " " + customer.getSurname();
        }
        return "No customer to send notice to.";
    }
}

class Store {
    private String name;
    private List<Car> cars;
    private List<Rental> rentals;
    private List<OverdueNotice> notices;

    public Store() {
        this.cars = new ArrayList<>();
        this.rentals = new ArrayList<>();
        this.notices = new ArrayList<>();
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

    // Functional Requirement 1: Identify available cars sorted by daily price ascending
    public List<Car> identifyAvailableCars() {
        List<Car> rentedCars = rentals.stream()
                .filter(r -> r.getBackDate() == null)
                .map(Rental::getCar)
                .collect(Collectors.toList());

        List<Car> availableCars = cars.stream()
                .filter(c -> !rentedCars.contains(c))
                .sorted((c1, c2) -> Double.compare(c1.getDailyPrice(), c2.getDailyPrice()))
                .collect(Collectors.toList());

        return availableCars;
    }

    // Functional Requirement 2: Calculate total revenue from all rentals
    public double calculateTotalRevenue() {
        return rentals.stream()
                .mapToDouble(Rental::getTotalPrice)
                .sum();
    }

    // Functional Requirement 3: Find customers with overdue rentals given current date
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

    // Functional Requirement 4: Determine average daily price of all cars
    public double determineAverageDailyPrice() {
        if (cars.isEmpty()) {
            return 0.0;
        }

        double totalDailyPrice = cars.stream()
                .mapToDouble(Car::getDailyPrice)
                .sum();

        return totalDailyPrice / cars.size();
    }

    // Functional Requirement 5: Count rentals per customer
    public Map<Customer, Integer> countCarsRentedPerCustomer() {
        Map<Customer, Integer> rentalCountMap = new HashMap<>();

        for (Rental rental : rentals) {
            Customer customer = rental.getCustomer();
            rentalCountMap.put(customer, rentalCountMap.getOrDefault(customer, 0) + 1);
        }

        return rentalCountMap;
    }
}