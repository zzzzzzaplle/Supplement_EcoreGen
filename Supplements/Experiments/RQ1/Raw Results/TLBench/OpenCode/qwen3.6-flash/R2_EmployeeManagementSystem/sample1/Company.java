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
        departments.add(department);
    }

    public void removeDepartment(Department department) {
        departments.remove(department);
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public double calculateTotalEmployeeSalary() {
        BigDecimal totalSalary = BigDecimal.ZERO;
        for (Employee employee : employees) {
            if (employee instanceof Worker) {
                totalSalary = totalSalary.add(BigDecimal.valueOf(((Worker) employee).getSalary()));
                if (employee instanceof ShiftWorker) {
                    totalSalary = totalSalary.add(BigDecimal.valueOf(((ShiftWorker) employee).getHolidayPremium()));
                }
            } else if (employee instanceof SalesPeople) {
                SalesPeople sp = (SalesPeople) employee;
                BigDecimal salesSalary = BigDecimal.valueOf(sp.getSalary()).add(
                    BigDecimal.valueOf(sp.getAmountOfSales() * sp.getCommissionPercentage())
                );
                totalSalary = totalSalary.add(salesSalary);
            } else if (employee instanceof Manager) {
                totalSalary = totalSalary.add(BigDecimal.valueOf(((Manager) employee).getSalary()));
            }
        }
        return totalSalary.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public double calculateTotalSalesPeopleCommission() {
        BigDecimal totalCommission = BigDecimal.ZERO;
        for (Employee employee : employees) {
            if (employee instanceof SalesPeople) {
                SalesPeople sp = (SalesPeople) employee;
                totalCommission = totalCommission.add(BigDecimal.valueOf(sp.getTotalCommission()));
            }
        }
        return totalCommission.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public double calculateTotalShiftWorkerHolidayPremiums() {
        BigDecimal totalPremiums = BigDecimal.ZERO;
        for (Employee employee : employees) {
            if (employee instanceof ShiftWorker) {
                totalPremiums = totalPremiums.add(BigDecimal.valueOf(((ShiftWorker) employee).calculateHolidayPremium()));
            }
        }
        return totalPremiums.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
