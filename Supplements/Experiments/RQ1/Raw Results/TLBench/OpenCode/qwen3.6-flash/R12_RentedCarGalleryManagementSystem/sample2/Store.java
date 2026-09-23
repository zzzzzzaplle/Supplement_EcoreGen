import java.util.*;

public class Store {
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
    List<Car> available = new ArrayList<>();
    for (Car car : cars) {
      boolean isRented = false;
      for (Rental rental : rentals) {
        if (rental.getCar().equals(car)) {
          if (rental.getBackDate() == null) {
            isRented = true;
            break;
          }
        }
      }
      if (!isRented) {
        available.add(car);
      }
    }
    available.sort(Comparator.comparingDouble(Car::getDailyPrice));
    return available;
  }

  public double calculateTotalRevenue() {
    double total = 0.0;
    for (Rental rental : rentals) {
      total += rental.getTotalPrice();
    }
    return total;
  }

  public List<Customer> findCustomersWithOverdueRentals(Date currentDate) {
    List<Customer> overdueCustomers = new ArrayList<>();
    if (currentDate == null) {
      return overdueCustomers;
    }
    Set<Customer> alreadyAdded = new HashSet<>();
    for (Rental rental : rentals) {
      if (rental.getBackDate() == null && currentDate.after(rental.getDueDate())) {
        Customer customer = rental.getCustomer();
        if (!alreadyAdded.contains(customer)) {
          overdueCustomers.add(customer);
          alreadyAdded.add(customer);
        }
      }
    }
    return overdueCustomers;
  }

  public double determineAverageDailyPrice() {
    if (cars.isEmpty()) {
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
    for (Rental rental : rentals) {
      Customer customer = rental.getCustomer();
      countMap.merge(customer, 1, Integer::sum);
    }
    return countMap;
  }
}
