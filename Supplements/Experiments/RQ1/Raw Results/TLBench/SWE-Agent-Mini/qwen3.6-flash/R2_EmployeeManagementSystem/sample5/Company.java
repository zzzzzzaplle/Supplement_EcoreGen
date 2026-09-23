import java.util.List;
import java.util.ArrayList;

public class Company {
  private String name;
  private List<Department> departments;
  private List<Employee> employees;

  public Company() {
    this.departments = new ArrayList<>();
    this.employees = new ArrayList<>();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Department> getDepartments() {
    return departments;
  }

  public void addDepartment(Department department) {
    if (!departments.contains(department)) {
      departments.add(department);
    }
  }

  public void removeDepartment(Department department) {
    if (departments.contains(department)) {
      departments.remove(department);
    }
  }

  public List<Employee> getEmployees() {
    return employees;
  }

  public double calculateTotalEmployeeSalary() {
    double total = 0;
    for (Employee emp : employees) {
      if (emp instanceof Manager) {
        total += ((Manager) emp).getSalary();
      } else if (emp instanceof SalesPeople) {
        total += ((SalesPeople) emp).getSalary() + ((SalesPeople) emp).getAmountOfSales() * ((SalesPeople) emp).getCommissionPercentage();
      } else if (emp instanceof Worker) {
        double workerSalary = ((Worker) emp).getWeeklyWorkingHour() * ((Worker) emp).getHourlyRates();
        if (emp instanceof ShiftWorker) {
          workerSalary += ((ShiftWorker) emp).calculateHolidayPremium();
        }
        total += workerSalary;
      }
    }
    return Math.round(total * 100.0) / 100.0;
  }

  public double calculateTotalSalesPeopleCommission() {
    double total = 0;
    for (Employee emp : employees) {
      if (emp instanceof SalesPeople) {
        total += ((SalesPeople) emp).getAmountOfSales() * ((SalesPeople) emp).getCommissionPercentage();
      }
    }
    return Math.round(total * 100.0) / 100.0;
  }

  public double calculateTotalShiftWorkerHolidayPremiums() {
    double total = 0;
    for (Employee emp : employees) {
      if (emp instanceof ShiftWorker) {
        total += ((ShiftWorker) emp).calculateHolidayPremium();
      }
    }
    return Math.round(total * 100.0) / 100.0;
  }
}
