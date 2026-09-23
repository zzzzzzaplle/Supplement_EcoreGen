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
                if (employee instanceof Worker) {
                    Worker worker = (Worker) employee;
                    total += worker.getWeeklyWorkingHour() * worker.getHourlyRates();
                    if (employee instanceof ShiftWorker) {
                        total += ((ShiftWorker) employee).calculateHolidayPremium();
                    }
                } else if (employee instanceof SalesPeople) {
                    SalesPeople salesPeople = (SalesPeople) employee;
                    total += salesPeople.getSalary() + salesPeople.getTotalCommission();
                } else if (employee instanceof Manager) {
                    total += ((Manager) employee).getSalary();
                }
            }
        }
        return roundTwoDecimals(total);
    }

    public double calculateTotalSalesPeopleCommission() {
        double total = 0.0;
        if (employees != null) {
            for (Employee employee : employees) {
                if (employee instanceof SalesPeople) {
                    total += ((SalesPeople) employee).getTotalCommission();
                }
            }
        }
        return roundTwoDecimals(total);
    }

    public double calculateTotalShiftWorkerHolidayPremiums() {
        double total = 0.0;
        if (employees != null) {
            for (Employee employee : employees) {
                if (employee instanceof ShiftWorker) {
                    total += ((ShiftWorker) employee).calculateHolidayPremium();
                }
            }
        }
        return roundTwoDecimals(total);
    }

    private double roundTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
