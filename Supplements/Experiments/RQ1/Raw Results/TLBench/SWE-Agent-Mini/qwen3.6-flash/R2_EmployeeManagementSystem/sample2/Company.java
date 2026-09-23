import java.util.List;

class Company {
    private String name;
    private List<Department> departments;
    private List<Employee> employees;

    public Company() {
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
        if (this.departments == null) {
            this.departments = new java.util.ArrayList<>();
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

    public double calculateTotalEmployeeSalary() {
        if (employees == null || employees.isEmpty()) {
            return 0;
        }
        double total = 0;
        for (Employee e : employees) {
            if (e instanceof Worker) {
                Worker w = (Worker) e;
                double workerSalary = w.getWeeklyWorkingHour() * w.getHourlyRates();
                if (e instanceof ShiftWorker) {
                    ShiftWorker sw = (ShiftWorker) e;
                    workerSalary += sw.getHolidayPremium();
                }
                total += workerSalary;
            } else if (e instanceof SalesPeople) {
                SalesPeople sp = (SalesPeople) e;
                total += sp.getSalary() + sp.getAmountOfSales() * sp.getCommissionPercentage();
            } else if (e instanceof Manager) {
                Manager m = (Manager) e;
                total += m.getSalary();
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }

    public double calculateTotalSalesPeopleCommission() {
        if (employees == null || employees.isEmpty()) {
            return 0;
        }
        double total = 0;
        for (Employee e : employees) {
            if (e instanceof SalesPeople) {
                SalesPeople sp = (SalesPeople) e;
                total += sp.getAmountOfSales() * sp.getCommissionPercentage();
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }

    public double calculateTotalShiftWorkerHolidayPremiums() {
        if (employees == null || employees.isEmpty()) {
            return 0;
        }
        double total = 0;
        for (Employee e : employees) {
            if (e instanceof ShiftWorker) {
                ShiftWorker sw = (ShiftWorker) e;
                total += sw.calculateHolidayPremium();
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }
}
