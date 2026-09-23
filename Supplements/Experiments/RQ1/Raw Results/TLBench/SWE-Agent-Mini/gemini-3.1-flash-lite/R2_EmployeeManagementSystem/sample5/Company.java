import java.util.ArrayList;
import java.util.List;

public class Company {
    private String name;
    private List<Department> departments = new ArrayList<>();
    private List<Employee> employees = new ArrayList<>();

    public Company() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Department> getDepartments() { return departments; }
    public void setDepartments(List<Department> departments) { this.departments = departments; }
    public void addDepartment(Department department) { departments.add(department); }
    public void removeDepartment(Department department) { departments.remove(department); }
    public List<Employee> getEmployees() { return employees; }
    public void setEmployees(List<Employee> employees) { this.employees = employees; }

    public double calculateTotalEmployeeSalary() {
        double total = 0;
        for (Employee e : employees) {
            if (e instanceof Worker) {
                Worker w = (Worker) e;
                total += w.getWeeklyWorkingHour() * w.getHourlyRates();
                if (w instanceof ShiftWorker) {
                    total += ((ShiftWorker) w).calculateHolidayPremium();
                }
            } else if (e instanceof SalesPeople) {
                SalesPeople s = (SalesPeople) e;
                total += s.getSalary() + (s.getAmountOfSales() * s.getCommissionPercentage());
            } else if (e instanceof Manager) {
                total += ((Manager) e).getSalary();
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }

    public double calculateTotalSalesPeopleCommission() {
        double total = 0;
        for (Employee e : employees) {
            if (e instanceof SalesPeople) {
                total += ((SalesPeople) e).getTotalCommission();
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }

    public double calculateTotalShiftWorkerHolidayPremiums() {
        double total = 0;
        for (Employee e : employees) {
            if (e instanceof ShiftWorker) {
                total += ((ShiftWorker) e).calculateHolidayPremium();
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }
}
