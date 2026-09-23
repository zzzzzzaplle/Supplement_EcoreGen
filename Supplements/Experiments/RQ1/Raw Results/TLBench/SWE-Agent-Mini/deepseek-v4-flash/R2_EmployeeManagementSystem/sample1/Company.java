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
        double totalSalary = 0.0;
        if (employees == null) {
            return 0.0;
        }
        for (Employee emp : employees) {
            if (emp instanceof Worker) {
                Worker worker = (Worker) emp;
                double workerSalary = worker.getWeeklyWorkingHour() * worker.getHourlyRates();
                if (worker instanceof ShiftWorker) {
                    ShiftWorker shiftWorker = (ShiftWorker) worker;
                    workerSalary += shiftWorker.getHolidayPremium();
                }
                totalSalary += workerSalary;
            } else if (emp instanceof SalesPeople) {
                SalesPeople sales = (SalesPeople) emp;
                double salesSalary = sales.getSalary() + (sales.getAmountOfSales() * sales.getCommissionPercentage());
                totalSalary += salesSalary;
            } else if (emp instanceof Manager) {
                Manager manager = (Manager) emp;
                totalSalary += manager.getSalary();
            }
        }
        return Math.round(totalSalary * 100.0) / 100.0;
    }

    public double calculateTotalSalesPeopleCommission() {
        double totalCommission = 0.0;
        if (employees == null) {
            return 0.0;
        }
        for (Employee emp : employees) {
            if (emp instanceof SalesPeople) {
                SalesPeople sales = (SalesPeople) emp;
                totalCommission += sales.getAmountOfSales() * sales.getCommissionPercentage();
            }
        }
        return Math.round(totalCommission * 100.0) / 100.0;
    }

    public double calculateTotalShiftWorkerHolidayPremiums() {
        double totalPremiums = 0.0;
        if (employees == null) {
            return 0.0;
        }
        boolean hasShiftWorker = false;
        for (Employee emp : employees) {
            if (emp instanceof ShiftWorker) {
                ShiftWorker shiftWorker = (ShiftWorker) emp;
                totalPremiums += shiftWorker.getHolidayPremium();
                hasShiftWorker = true;
            }
        }
        if (!hasShiftWorker) {
            return 0.0;
        }
        return Math.round(totalPremiums * 100.0) / 100.0;
    }
}
