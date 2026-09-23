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
        this.name = "";
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
        // A car is available when it is not tied to an active rental whose return date (backDate) is still empty.
        // Active rental: backDate is null.
        // So we want cars that do NOT have any rental where backDate == null.
        
        // First, find all car plate numbers that are currently "unreturned" (active rental)
        List<String> unreturnedCarPlates = rentals.stream()
                .filter(rental -> rental.getBackDate() == null)
                .map(rental -> rental.getCar().getPlate())
                .distinct()
                .collect(Collectors.toList());
        
        // Filter cars that are not in the unreturned list
        List<Car> availableCars = cars.stream()
                .filter(car -> !unreturnedCarPlates.contains(car.getPlate()))
                .collect(Collectors.toList());
        
        // Sort by daily price ascending
        availableCars.sort((c1, c2) -> Double.compare(c1.getDailyPrice(), c2.getDailyPrice()));
        
        return availableCars;
    }

    public double calculateTotalRevenue() {
        double total = 0.0;
        for (Rental rental : rentals) {
            total += rental.getTotalPrice();
        }
        return total;
    }

    public List<Customer> findCustomersWithOverdueRentals(Date currentDate) {
        // Return an empty list when no overdue customers exist, or when the current date input is null.
        if (currentDate == null) {
            return new ArrayList<>();
        }

        List<Customer> overdueCustomers = new ArrayList<>();
        for (Rental rental : rentals) {
            // A rental is overdue when its return date (backDate) is empty and the current date is later than the due date.
            if (rental.getBackDate() == null && rental.getDueDate() != null && currentDate.after(rental.getDueDate())) {
                Customer customer = rental.getCustomer();
                if (!overdueCustomers.contains(customer)) {
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
        
        double sum = 0.0;
        for (Car car : cars) {
            sum += car.getDailyPrice();
        }
        return sum / cars.size();
    }

    public Map<Customer, Integer> countCarsRentedPerCustomer() {
        Map<Customer, Integer> counts = new HashMap<>();
        if (rentals == null || rentals.isEmpty()) {
            return counts;
        }
        
        for (Rental rental : rentals) {
            Customer customer = rental.getCustomer();
            counts.put(customer, counts.getOrDefault(customer, 0) + 1);
        }
        return counts;
    }
}

class Car {
    private String plate;
    private String model;
    private double dailyPrice;

    public Car() {
        this.plate = "";
        this.model = "";
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
        this.name = "";
        this.surname = "";
        this.address = "";
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
        this.rentalDate = null;
        this.dueDate = null;
        this.backDate = null;
        this.totalPrice = 0.0;
        this.leasingTerms = "";
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
        this.noticeId = "";
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
        this.setCustomer(customer);
        return "Notice " + noticeId + " sent to " + (customer != null ? customer.getName() : "unknown") + " " + (customer != null ? customer.getSurname() : "");
    }
}