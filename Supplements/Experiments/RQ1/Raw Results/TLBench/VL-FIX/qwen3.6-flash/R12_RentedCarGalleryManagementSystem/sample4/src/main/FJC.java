import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public List<Car> identifyAvailableCars() {
        List<Rental> activeRentals = getActiveRentals();
        List<Car> rentedCars = new ArrayList<>();
        for (Rental rental : activeRentals) {
            rentedCars.add(rental.getCar());
        }

        List<Car> availableCars = new ArrayList<>();
        for (Car car : this.cars) {
            if (!rentedCars.contains(car)) {
                availableCars.add(car);
            }
        }

        availableCars.sort((c1, c2) -> Double.compare(c1.getDailyPrice(), c2.getDailyPrice()));

        return availableCars;
    }

    public double calculateTotalRevenue() {
        double total = 0.0;
        for (Rental rental : this.rentals) {
            total += rental.getTotalPrice();
        }
        return total;
    }

    public List<Customer> findCustomersWithOverdueRentals(Date currentDate) {
        if (currentDate == null) {
            return new ArrayList<>();
        }

        List<Rental> overdueRentals = new ArrayList<>();
        for (Rental rental : this.rentals) {
            if (isOverdue(rental, currentDate)) {
                overdueRentals.add(rental);
            }
        }

        Map<Customer, Boolean> seenCustomers = new HashMap<>();
        List<Customer> result = new ArrayList<>();
        for (Rental rental : overdueRentals) {
            Customer customer = rental.getCustomer();
            if (!seenCustomers.containsKey(customer)) {
                result.add(customer);
                seenCustomers.put(customer, true);
            }
        }

        return result;
    }

    public double determineAverageDailyPrice() {
        if (this.cars == null || this.cars.isEmpty()) {
            return 0.0;
        }

        double total = 0.0;
        for (Car car : this.cars) {
            total += car.getDailyPrice();
        }

        return total / this.cars.size();
    }

    public Map<Customer, Integer> countCarsRentedPerCustomer() {
        Map<Customer, Integer> countMap = new HashMap<>();
        if (this.rentals == null || this.rentals.isEmpty()) {
            return countMap;
        }

        for (Rental rental : this.rentals) {
            Customer customer = rental.getCustomer();
            countMap.put(customer, countMap.getOrDefault(customer, 0) + 1);
        }

        return countMap;
    }

    private List<Rental> getActiveRentals() {
        List<Rental> activeRentals = new ArrayList<>();
        for (Rental rental : this.rentals) {
            if (rental.getBackDate() == null) {
                activeRentals.add(rental);
            }
        }
        return activeRentals;
    }

    private boolean isOverdue(Rental rental, Date currentDate) {
        if (rental.getBackDate() != null) {
            return false;
        }
        if (rental.getDueDate() == null) {
            return false;
        }
        return currentDate.after(rental.getDueDate());
    }
}

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
        this.customer = customer;
        return "Overdue notice sent to " + customer.getName() + " " + customer.getSurname();
    }
}