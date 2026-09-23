import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

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

    public double calculateAverageWorkerWorkingHours() {
        List<Worker> workers = new ArrayList<>();
        for (Employee employee : employees) {
            if (employee instanceof Worker) {
                workers.add((Worker) employee);
            }
        }
        if (workers.isEmpty()) {
            return 0;
        }
        BigDecimal totalHours = BigDecimal.valueOf(0);
        for (Worker worker : workers) {
            totalHours = totalHours.add(BigDecimal.valueOf(worker.getWeeklyWorkingHour()));
        }
        BigDecimal count = BigDecimal.valueOf(workers.size());
        BigDecimal average = totalHours.divide(count, 2, RoundingMode.HALF_UP);
        return average.doubleValue();
    }
}
