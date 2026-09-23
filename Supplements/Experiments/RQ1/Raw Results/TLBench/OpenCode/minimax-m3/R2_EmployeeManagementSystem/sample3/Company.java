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
        if (this.departments == null) {
            this.departments = new ArrayList<Department>();
        }
        this.departments.add(department);
    }

    public void removeDepartment(Department department) {
        if (this.departments != null) {
            this.departments.remove(department);
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
        for (Employee e : employees) {
            total += calculateEmployeeSalary(e);
        }
        return Math.round(total * 100.0) / 100.0;
    }

    private double calculateEmployeeSalary(Employee e) {
        if (e instanceof Manager) {
            return ((Manager) e).getSalary();
        } else if (e instanceof ShiftWorker) {
            ShiftWorker sw = (ShiftWorker) e;
            return sw.getWeeklyWorkingHour() * sw.getHourlyRates() + sw.getHolidayPremium();
        } else if (e instanceof OffShiftWorker) {
            OffShiftWorker osw = (OffShiftWorker) e;
            return osw.getWeeklyWorkingHour() * osw.getHourlyRates();
        } else if (e instanceof SalesPeople) {
            SalesPeople sp = (SalesPeople) e;
            return sp.getSalary() + sp.getAmountOfSales() * sp.getCommissionPercentage();
        }
        return 0.0;
    }

    public double calculateTotalSalesPeopleCommission() {
        double total = 0.0;
        if (employees == null) {
            return 0.0;
        }
        for (Employee e : employees) {
            if (e instanceof SalesPeople) {
                SalesPeople sp = (SalesPeople) e;
                total += sp.getAmountOfSales() * sp.getCommissionPercentage();
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }

    public double calculateTotalShiftWorkerHolidayPremiums() {
        double total = 0.0;
        if (employees == null) {
            return 0.0;
        }
        for (Employee e : employees) {
            if (e instanceof ShiftWorker) {
                total += ((ShiftWorker) e).getHolidayPremium();
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }
}
