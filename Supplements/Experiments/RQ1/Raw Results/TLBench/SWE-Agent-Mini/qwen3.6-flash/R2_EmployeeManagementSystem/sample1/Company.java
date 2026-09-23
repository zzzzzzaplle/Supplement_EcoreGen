import java.text.DecimalFormat;
import java.util.List;

public class Company {
  private String name;
  private List<Department> departments;
  private List<Employee> employees;

  public Company() {
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
    if (departments == null) {
      departments = new java.util.ArrayList<>();
    }
    departments.add(department);
  }

  public void removeDepartment(Department department) {
    if (departments != null) {
      departments.remove(department);
    }
  }

  public List<Employee> getEmployees() {
    return employees;
  }

  public double calculateTotalEmployeeSalary() {
    if (employees == null || employees.isEmpty()) {
      return 0.0;
    }
    double totalSalary = 0.0;
    for (Employee emp : employees) {
      if (emp instanceof Worker) {
        Worker worker = (Worker) emp;
        double salary = worker.getWeeklyWorkingHour() * worker.getHourlyRates();
        if (emp instanceof ShiftWorker) {
          salary += ((ShiftWorker) emp).getHolidayPremium();
        }
        totalSalary += salary;
      } else if (emp instanceof SalesPeople) {
        SalesPeople sp = (SalesPeople) emp;
        totalSalary += sp.getSalary() + sp.getAmountOfSales() * sp.getCommissionPercentage();
      } else if (emp instanceof Manager) {
        totalSalary += ((Manager) emp).getSalary();
      }
    }
    DecimalFormat df = new DecimalFormat("0.00");
    return Double.parseDouble(df.format(totalSalary));
  }

  public double calculateTotalSalesPeopleCommission() {
    if (employees == null || employees.isEmpty()) {
      return 0.0;
    }
    double totalCommission = 0.0;
    for (Employee emp : employees) {
      if (emp instanceof SalesPeople) {
        SalesPeople sp = (SalesPeople) emp;
        totalCommission += sp.getTotalCommission();
      }
    }
    DecimalFormat df = new DecimalFormat("0.00");
    return Double.parseDouble(df.format(totalCommission));
  }

  public double calculateTotalShiftWorkerHolidayPremiums() {
    if (employees == null || employees.isEmpty()) {
      return 0.0;
    }
    double totalPremiums = 0.0;
    for (Employee emp : employees) {
      if (emp instanceof ShiftWorker) {
        totalPremiums += ((ShiftWorker) emp).getHolidayPremium();
      }
    }
    DecimalFormat df = new DecimalFormat("0.00");
    return Double.parseDouble(df.format(totalPremiums));
  }
}
