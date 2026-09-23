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

    /**
     * Identify available cars: cars not tied to any active rental (rental with null backDate).
     * Return sorted ascending by dailyPrice.
     */
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

    /**
     * Calculate total revenue from all rentals.
     */
    public double calculateTotalRevenue() {
        return rentals.stream()
                .mapToDouble(Rental::getTotalPrice)
                .sum();
    }

    /**
     * Find customers whose rentals are overdue.
     * Overdue: return date is null and currentDate > dueDate.
     * Return empty list if currentDate is null or no overdue rentals.
     */
    public List<Customer> findCustomersWithOverdueRentals(Date currentDate) {
        if (currentDate == null) {
            return new ArrayList<>();
        }
        return rentals.stream()
                .filter(r -> r.getBackDate() == null && currentDate.after(r.getDueDate()))
                .map(Rental::getCustomer)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Determine average daily price of all cars.
     * Return 0.0 if no cars.
     */
    public double determineAverageDailyPrice() {
        if (cars.isEmpty()) {
            return 0.0;
        }
        return cars.stream()
                .mapToDouble(Car::getDailyPrice)
                .average()
                .orElse(0.0);
    }

    /**
     * Count how many rental records belong to each customer.
     * Return empty map if no rentals.
     */
    public Map<Customer, Integer> countCarsRentedPerCustomer() {
        return rentals.stream()
                .collect(Collectors.groupingBy(Rental::getCustomer, Collectors.summingInt(r -> 1)));
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(name, customer.name) &&
                Objects.equals(surname, customer.surname) &&
                Objects.equals(address, customer.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, address);
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

    /**
     * Sends a notice to the given customer and returns a confirmation message.
     */
    public String sendNoticeTo(Customer customer) {
        if (customer == null) {
            return "No customer specified.";
        }
        return "Overdue notice " + noticeId + " sent to " + customer.getName() + " " + customer.getSurname();
    }
}