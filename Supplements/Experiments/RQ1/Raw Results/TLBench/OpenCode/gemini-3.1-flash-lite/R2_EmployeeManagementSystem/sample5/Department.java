import java.util.ArrayList;
import java.util.List;

public class Department {
    private DepartmentType type;
    private Manager manager;
    private List<Employee> employees = new ArrayList<>();

    public Department() {}

    public DepartmentType getType() { return type; }
    public void setType(DepartmentType type) { this.type = type; }
    public Manager getManager() { return manager; }
    public void setManager(Manager manager) { this.manager = manager; }
    public List<Employee> getEmployees() { return employees; }
    public void setEmployees(List<Employee> employees) { this.employees = employees; }

    public double calculateAverageWorkerWorkingHours() {
        int totalHours = 0;
        int workerCount = 0;
        for (Employee e : employees) {
            if (e instanceof Worker) {
                totalHours += ((Worker) e).getWeeklyWorkingHour();
                workerCount++;
            }
        }
        if (workerCount == 0) return 0.0;
        return Math.round((double) totalHours / workerCount * 100.0) / 100.0;
    }
}
