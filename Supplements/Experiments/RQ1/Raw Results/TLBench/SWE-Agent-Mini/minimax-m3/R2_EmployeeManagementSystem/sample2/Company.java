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

    public void addDepartment(Department department) {
        if (departments == null) {
            departments = new ArrayList<Department>();
        }
        if (!departments.contains(department)) {
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

    public double calculateTotalEmployeeSalary() {
        double total = 0.0;
        if (employees != null) {
            for (Employee emp : employees) {
                if (emp instanceof Worker) {
                    Worker w = (Worker) emp;
                    double workerSalary = w.getWeeklyWorkingHour() * w.getHourlyRates();
                    if (w instanceof ShiftWorker) {
                        ShiftWorker sw = (ShiftWorker) w;
                        workerSalary += sw.getHolidayPremium();
                    }
                    total += workerSalary;
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
        return Math.round(total * 100.0) / 100.0;
    }
}
