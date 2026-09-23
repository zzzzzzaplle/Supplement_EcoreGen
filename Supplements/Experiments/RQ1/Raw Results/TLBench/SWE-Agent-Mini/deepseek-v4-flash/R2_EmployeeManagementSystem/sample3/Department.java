import java.util.List;
import java.util.ArrayList;

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
        List<Worker> workers = new ArrayList<Worker>();
        for (Employee emp : employees) {
            if (emp instanceof Worker) {
                workers.add((Worker) emp);
            }
        }
        if (workers.isEmpty()) {
            return 0.0;
        }
        double totalHours = 0;
        for (Worker w : workers) {
            totalHours += w.getWeeklyWorkingHour();
        }
        double average = totalHours / workers.size();
        return Math.round(average * 100.0) / 100.0;
    }
}
