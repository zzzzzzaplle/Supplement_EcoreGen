import java.util.ArrayList;
import java.util.List;

public class Company {
    private String name;
    private List<Department> departments;
    private List<Employee> employees;

    public Company() {
        departments = new ArrayList<>();
        employees = new ArrayList<>();
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

    public void addDepartment(Department department) {
        departments.add(department);
    }

    public void removeDepartment(Department department) {
        departments.remove(department);
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public double calculateTotalEmployeeSalary() {
        double totalSalary = 0;
        for (Employee emp : employees) {
            if (emp instanceof ShiftWorker) {
                ShiftWorker sw = (ShiftWorker) emp;
                totalSalary += sw.getWeeklyWorkingHour() * sw.getHourlyRates() + sw.calculateHolidayPremium();
            } else if (emp instanceof OffShiftWorker) {
                OffShiftWorker ow = (OffShiftWorker) emp;
                totalSalary += ow.getWeeklyWorkingHour() * ow.getHourlyRates();
            } else if (emp instanceof SalesPeople) {
                SalesPeople sp = (SalesPeople) emp;
                totalSalary += sp.getSalary() + sp.getAmountOfSales() * sp.getCommissionPercentage();
            } else if (emp instanceof Manager) {
                Manager mgr = (Manager) emp;
                totalSalary += mgr.getSalary();
            }
        }
        return Math.round(totalSalary * 100.0) / 100.0;
    }

    public double calculateTotalSalesPeopleCommission() {
        double totalCommission = 0;
        for (Employee emp : employees) {
            if (emp instanceof SalesPeople) {
                SalesPeople sp = (SalesPeople) emp;
                totalCommission += sp.getTotalCommission();
            }
        }
        return Math.round(totalCommission * 100.0) / 100.0;
    }

    public double calculateTotalShiftWorkerHolidayPremiums() {
        double totalPremium = 0;
        for (Employee emp : employees) {
            if (emp instanceof ShiftWorker) {
                ShiftWorker sw = (ShiftWorker) emp;
                totalPremium += sw.calculateHolidayPremium();
            }
        }
        return Math.round(totalPremium * 100.0) / 100.0;
    }
}
