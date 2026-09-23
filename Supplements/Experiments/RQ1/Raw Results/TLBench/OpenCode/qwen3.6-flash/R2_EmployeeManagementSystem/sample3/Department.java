import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;

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
        int totalHours = 0;
        int workerCount = 0;
        if (employees != null) {
            for (Employee emp : employees) {
                if (emp instanceof Worker) {
                    totalHours += ((Worker) emp).getWeeklyWorkingHour();
                    workerCount++;
                }
            }
        }
        if (workerCount == 0) {
            return 0;
        }
        BigDecimal avg = new BigDecimal((double) totalHours / workerCount);
        return avg.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
