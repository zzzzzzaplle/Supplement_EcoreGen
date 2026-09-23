import java.util.*;

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

    public List<Car> identifyAvailableCars() {
        List<Car> availableCars = new ArrayList<>();
        if (cars == null) {
            return availableCars;
        }
        for (Car car : cars) {
            boolean isAvailable = true;
            if (rentals != null) {
                for (Rental rental : rentals) {
                    if (rental.getCar() != null && rental.getCar().equals(car) && rental.getBackDate() == null) {
                        isAvailable = false;
                        break;
                    }
                }
            }
            if (isAvailable) {
                availableCars.add(car);
            }
        }
        availableCars.sort(Comparator.comparingDouble(Car::getDailyPrice));
        return availableCars;
    }

    public double calculateTotalRevenue() {
        double total = 0.0;
        if (rentals != null) {
            for (Rental rental : rentals) {
                total += rental.getTotalPrice();
            }
        }
        return total;
    }

    public List<Customer> findCustomersWithOverdueRentals(Date currentDate) {
        List<Customer> overdueCustomers = new ArrayList<>();
        if (currentDate == null) {
            return overdueCustomers;
        }
        if (rentals != null) {
            for (Rental rental : rentals) {
                if (rental.getBackDate() == null && rental.getDueDate() != null && currentDate.after(rental.getDueDate())) {
                    Customer customer = rental.getCustomer();
                    if (customer != null && !overdueCustomers.contains(customer)) {
                        overdueCustomers.add(customer);
                    }
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
        Map<Customer, Integer> countMap = new HashMap<>();
        if (rentals != null) {
            for (Rental rental : rentals) {
                Customer customer = rental.getCustomer();
                if (customer != null) {
                    countMap.merge(customer, 1, Integer::sum);
                }
            }
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
        return "Overdue notice " + noticeId + " has been sent to " +
                (customer != null ? customer.getName() + " " + customer.getSurname() : "unknown customer");
    }
}