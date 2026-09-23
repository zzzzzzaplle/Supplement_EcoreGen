import java.util.ArrayList;
import java.util.List;

public class Company {
    private String name;
    private List<Department> departments;
    private List<Employee> employees;

    public Company() {
        this.departments = new ArrayList<>();
        this.employees = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public void addDepartment(Department department) {
        if (department == null) {
            return;
        }
        if (departments == null) {
            departments = new ArrayList<>();
        }
        departments.add(department);
    }

    public void removeDepartment(Department department) {
        if (departments == null || department == null) {
            return;
        }
        departments.remove(department);
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public double calculateTotalEmployeeSalary() {
        if (employees == null || employees.isEmpty()) {
            return 0;
        }
        double total = 0;
        for (Employee employee : employees) {
            if (employee instanceof ShiftWorker) {
                ShiftWorker sw = (ShiftWorker) employee;
                total += sw.getWeeklyWorkingHour() * sw.getHourlyRates();
                total += sw.getHolidayPremium();
            } else if (employee instanceof Worker) {
                Worker worker = (Worker) employee;
                total += worker.getWeeklyWorkingHour() * worker.getHourlyRates();
            } else if (employee instanceof SalesPeople) {
                SalesPeople sp = (SalesPeople) employee;
                total += sp.getSalary() + sp.getAmountOfSales() * sp.getCommissionPercentage();
            } else if (employee instanceof Manager) {
                Manager manager = (Manager) employee;
                total += manager.getSalary();
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }

    public double calculateTotalSalesPeopleCommission() {
        if (employees == null || employees.isEmpty()) {
            return 0;
        }
        double total = 0;
        for (Employee employee : employees) {
            if (employee instanceof SalesPeople) {
                SalesPeople sp = (SalesPeople) employee;
                total += sp.getAmountOfSales() * sp.getCommissionPercentage();
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }

    public double calculateTotalShiftWorkerHolidayPremiums() {
        if (employees == null || employees.isEmpty()) {
            return 0;
        }
        double total = 0;
        boolean hasShiftWorker = false;
        for (Employee employee : employees) {
            if (employee instanceof ShiftWorker) {
                ShiftWorker sw = (ShiftWorker) employee;
                total += sw.getHolidayPremium();
                hasShiftWorker = true;
            }
        }
        if (!hasShiftWorker) {
            return 0;
        }
        return Math.round(total * 100.0) / 100.0;
    }
}
