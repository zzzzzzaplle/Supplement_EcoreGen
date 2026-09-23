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
        departments.add(department);
    }

    public void removeDepartment(Department department) {
        if (departments != null) {
            departments.remove(department);
        }
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
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
            for (Employee employee : employees) {
                if (employee instanceof ShiftWorker) {
                    ShiftWorker worker = (ShiftWorker) employee;
                    total += worker.getWeeklyWorkingHour() * worker.getHourlyRates() + worker.calculateHolidayPremium();
                } else if (employee instanceof Worker) {
                    Worker worker = (Worker) employee;
                    total += worker.getWeeklyWorkingHour() * worker.getHourlyRates();
                } else if (employee instanceof SalesPeople) {
                    SalesPeople salesPeople = (SalesPeople) employee;
                    total += salesPeople.getSalary() + salesPeople.getAmountOfSales() * salesPeople.getCommissionPercentage();
                } else if (employee instanceof Manager) {
                    total += ((Manager) employee).getSalary();
                }
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }

    public double calculateTotalSalesPeopleCommission() {
        double total = 0.0;
        if (employees != null) {
            for (Employee employee : employees) {
                if (employee instanceof SalesPeople) {
                    SalesPeople salesPeople = (SalesPeople) employee;
                    total += salesPeople.getAmountOfSales() * salesPeople.getCommissionPercentage();
                }
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }

    public double calculateTotalShiftWorkerHolidayPremiums() {
        double total = 0.0;
        if (employees != null) {
            for (Employee employee : employees) {
                if (employee instanceof ShiftWorker) {
                    total += ((ShiftWorker) employee).getHolidayPremium();
                }
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }
}
