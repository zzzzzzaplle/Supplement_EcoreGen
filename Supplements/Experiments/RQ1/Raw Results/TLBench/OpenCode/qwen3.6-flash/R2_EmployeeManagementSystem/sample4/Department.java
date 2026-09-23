import java.util.ArrayList;
import java.util.List;

public class Department {
    private DepartmentType type;
    private Manager manager;
    private List<Employee> employees;

    public Department() {
        employees = new ArrayList<>();
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
        if (type != DepartmentType.DELIVERY) {
            return 0;
        }
        int totalHours = 0;
        int count = 0;
        for (Employee emp : employees) {
            if (emp instanceof Worker) {
                Worker worker = (Worker) emp;
                totalHours += worker.getWeeklyWorkingHour();
                count++;
            }
        }
        if (count == 0) {
            return 0;
        }
        return Math.round(((double) totalHours / count) * 100.0) / 100.0;
    }
}
