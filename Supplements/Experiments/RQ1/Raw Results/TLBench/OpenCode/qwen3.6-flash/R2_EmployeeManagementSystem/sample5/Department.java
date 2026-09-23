import java.util.List;
import java.util.ArrayList;

public class Department {
  private DepartmentType type;
  private Manager manager;
  private List<Employee> employees;

  public Department() {
    this.employees = new ArrayList<>();
  }

  public DepartmentType getType() {
    return type;
  }

  public void setType(DepartmentType type) {
    this.type = type;
  }

  public Manager getManager() {
    return manager;
  }

  public void setManager(Manager manager) {
    this.manager = manager;
  }

  public List<Employee> getEmployees() {
    return employees;
  }

  public void setEmployees(List<Employee> employees) {
    this.employees = employees;
  }

  public double calculateAverageWorkerWorkingHours() {
    int workerCount = 0;
    int totalHours = 0;
    for (Employee e : employees) {
      if (e instanceof Worker) {
        workerCount++;
        totalHours += ((Worker) e).getWeeklyWorkingHour();
      }
    }
    if (workerCount == 0) {
      return 0;
    }
    double result = (double) totalHours / workerCount;
    return Math.round(result * 100.0) / 100.0;
  }
}
