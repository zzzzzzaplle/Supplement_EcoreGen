public class Department {
    private DepartmentType type;
    private Manager manager;
    private java.util.List<Employee> employees;

    public Department() {
        this.employees = new java.util.ArrayList<>();
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

    public java.util.List<Employee> getEmployees() {
        return employees;
    }

    public double calculateAverageWorkerWorkingHours() {
        int workerCount = 0;
        int totalHours = 0;
        for (Employee employee : employees) {
            if (employee instanceof Worker) {
                totalHours += ((Worker) employee).getWeeklyWorkingHour();
                workerCount++;
            }
        }
        if (workerCount == 0) {
            return 0;
        }
        return Math.round(((double) totalHours / workerCount) * 100.0) / 100.0;
    }
}
