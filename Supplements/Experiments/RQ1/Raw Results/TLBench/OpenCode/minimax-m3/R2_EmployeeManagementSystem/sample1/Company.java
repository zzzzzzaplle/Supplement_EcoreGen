public class Company {
    private String name;
    private java.util.List<Department> departments;
    private java.util.List<Employee> employees;

    public Company() {
        this.departments = new java.util.ArrayList<Department>();
        this.employees = new java.util.ArrayList<Employee>();
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

    public void setDepartments(java.util.List<Department> departments) {
        this.departments = departments;
    }

    public void addDepartment(Department department) {
        if (this.departments == null) {
            this.departments = new java.util.ArrayList<Department>();
        }
        this.departments.add(department);
    }

    public void removeDepartment(Department department) {
        if (this.departments != null) {
            this.departments.remove(department);
        }
    }

    public java.util.List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(java.util.List<Employee> employees) {
        this.employees = employees;
    }

    public double calculateTotalEmployeeSalary() {
        double total = 0.0;
        if (employees != null) {
            for (Employee e : employees) {
                if (e instanceof Worker) {
                    Worker w = (Worker) e;
                    total += w.getWeeklyWorkingHour() * w.getHourlyRates();
                    if (w instanceof ShiftWorker) {
                        total += ((ShiftWorker) w).getHolidayPremium();
                    }
                } else if (e instanceof SalesPeople) {
                    SalesPeople s = (SalesPeople) e;
                    total += s.getSalary() + s.getAmountOfSales() * s.getCommissionPercentage();
                } else if (e instanceof Manager) {
                    total += ((Manager) e).getSalary();
                }
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }

    public double calculateTotalSalesPeopleCommission() {
        double total = 0.0;
        if (employees != null) {
            for (Employee e : employees) {
                if (e instanceof SalesPeople) {
                    SalesPeople s = (SalesPeople) e;
                    total += s.getAmountOfSales() * s.getCommissionPercentage();
                }
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }

    public double calculateTotalShiftWorkerHolidayPremiums() {
        double total = 0.0;
        if (employees != null) {
            for (Employee e : employees) {
                if (e instanceof ShiftWorker) {
                    total += ((ShiftWorker) e).getHolidayPremium();
                }
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }
}
