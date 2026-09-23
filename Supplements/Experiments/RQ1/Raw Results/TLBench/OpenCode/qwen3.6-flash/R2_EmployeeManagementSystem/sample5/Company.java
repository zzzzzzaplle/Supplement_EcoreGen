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

  public void setDepartments(List<Department> departments) {
    this.departments = departments;
  }

  public List<Employee> getEmployees() {
    return employees;
  }

  public void setEmployees(List<Employee> employees) {
    this.employees = employees;
  }

  public void addDepartment(Department department) {
    departments.add(department);
  }

  public void removeDepartment(Department department) {
    departments.remove(department);
  }

  public double calculateTotalEmployeeSalary() {
    double total = 0;
    for (Employee e : employees) {
      total += e.calculateSalary();
    }
    return Math.round(total * 100.0) / 100.0;
  }

  public double calculateTotalSalesPeopleCommission() {
    double total = 0;
    for (Employee e : employees) {
      if (e instanceof SalesPeople) {
        total += e.calculateCommission();
      }
    }
    return Math.round(total * 100.0) / 100.0;
  }

  public double calculateTotalShiftWorkerHolidayPremiums() {
    double total = 0;
    for (Employee e : employees) {
      if (e instanceof ShiftWorker) {
        total += e.calculateHolidayPremium();
      }
    }
    return Math.round(total * 100.0) / 100.0;
  }
}
