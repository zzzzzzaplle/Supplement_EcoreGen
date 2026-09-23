import java.util.ArrayList;
import java.util.List;

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
        if (departments == null) {
            departments = new ArrayList<Department>();
        }
        departments.add(department);
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
        double total = 0.0;
        if (employees == null) {
            return 0.0;
        }
        for (Employee emp : employees) {
            if (emp instanceof Manager) {
                total += ((Manager) emp).getSalary();
            } else if (emp instanceof SalesPeople) {
                SalesPeople sp = (SalesPeople) emp;
                total += sp.getSalary() + sp.getTotalCommission();
            } else if (emp instanceof Worker) {
                Worker w = (Worker) emp;
                total += w.getWeeklyWorkingHour() * w.getHourlyRates();
                if (emp instanceof ShiftWorker) {
                    total += ((ShiftWorker) emp).getHolidayPremium();
                }
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }

    public double calculateTotalSalesPeopleCommission() {
        double total = 0.0;
        if (employees == null) {
            return 0.0;
        }
        for (Employee emp : employees) {
            if (emp instanceof SalesPeople) {
                SalesPeople sp = (SalesPeople) emp;
                total += sp.getTotalCommission();
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }

    public double calculateTotalShiftWorkerHolidayPremiums() {
        double total = 0.0;
        if (employees == null) {
            return 0.0;
        }
        for (Employee emp : employees) {
            if (emp instanceof ShiftWorker) {
                ShiftWorker sw = (ShiftWorker) emp;
                total += sw.calculateHolidayPremium();
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }
}
