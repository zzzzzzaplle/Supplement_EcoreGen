import java.math.BigDecimal;
import java.math.RoundingMode;
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
        double total = 0;
        if (employees != null) {
            for (Employee emp : employees) {
                if (emp instanceof Manager) {
                    total += ((Manager) emp).getSalary();
                } else if (emp instanceof SalesPeople) {
                    SalesPeople sp = (SalesPeople) emp;
                    total += sp.getSalary() + sp.getAmountOfSales() * sp.getCommissionPercentage();
                } else if (emp instanceof Worker) {
                    Worker w = (Worker) emp;
                    total += w.getWeeklyWorkingHour() * w.getHourlyRates();
                    if (emp instanceof ShiftWorker) {
                        total += ((ShiftWorker) emp).getHolidayPremium();
                    }
                }
            }
        }
        BigDecimal bd = new BigDecimal(total);
        return bd.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public double calculateTotalSalesPeopleCommission() {
        double total = 0;
        if (employees != null) {
            for (Employee emp : employees) {
                if (emp instanceof SalesPeople) {
                    SalesPeople sp = (SalesPeople) emp;
                    total += sp.getAmountOfSales() * sp.getCommissionPercentage();
                }
            }
        }
        BigDecimal bd = new BigDecimal(total);
        return bd.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public double calculateTotalShiftWorkerHolidayPremiums() {
        double total = 0;
        if (employees != null) {
            for (Employee emp : employees) {
                if (emp instanceof ShiftWorker) {
                    total += ((ShiftWorker) emp).getHolidayPremium();
                }
            }
        }
        BigDecimal bd = new BigDecimal(total);
        return bd.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
