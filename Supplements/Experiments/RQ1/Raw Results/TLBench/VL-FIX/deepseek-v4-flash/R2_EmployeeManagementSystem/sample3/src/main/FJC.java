import java.util.Date;
import java.util.List;
import java.util.ArrayList;

// ---------- Enum DepartmentType ----------
enum DepartmentType {
    PRODUCTION,
    CONTROL,
    DELIVERY
}

// ---------- Abstract class Employee ----------
abstract class Employee {
    private String department;
    private String name;
    private Date birthDate;
    private String socialInsuranceNumber;

    // Unparameterized constructor
    public Employee() {}

    // Getters and Setters
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getSocialInsuranceNumber() {
        return socialInsuranceNumber;
    }

    public void setSocialInsuranceNumber(String socialInsuranceNumber) {
        this.socialInsuranceNumber = socialInsuranceNumber;
    }
}

// ---------- Abstract class Worker ----------
abstract class Worker extends Employee {
    private int weeklyWorkingHour;
    private double hourlyRates;

    // Unparameterized constructor
    public Worker() {}

    // Getters and Setters
    public int getWeeklyWorkingHour() {
        return weeklyWorkingHour;
    }

    public void setWeeklyWorkingHour(int weeklyWorkingHour) {
        this.weeklyWorkingHour = weeklyWorkingHour;
    }

    public double getHourlyRates() {
        return hourlyRates;
    }

    public void setHourlyRates(double hourlyRates) {
        this.hourlyRates = hourlyRates;
    }
}

// ---------- ShiftWorker ----------
class ShiftWorker extends Worker {
    private double holidayPremium;

    // Unparameterized constructor
    public ShiftWorker() {}

    // Getters and Setters
    public double getHolidayPremium() {
        return holidayPremium;
    }

    public void setHolidayPremium(double holidayPremium) {
        this.holidayPremium = holidayPremium;
    }

    // Override setDepartment to enforce only DELIVERY department
    @Override
    public void setDepartment(String department) {
        if (!"DELIVERY".equalsIgnoreCase(department)) {
            throw new IllegalArgumentException("ShiftWorker can only belong to the Delivery department.");
        }
        super.setDepartment(department);
    }

    public double calculateHolidayPremium() {
        return holidayPremium;
    }
}

// ---------- OffShiftWorker ----------
class OffShiftWorker extends Worker {
    private boolean weekendPermit;
    private boolean officialHolidayPermit;

    // Unparameterized constructor
    public OffShiftWorker() {}

    // Getters and Setters
    public boolean isWeekendPermit() {
        return weekendPermit;
    }

    public void setWeekendPermit(boolean weekendPermit) {
        this.weekendPermit = weekendPermit;
    }

    public boolean isOfficialHolidayPermit() {
        return officialHolidayPermit;
    }

    public void setOfficialHolidayPermit(boolean officialHolidayPermit) {
        this.officialHolidayPermit = officialHolidayPermit;
    }
}

// ---------- SalesPeople ----------
class SalesPeople extends Employee {
    private double salary;
    private double amountOfSales;
    private double commissionPercentage;

    // Unparameterized constructor
    public SalesPeople() {}

    // Getters and Setters
    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public double getAmountOfSales() {
        return amountOfSales;
    }

    public void setAmountOfSales(double amountOfSales) {
        this.amountOfSales = amountOfSales;
    }

    public double getCommissionPercentage() {
        return commissionPercentage;
    }

    public void setCommissionPercentage(double commissionPercentage) {
        this.commissionPercentage = commissionPercentage;
    }

    // Commission for this salesperson
    public double getTotalCommission() {
        return amountOfSales * commissionPercentage;
    }
}

// ---------- Manager ----------
class Manager extends Employee {
    private double salary;
    private String position;
    private List<Employee> subordinates;

    // Unparameterized constructor
    public Manager() {
        this.subordinates = new ArrayList<>();
    }

    // Getters and Setters
    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public List<Employee> getSubordinates() {
        return subordinates;
    }

    public void setSubordinates(List<Employee> subordinates) {
        this.subordinates = subordinates;
    }

    // Number of direct subordinate employees
    public int getDirectSubordinateEmployeesCount() {
        return subordinates != null ? subordinates.size() : 0;
    }
}

// ---------- Department ----------
class Department {
    private DepartmentType type;
    private Manager manager;
    private List<Employee> employees;

    // Unparameterized constructor
    public Department() {
        this.employees = new ArrayList<>();
    }

    // Getters and Setters
    public DepartmentType getType() {
        return type;
    }

    public void setType(DepartmentType type) {
        this.type = type;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    // Calculate average weekly working hours of all workers in this department
    public double calculateAverageWorkerWorkingHours() {
        List<Employee> deptEmployees = getEmployees();
        double totalHours = 0;
        int workerCount = 0;
        for (Employee emp : deptEmployees) {
            if (emp instanceof Worker) {
                Worker worker = (Worker) emp;
                totalHours += worker.getWeeklyWorkingHour();
                workerCount++;
            }
        }
        if (workerCount == 0) {
            return 0.0;
        }
        double average = totalHours / workerCount;
        // Round to two decimal places
        return Math.round(average * 100.0) / 100.0;
    }
}

// ---------- Company ----------
class Company {
    private String name;
    private List<Department> departments;
    private List<Employee> employees;

    // Unparameterized constructor
    public Company() {
        this.departments = new ArrayList<>();
        this.employees = new ArrayList<>();
    }

    // Getters and Setters
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
        this.departments.add(department);
    }

    public void removeDepartment(Department department) {
        this.departments.remove(department);
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    // Calculate total salary of all employees
    public double calculateTotalEmployeeSalary() {
        double totalSalary = 0.0;
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
                SalesPeople sp = (SalesPeople) emp;
                totalSalary += sp.getSalary() + sp.getAmountOfSales() * sp.getCommissionPercentage();
            } else if (emp instanceof Manager) {
                Manager mgr = (Manager) emp;
                totalSalary += mgr.getSalary();
            }
        }
        // Round to two decimal places
        return Math.round(totalSalary * 100.0) / 100.0;
    }

    // Calculate total commission amount of all salespeople
    public double calculateTotalSalesPeopleCommission() {
        double totalCommission = 0.0;
        for (Employee emp : employees) {
            if (emp instanceof SalesPeople) {
                SalesPeople sp = (SalesPeople) emp;
                totalCommission += sp.getAmountOfSales() * sp.getCommissionPercentage();
            }
        }
        // Round to two decimal places
        return Math.round(totalCommission * 100.0) / 100.0;
    }

    // Calculate total holiday premiums paid to all shift workers
    public double calculateTotalShiftWorkerHolidayPremiums() {
        double totalPremiums = 0.0;
        boolean hasShiftWorker = false;
        for (Employee emp : employees) {
            if (emp instanceof ShiftWorker) {
                ShiftWorker sw = (ShiftWorker) emp;
                totalPremiums += sw.getHolidayPremium();
                hasShiftWorker = true;
            }
        }
        if (!hasShiftWorker) {
            return 0.0;
        }
        // Round to two decimal places
        return Math.round(totalPremiums * 100.0) / 100.0;
    }
}