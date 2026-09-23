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
        int workerCount = 0;
        int totalHours = 0;
        for (Employee employee : employees) {
            if (employee instanceof Worker) {
                workerCount++;
                totalHours += ((Worker) employee).getWeeklyWorkingHour();
            }
        }
        if (workerCount == 0) {
            return 0;
        }
        BigDecimal avg = BigDecimal.valueOf((double) totalHours / workerCount).setScale(2, RoundingMode.HALF_UP);
        return avg.doubleValue();
    }
}
