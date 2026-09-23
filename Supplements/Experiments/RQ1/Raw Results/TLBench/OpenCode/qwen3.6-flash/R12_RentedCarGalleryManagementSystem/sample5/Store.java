import java.util.*;

public class Store {
  private String name;
  private List<Car> cars;
  private List<Rental> rentals;
  private List<OverdueNotice> notices;

  public Store() {
    cars = new ArrayList<>();
    rentals = new ArrayList<>();
    notices = new ArrayList<>();
  }

  public List<Car> identifyAvailableCars() {
    List<Car> availableCars = new ArrayList<>();
    for (Car car : cars) {
      boolean isRentedActive = false;
      for (Rental rental : rentals) {
        if (rental.getCar() != null && rental.getCar() == car && rental.getBackDate() == null) {
          isRentedActive = true;
          break;
        }
      }
      if (!isRentedActive) {
        availableCars.add(car);
      }
    }
    availableCars.sort(new Comparator<Car>() {
      public int compare(Car c1, Car c2) {
        return Double.compare(c1.getDailyPrice(), c2.getDailyPrice());
      }
    });
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
    List<Customer> overdueCustomers = new ArrayList<>();
    if (currentDate == null) {
      return overdueCustomers;
    }
    for (Rental rental : rentals) {
      if (rental.getBackDate() == null && rental.getDueDate() != null && rental.getDueDate().before(currentDate)) {
        Customer customer = rental.getCustomer();
        boolean alreadyAdded = false;
        for (Customer c : overdueCustomers) {
          if (c == customer) {
            alreadyAdded = true;
            break;
          }
        }
        if (!alreadyAdded) {
          overdueCustomers.add(customer);
        }
      }
    }
    return overdueCustomers;
  }

  public double determineAverageDailyPrice() {
    if (cars.isEmpty()) {
      return 0.0;
    }
    double total = 0.0;
    for (Car car : cars) {
      total += car.getDailyPrice();
    }
    return total / cars.size();
  }

  public Map<Customer, Integer> countCarsRentedPerCustomer() {
    Map<Customer, Integer> counts = new HashMap<>();
    for (Rental rental : rentals) {
      Customer customer = rental.getCustomer();
      if (customer == null) {
        continue;
      }
      int count = counts.getOrDefault(customer, 0);
      counts.put(customer, count + 1);
    }
    return counts;
  }

  public void addCar(Car car) {
    cars.add(car);
  }

  public void addRental(Rental rental) {
    rentals.add(rental);
  }

  public void addNotice(OverdueNotice notice) {
    notices.add(notice);
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

  public List<Rental> getRentals() {
    return rentals;
  }

  public void setRentals(List<Rental> rentals) {
    this.rentals = rentals;
  }

  public List<OverdueNotice> getNotices() {
    return notices;
  }

  public void setNotices(List<OverdueNotice> notices) {
    this.notices = notices;
  }
}
