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
        this.departments.add(department);
    }

    public void removeDepartment(Department department) {
        this.departments.remove(department);
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public double calculateTotalEmployeeSalary() {
        BigDecimal total = BigDecimal.valueOf(0);
        for (Employee employee : employees) {
            if (employee instanceof ShiftWorker) {
                ShiftWorker sw = (ShiftWorker) employee;
                total = total.add(BigDecimal.valueOf(sw.getCalculatedWeeklySalary()));
            } else if (employee instanceof OffShiftWorker) {
                OffShiftWorker osw = (OffShiftWorker) employee;
                total = total.add(BigDecimal.valueOf(osw.getWeeklySalary()));
            } else if (employee instanceof SalesPeople) {
                SalesPeople sp = (SalesPeople) employee;
                BigDecimal salesSalary = BigDecimal.valueOf(sp.getSalary());
                BigDecimal salesAmount = BigDecimal.valueOf(sp.getAmountOfSales());
                BigDecimal commission = salesAmount.multiply(BigDecimal.valueOf(sp.getCommissionPercentage()));
                total = total.add(salesSalary.add(commission));
            } else if (employee instanceof Manager) {
                Manager m = (Manager) employee;
                total = total.add(BigDecimal.valueOf(m.getSalary()));
            }
        }
        return total.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public double calculateTotalSalesPeopleCommission() {
        BigDecimal total = BigDecimal.valueOf(0);
        for (Employee employee : employees) {
            if (employee instanceof SalesPeople) {
                SalesPeople sp = (SalesPeople) employee;
                BigDecimal salesAmount = BigDecimal.valueOf(sp.getAmountOfSales());
                BigDecimal commissionPercentage = BigDecimal.valueOf(sp.getCommissionPercentage());
                total = total.add(salesAmount.multiply(commissionPercentage));
            }
        }
        return total.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public double calculateTotalShiftWorkerHolidayPremiums() {
        BigDecimal total = BigDecimal.valueOf(0);
        for (Employee employee : employees) {
            if (employee instanceof ShiftWorker) {
                ShiftWorker sw = (ShiftWorker) employee;
                total = total.add(BigDecimal.valueOf(sw.calculateHolidayPremium()));
            }
        }
        return total.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public int getManagerSubordinateCount(Manager manager) {
        return manager.getDirectSubordinateEmployeesCount();
    }
}
