import java.util.List;
import java.util.ArrayList;

public class Company {
    private String name;
    private List<Department> departments;
    private List<Employee> employees;

    public Company() {
        this.departments = new ArrayList<Department>();
        this.employees = new ArrayList<Employee>();
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
        if (departments != null) {
            departments.add(department);
        }
    }

    public void removeDepartment(Department department) {
        if (departments != null) {
            departments.remove(department);
        }
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public double calculateTotalEmployeeSalary() {
        if (employees == null || employees.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (Employee emp : employees) {
            if (emp instanceof Worker) {
                Worker worker = (Worker) emp;
                double workerSalary = worker.getWeeklyWorkingHour() * worker.getHourlyRates();
                if (emp instanceof ShiftWorker) {
                    workerSalary += ((ShiftWorker) emp).calculateHolidayPremium();
                }
                total += workerSalary;
            } else if (emp instanceof SalesPeople) {
                SalesPeople sp = (SalesPeople) emp;
                total += sp.getSalary() + sp.getAmountOfSales() * sp.getCommissionPercentage();
            } else if (emp instanceof Manager) {
                total += ((Manager) emp).getSalary();
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }

    public double calculateTotalSalesPeopleCommission() {
        if (employees == null || employees.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (Employee emp : employees) {
            if (emp instanceof SalesPeople) {
                SalesPeople sp = (SalesPeople) emp;
                total += sp.getAmountOfSales() * sp.getCommissionPercentage();
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }

    public double calculateTotalShiftWorkerHolidayPremiums() {
        if (employees == null || employees.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (Employee emp : employees) {
            if (emp instanceof ShiftWorker) {
                total += ((ShiftWorker) emp).calculateHolidayPremium();
            }
        }
        if (total == 0.0) {
            return 0.0;
        }
        return Math.round(total * 100.0) / 100.0;
    }
}
