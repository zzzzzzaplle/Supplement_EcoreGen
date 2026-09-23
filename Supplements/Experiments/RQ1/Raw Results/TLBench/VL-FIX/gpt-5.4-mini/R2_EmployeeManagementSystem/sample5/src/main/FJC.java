import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Enumeration of supported department types.
 */
enum DepartmentType {
    PRODUCTION,
    CONTROL,
    DELIVERY
}

/**
 * Base abstract class for all employees.
 */
abstract class Employee {
    private String department;
    private String name;
    private Date birthDate;
    private String socialInsuranceNumber;

    /**
     * Creates an empty employee instance.
     */
    public Employee() {
    }

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

/**
 * Abstract base class for workers.
 */
abstract class Worker extends Employee {
    private int weeklyWorkingHour;
    private double hourlyRates;

    /**
     * Creates an empty worker instance.
     */
    public Worker() {
    }

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

/**
 * Worker who can work shifts and receive a holiday premium.
 */
class ShiftWorker extends Worker {
    private double holidayPremium;

    /**
     * Creates an empty shift worker instance.
     */
    public ShiftWorker() {
    }

    public double getHolidayPremium() {
        return holidayPremium;
    }

    public void setHolidayPremium(double holidayPremium) {
        this.holidayPremium = holidayPremium;
    }

    @Override
    public void setDepartment(String department) {
        if (department != null && !"DELIVERY".equalsIgnoreCase(department)) {
            throw new IllegalArgumentException("ShiftWorker can only belong to the Delivery department.");
        }
        super.setDepartment(department);
    }

    /**
     * Returns the holiday premium paid to this shift worker.
     *
     * @return holiday premium
     */
    public double calculateHolidayPremium() {
        return holidayPremium;
    }
}

/**
 * Worker without shift privileges.
 */
class OffShiftWorker extends Worker {
    private boolean weekendPermit;
    private boolean officialHolidayPermit;

    /**
     * Creates an empty off-shift worker instance.
     */
    public OffShiftWorker() {
    }

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

/**
 * Sales person with salary and commission details.
 */
class SalesPeople extends Employee {
    private double salary;
    private double amountOfSales;
    private double commissionPercentage;

    /**
     * Creates an empty sales people instance.
     */
    public SalesPeople() {
    }

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

    /**
     * Calculates the commission amount for this salesperson.
     *
     * @return total commission
     */
    public double getTotalCommission() {
        return amountOfSales * commissionPercentage;
    }
}

/**
 * Manager with direct subordinates.
 */
class Manager extends Employee {
    private double salary;
    private String position;
    private List<Employee> subordinates;

    /**
     * Creates an empty manager instance.
     */
    public Manager() {
        this.subordinates = new ArrayList<>();
    }

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
        if (subordinates == null) {
            subordinates = new ArrayList<>();
        }
        return subordinates;
    }

    public void setSubordinates(List<Employee> subordinates) {
        this.subordinates = subordinates;
    }

    /**
     * Returns the number of direct subordinate employees.
     *
     * @return count of subordinates
     */
    public int getDirectSubordinateEmployeesCount() {
        return getSubordinates() == null ? 0 : getSubordinates().size();
    }
}

/**
 * Department containing employees and one manager.
 */
class Department {
    private DepartmentType type;
    private Manager manager;
    private List<Employee> employees;

    /**
     * Creates an empty department instance.
     */
    public Department() {
        this.employees = new ArrayList<>();
    }

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
        if (employees == null) {
            employees = new ArrayList<>();
        }
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    /**
     * Calculates the average weekly working hours of all workers in this department.
     * Returns 0 if there are no workers.
     *
     * @return average weekly working hours rounded to two decimals
     */
    public double calculateAverageWorkerWorkingHours() {
        double totalHours = 0.0;
        int count = 0;
        for (Employee employee : getEmployees()) {
            if (employee instanceof Worker) {
                totalHours += ((Worker) employee).getWeeklyWorkingHour();
                count++;
            }
        }
        if (count == 0) {
            return 0.0;
        }
        return roundToTwoDecimals(totalHours / count);
    }

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}

/**
 * Company containing departments and employees.
 */
class Company {
    private String name;
    private List<Department> departments;
    private List<Employee> employees;

    /**
     * Creates an empty company instance.
     */
    public Company() {
        this.departments = new ArrayList<>();
        this.employees = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Department> getDepartments() {
        if (departments == null) {
            departments = new ArrayList<>();
        }
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public void addDepartment(Department department) {
        getDepartments().add(department);
    }

    public void removeDepartment(Department department) {
        getDepartments().remove(department);
    }

    public List<Employee> getEmployees() {
        if (employees == null) {
            employees = new ArrayList<>();
        }
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    /**
     * Calculates the total salary of all employees in the company.
     * Monetary result is rounded to two decimals.
     *
     * @return total employee salary
     */
    public double calculateTotalEmployeeSalary() {
        double total = 0.0;
        for (Employee employee : getEmployees()) {
            if (employee instanceof ShiftWorker) {
                ShiftWorker worker = (ShiftWorker) employee;
                total += worker.getWeeklyWorkingHour() * worker.getHourlyRates() + worker.getHolidayPremium();
            } else if (employee instanceof OffShiftWorker) {
                OffShiftWorker worker = (OffShiftWorker) employee;
                total += worker.getWeeklyWorkingHour() * worker.getHourlyRates();
            } else if (employee instanceof SalesPeople) {
                SalesPeople salesperson = (SalesPeople) employee;
                total += salesperson.getSalary() + salesperson.getTotalCommission();
            } else if (employee instanceof Manager) {
                total += ((Manager) employee).getSalary();
            }
        }
        return roundToTwoDecimals(total);
    }

    /**
     * Calculates the total commission amount for all salespeople in the company.
     * Monetary result is rounded to two decimals.
     *
     * @return total salespeople commission
     */
    public double calculateTotalSalesPeopleCommission() {
        double total = 0.0;
        for (Employee employee : getEmployees()) {
            if (employee instanceof SalesPeople) {
                total += ((SalesPeople) employee).getTotalCommission();
            }
        }
        return roundToTwoDecimals(total);
    }

    /**
     * Calculates the total holiday premiums paid to all shift workers in the company.
     * Monetary result is rounded to two decimals.
     *
     * @return total shift worker holiday premiums
     */
    public double calculateTotalShiftWorkerHolidayPremiums() {
        double total = 0.0;
        for (Employee employee : getEmployees()) {
            if (employee instanceof ShiftWorker) {
                total += ((ShiftWorker) employee).getHolidayPremium();
            }
        }
        return roundToTwoDecimals(total);
    }

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}