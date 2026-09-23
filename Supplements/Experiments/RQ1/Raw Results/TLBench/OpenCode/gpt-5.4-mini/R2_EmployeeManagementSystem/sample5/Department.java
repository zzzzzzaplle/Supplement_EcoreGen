import java.util.ArrayList;
import java.util.List;

public class Department {
    private DepartmentType type;
    private Manager manager;
    private List<Employee> employees;

    public Department() {
        this.employees = new ArrayList<Employee>();
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
        if (employees == null || employees.isEmpty()) {
            return 0.0;
        }
        int totalHours = 0;
        int workerCount = 0;
        for (Employee employee : employees) {
            if (employee instanceof Worker) {
                totalHours += ((Worker) employee).getWeeklyWorkingHour();
                workerCount++;
            }
        }
        if (workerCount == 0) {
            return 0.0;
        }
        return Math.round((totalHours * 1.0 / workerCount) * 100.0) / 100.0;
    }
}
