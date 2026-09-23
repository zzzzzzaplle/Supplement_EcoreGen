import java.util.List;
import java.util.ArrayList;

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
        double sum = 0;
        int count = 0;
        for (Employee e : employees) {
            if (e instanceof Worker) {
                sum += ((Worker) e).getWeeklyWorkingHour();
                count++;
            }
        }
        if (count == 0) return 0;
        return Math.round((sum / count) * 100.0) / 100.0;
    }
}
