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
        departments.add(department);
    }

    public void removeDepartment(Department department) {
        departments.remove(department);
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public double calculateTotalEmployeeSalary() {
        double total = 0.0;
        if (employees != null) {
            for (Employee emp : employees) {
                if (emp instanceof ShiftWorker) {
                    ShiftWorker sw = (ShiftWorker) emp;
                    total += sw.getWeeklyWorkingHour() * sw.getHourlyRates() + sw.getHolidayPremium();
                } else if (emp instanceof Worker) {
                    Worker w = (Worker) emp;
                    total += w.getWeeklyWorkingHour() * w.getHourlyRates();
                } else if (emp instanceof SalesPeople) {
                    SalesPeople sp = (SalesPeople) emp;
                    total += sp.getSalary() + sp.getAmountOfSales() * sp.getCommissionPercentage();
                } else if (emp instanceof Manager) {
                    Manager m = (Manager) emp;
                    total += m.getSalary();
                }
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }

    public double calculateTotalSalesPeopleCommission() {
        double total = 0.0;
        if (employees != null) {
            for (Employee emp : employees) {
                if (emp instanceof SalesPeople) {
                    SalesPeople sp = (SalesPeople) emp;
                    total += sp.getAmountOfSales() * sp.getCommissionPercentage();
                }
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }

    public double calculateTotalShiftWorkerHolidayPremiums() {
        double total = 0.0;
        if (employees != null) {
            for (Employee emp : employees) {
                if (emp instanceof ShiftWorker) {
                    ShiftWorker sw = (ShiftWorker) emp;
                    total += sw.getHolidayPremium();
                }
            }
        }
        if (total == 0.0) {
            return 0.0;
        }
        return Math.round(total * 100.0) / 100.0;
    }
}
