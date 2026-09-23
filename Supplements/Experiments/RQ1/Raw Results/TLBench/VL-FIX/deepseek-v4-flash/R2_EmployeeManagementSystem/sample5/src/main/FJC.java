import java.util.Date;
import java.util.List;
import java.util.ArrayList;

// Abstract base class for all employees
abstract class Employee {
    private String department;
    private String name;
    private Date birthDate;
    private String socialInsuranceNumber;

    // Unparameterized constructor
    public Employee() {
    }

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

// Abstract worker class extends Employee
abstract class Worker extends Employee {
    private int weeklyWorkingHour;
    private double hourlyRates;

    // Unparameterized constructor
    public Worker() {
    }

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

// Shift worker class extends Worker
class ShiftWorker extends Worker {
    private double holidayPremium;

    // Unparameterized constructor
    public ShiftWorker() {
    }

    // Getters and Setters
    public double getHolidayPremium() {
        return holidayPremium;
    }

    public void setHolidayPremium(double holidayPremium) {
        this.holidayPremium = holidayPremium;
    }

    // Override setDepartment to enforce DELIVERY only
    @Override
    public void setDepartment(String department) {
        if ("DELIVERY".equals(department)) {
            super.setDepartment(department);
        } else {
            throw new IllegalArgumentException("ShiftWorker can only belong to DELIVERY department");
        }
    }

    // Calculate holiday premium (returns the holidayPremium value)
    public double calculateHolidayPremium() {
        return holidayPremium;
    }
}

// Off-shift worker class extends Worker
class OffShiftWorker extends Worker {
    private boolean weekendPermit;
    private boolean officialHolidayPermit;

    // Unparameterized constructor
    public OffShiftWorker() {
    }

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

// SalesPeople class extends Employee
class SalesPeople extends Employee {
    private double salary;
    private double amountOfSales;
    private double commissionPercentage;

    // Unparameterized constructor
    public SalesPeople() {
    }

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

    // Calculate total commission: amountOfSales * commissionPercentage
    public double getTotalCommission() {
        return Math.round((amountOfSales * commissionPercentage) * 100.0) / 100.0;
    }
}

// Manager class extends Employee
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

    // Returns the number of direct subordinate employees
    public int getDirectSubordinateEmployeesCount() {
        return subordinates.size();
    }
}

// Enum for department types
enum DepartmentType {
    PRODUCTION,
    CONTROL,
    DELIVERY
}

// Department class
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

    // Calculate average weekly working hours of workers in this department
    // Returns 0 if no workers
    public double calculateAverageWorkerWorkingHours() {
        List<Worker> workers = new ArrayList<>();
        for (Employee emp : employees) {
            if (emp instanceof Worker) {
                workers.add((Worker) emp);
            }
        }
        if (workers.isEmpty()) {
            return 0.0;
        }
        double totalHours = 0;
        for (Worker w : workers) {
            totalHours += w.getWeeklyWorkingHour();
        }
        double average = totalHours / workers.size();
        return Math.round(average * 100.0) / 100.0;
    }
}

// Company class
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

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    // Add department
    public void addDepartment(Department department) {
        departments.add(department);
    }

    // Remove department
    public void removeDepartment(Department department) {
        departments.remove(department);
    }

    // Calculate total salary of all employees
    // Workers: weeklyWorkingHour * hourlyRates + holidayPremium (for shift workers)
    // SalesPeople: salary + amountOfSales * commissionPercentage
    // Managers: salary
    public double calculateTotalEmployeeSalary() {
        double total = 0.0;
        for (Employee emp : employees) {
            if (emp instanceof ShiftWorker) {
                ShiftWorker sw = (ShiftWorker) emp;
                total += sw.getWeeklyWorkingHour() * sw.getHourlyRates() + sw.calculateHolidayPremium();
            } else if (emp instanceof Worker) {
                Worker w = (Worker) emp;
                total += w.getWeeklyWorkingHour() * w.getHourlyRates();
            } else if (emp instanceof SalesPeople) {
                SalesPeople sp = (SalesPeople) emp;
                total += sp.getSalary() + sp.getAmountOfSales() * sp.getCommissionPercentage();
            } else if (emp instanceof Manager) {
                Manager m = (Manager) emp;
                total += m.getSalary();
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }

    // Calculate total commission amount for all salespeople
    public double calculateTotalSalesPeopleCommission() {
        double total = 0.0;
        for (Employee emp : employees) {
            if (emp instanceof SalesPeople) {
                SalesPeople sp = (SalesPeople) emp;
                total += sp.getAmountOfSales() * sp.getCommissionPercentage();
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }

    // Calculate total holiday premiums paid to all shift workers
    public double calculateTotalShiftWorkerHolidayPremiums() {
        double total = 0.0;
        for (Employee emp : employees) {
            if (emp instanceof ShiftWorker) {
                ShiftWorker sw = (ShiftWorker) emp;
                total += sw.calculateHolidayPremium();
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }
}