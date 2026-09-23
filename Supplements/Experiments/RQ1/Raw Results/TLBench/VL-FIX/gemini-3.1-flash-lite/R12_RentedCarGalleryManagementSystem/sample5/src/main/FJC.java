import java.util.*;
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
        return "Notice sent to " + customer.getName();
    }
}

class Store {
    private String name;
    private List<Car> cars = new ArrayList<>();
    private List<Rental> rentals = new ArrayList<>();
    private List<OverdueNotice> notices = new ArrayList<>();

    public Store() {
    }

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

    public double calculateTotalRevenue() {
        return rentals.stream().mapToDouble(Rental::getTotalPrice).sum();
    }

    public List<Customer> findCustomersWithOverdueRentals(Date currentDate) {
        if (currentDate == null) return new ArrayList<>();
        return rentals.stream()
                .filter(r -> r.getBackDate() == null && r.getDueDate() != null && r.getDueDate().before(currentDate))
                .map(Rental::getCustomer)
                .distinct()
                .collect(Collectors.toList());
    }

    public double determineAverageDailyPrice() {
        if (cars.isEmpty()) return 0.0;
        return cars.stream().mapToDouble(Car::getDailyPrice).average().orElse(0.0);
    }

    public Map<Customer, Integer> countCarsRentedPerCustomer() {
        return rentals.stream()
                .collect(Collectors.groupingBy(Rental::getCustomer, Collectors.summingInt(e -> 1)));
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Car> getCars() { return cars; }
    public void setCars(List<Car> cars) { this.cars = cars; }
    public void addCar(Car car) { this.cars.add(car); }
    public List<Rental> getRentals() { return rentals; }
    public void setRentals(List<Rental> rentals) { this.rentals = rentals; }
    public void addRental(Rental rental) { this.rentals.add(rental); }
    public List<OverdueNotice> getNotices() { return notices; }
    public void setNotices(List<OverdueNotice> notices) { this.notices = notices; }
    public void addNotice(OverdueNotice notice) { this.notices.add(notice); }
}