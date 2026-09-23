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
        int workerCount = 0;
        for (Employee e : employees) {
            if (e instanceof Worker) {
                totalHours += ((Worker) e).getWeeklyWorkingHour();
                workerCount++;
            }
        }
        if (workerCount == 0) return 0.0;
        double avg = (double) totalHours / workerCount;
        return Math.round(avg * 100.0) / 100.0;
    }
}
