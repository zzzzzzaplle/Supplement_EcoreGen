import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public void addDepartment(Department department) {
        if (!departments.contains(department)) {
            departments.add(department);
        }
    }

    public void removeDepartment(Department department) {
        departments.remove(department);
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public double calculateTotalEmployeeSalary() {
        double totalSalary = 0.0;
        for (Employee emp : employees) {
            if (emp instanceof Worker) {
                Worker worker = (Worker) emp;
                double salary = worker.getWeeklyWorkingHour() * worker.getHourlyRates();
                if (emp instanceof ShiftWorker) {
                    salary += ((ShiftWorker) emp).getHolidayPremium();
                }
                totalSalary += salary;
            } else if (emp instanceof SalesPeople) {
                SalesPeople sp = (SalesPeople) emp;
                double salary = sp.getSalary() + sp.getAmountOfSales() * sp.getCommissionPercentage();
                totalSalary += salary;
            } else if (emp instanceof Manager) {
                Manager mgr = (Manager) emp;
                totalSalary += mgr.getSalary();
            }
        }
        return roundToTwoDecimalPlaces(totalSalary);
    }

    public double calculateTotalSalesPeopleCommission() {
        double totalCommission = 0.0;
        for (Employee emp : employees) {
            if (emp instanceof SalesPeople) {
                SalesPeople sp = (SalesPeople) emp;
                totalCommission += sp.getAmountOfSales() * sp.getCommissionPercentage();
            }
        }
        return roundToTwoDecimalPlaces(totalCommission);
    }

    public double calculateTotalShiftWorkerHolidayPremiums() {
        double totalPremiums = 0.0;
        for (Employee emp : employees) {
            if (emp instanceof ShiftWorker) {
                totalPremiums += ((ShiftWorker) emp).getHolidayPremium();
            }
        }
        return roundToTwoDecimalPlaces(totalPremiums);
    }

    private double roundToTwoDecimalPlaces(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
