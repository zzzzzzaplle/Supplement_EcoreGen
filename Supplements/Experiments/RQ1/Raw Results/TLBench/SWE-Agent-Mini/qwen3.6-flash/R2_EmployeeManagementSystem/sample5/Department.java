import java.util.List;

public class Department {
  private DepartmentType type;
  private Manager manager;
  private List<Employee> employees;

  public Department() {
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

  public double calculateAverageWorkerWorkingHours() {
    if (employees == null || employees.isEmpty()) {
      return 0;
    }
    int totalHours = 0;
    int workerCount = 0;
    for (Employee emp : employees) {
      if (emp instanceof Worker) {
        totalHours += ((Worker) emp).getWeeklyWorkingHour();
        workerCount++;
      }
    }
    if (workerCount == 0) {
      return 0;
    }
    double avg = (double) totalHours / workerCount;
    return Math.round(avg * 100.0) / 100.0;
  }
}
