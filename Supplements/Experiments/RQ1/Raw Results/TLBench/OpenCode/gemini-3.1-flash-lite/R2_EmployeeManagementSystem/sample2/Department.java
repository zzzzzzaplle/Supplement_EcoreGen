import java.util.ArrayList;
import java.util.List;

public class Department {
    private DepartmentType type;
    private Manager manager;
    private List<Employee> employees;

    public Department() {
        this.employees = new ArrayList<>();
    }

    public DepartmentType getType() { return type; }
    public void setType(DepartmentType type) { this.type = type; }
    public Manager getManager() { return manager; }
    public void setManager(Manager manager) { this.manager = manager; }
    public List<Employee> getEmployees() { return employees; }
    public void setEmployees(List<Employee> employees) { this.employees = employees; }

    public double calculateAverageWorkerWorkingHours() {
        int totalHours = 0;
        int count = 0;
        for (Employee e : employees) {
            if (e instanceof Worker) {
                totalHours += ((Worker) e).getWeeklyWorkingHour();
                count++;
            }
        }
        return count == 0 ? 0.0 : Math.round((double) totalHours / count * 100.0) / 100.0;
    }
}
