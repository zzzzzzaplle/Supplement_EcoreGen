public class Company {
    private String name;
    private java.util.List<Department> departments;
    private java.util.List<Employee> employees;

    public Company() {
        this.departments = new java.util.ArrayList<>();
        this.employees = new java.util.ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public java.util.List<Department> getDepartments() {
        return departments;
    }

    public void addDepartment(Department department) {
        departments.add(department);
    }

    public void removeDepartment(Department department) {
        departments.remove(department);
    }

    public java.util.List<Employee> getEmployees() {
        return employees;
    }

    public double calculateTotalEmployeeSalary() {
        double total = 0;
        for (Employee employee : employees) {
            if (employee instanceof Worker) {
                Worker worker = (Worker) employee;
                total += worker.getWeeklyWorkingHour() * worker.getHourlyRates();
                if (employee instanceof ShiftWorker) {
                    total += ((ShiftWorker) employee).calculateHolidayPremium();
                }
            } else if (employee instanceof SalesPeople) {
                SalesPeople salesPerson = (SalesPeople) employee;
                total += salesPerson.getSalary() + salesPerson.getAmountOfSales() * salesPerson.getCommissionPercentage();
            } else if (employee instanceof Manager) {
                Manager manager = (Manager) employee;
                total += manager.getSalary();
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }

    public double calculateTotalSalesPeopleCommission() {
        double total = 0;
        for (Employee employee : employees) {
            if (employee instanceof SalesPeople) {
                SalesPeople salesPerson = (SalesPeople) employee;
                total += salesPerson.getAmountOfSales() * salesPerson.getCommissionPercentage();
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }

    public double calculateTotalShiftWorkerHolidayPremiums() {
        double total = 0;
        for (Employee employee : employees) {
            if (employee instanceof ShiftWorker) {
                total += ((ShiftWorker) employee).calculateHolidayPremium();
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }
}
