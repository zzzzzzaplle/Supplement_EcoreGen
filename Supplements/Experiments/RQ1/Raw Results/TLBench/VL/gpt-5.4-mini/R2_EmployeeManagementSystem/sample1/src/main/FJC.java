import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Enumeration of department types.
 */
enum DepartmentType {
    PRODUCTION,
    CONTROL,
    DELIVERY
}

/**
 * Abstract base class for all employees.
 */
abstract class Employee {
    private String department;
    private String name;
    private Date birthDate;
    private String socialInsuranceNumber;

    /**
     * Unparameterized constructor.
     */
    public Employee() {
        this.department = null;
        this.name = null;
        this.birthDate = null;
        this.socialInsuranceNumber = null;
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
        return birthDate == null ? null : new Date(birthDate.getTime());
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate == null ? null : new Date(birthDate.getTime());
    }

    public String getSocialInsuranceNumber() {
        return socialInsuranceNumber;
    }

    public void setSocialInsuranceNumber(String socialInsuranceNumber) {
        this.socialInsuranceNumber = socialInsuranceNumber;
    }
}

/**
 * Abstract base class for employees who are paid by hourly work.
 */
abstract class Worker extends Employee {
    private int weeklyWorkingHour;
    private double hourlyRates;

    /**
     * Unparameterized constructor.
     */
    public Worker() {
        super();
        this.weeklyWorkingHour = 0;
        this.hourlyRates = 0.0;
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

    /**
     * Calculates the worker salary, excluding any special premiums from subclasses.
     *
     * @return salary based on hours and hourly rate
     */
    public double calculateBaseSalary() {
        return weeklyWorkingHour * hourlyRates;
    }
}

/**
 * Worker that is allowed to work off-shift.
 */
class OffShiftWorker extends Worker {
    private boolean weekendPermit;
    private boolean officialHolidayPermit;

    /**
     * Unparameterized constructor.
     */
    public OffShiftWorker() {
        super();
        this.weekendPermit = false;
        this.officialHolidayPermit = false;
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
 * Worker that can receive a holiday premium and can only belong to Delivery department.
 */
class ShiftWorker extends Worker {
    private double holidayPremium;

    /**
     * Unparameterized constructor.
     */
    public ShiftWorker() {
        super();
        this.holidayPremium = 0.0;
    }

    public double getHolidayPremium() {
        return holidayPremium;
    }

    public void setHolidayPremium(double holidayPremium) {
        this.holidayPremium = holidayPremium;
    }

    @Override
    public void setDepartment(String department) {
        if (department != null && !Objects.equals("DELIVERY", department)) {
            throw new IllegalArgumentException("ShiftWorker can only belong to the Delivery department.");
        }
        super.setDepartment(department);
    }

    /**
     * Returns the holiday premium amount.
     *
     * @return holiday premium
     */
    public double calculateHolidayPremium() {
        return holidayPremium;
    }
}

/**
 * Employee with sales-based commission.
 */
class SalesPeople extends Employee {
    private double salary;
    private double amountOfSales;
    private double commissionPercentage;

    /**
     * Unparameterized constructor.
     */
    public SalesPeople() {
        super();
        this.salary = 0.0;
        this.amountOfSales = 0.0;
        this.commissionPercentage = 0.0;
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
 * Manager employee with subordinates.
 */
class Manager extends Employee {
    private double salary;
    private String position;
    private List<Employee> subordinates;

    /**
     * Unparameterized constructor.
     */
    public Manager() {
        super();
        this.salary = 0.0;
        this.position = null;
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
        return subordinates;
    }

    public void setSubordinates(List<Employee> subordinates) {
        this.subordinates = subordinates == null ? new ArrayList<>() : subordinates;
    }

    /**
     * Returns the number of direct subordinate employees.
     *
     * @return direct subordinate count
     */
    public int getDirectSubordinateEmployeesCount() {
        return subordinates == null ? 0 : subordinates.size();
    }
}

/**
 * Department with a manager and employees.
 */
class Department {
    private DepartmentType type;
    private Manager manager;
    private List<Employee> employees;

    /**
     * Unparameterized constructor.
     */
    public Department() {
        this.type = null;
        this.manager = null;
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
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees == null ? new ArrayList<>() : employees;
    }

    /**
     * Calculates the average weekly working hours of workers in this department.
     * Returns 0 when there are no workers.
     *
     * @return average weekly working hours rounded to two decimals
     */
    public double calculateAverageWorkerWorkingHours() {
        if (employees == null || employees.isEmpty()) {
            return 0.0;
        }
        double totalHours = 0.0;
        int workerCount = 0;
        for (Employee employee : employees) {
            if (employee instanceof Worker) {
                totalHours += ((Worker) employee).getWeeklyWorkingHour();
                workerCount++;
            }
        }
        if (workerCount == 0) {
            return 0.0;
        }
        return roundToTwoDecimals(totalHours / workerCount);
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
     * Unparameterized constructor.
     */
    public Company() {
        this.name = null;
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
        return departments;
    }

    public void addDepartment(Department department) {
        if (department != null) {
            departments.add(department);
        }
    }

    public void removeDepartment(Department department) {
        departments.remove(department);
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees == null ? new ArrayList<>() : employees;
    }

    /**
     * Calculates the total salary of all employees in the company.
     * Workers' salary = weeklyWorkingHour * hourlyRates + holidayPremium for shift workers.
     * Salespeople's salary = salary + amountOfSales * commissionPercentage.
     * Managers' salary = salary.
     *
     * @return total salary rounded to two decimals
     */
    public double calculateTotalEmployeeSalary() {
        double total = 0.0;
        if (employees != null) {
            for (Employee employee : employees) {
                if (employee instanceof ShiftWorker) {
                    ShiftWorker sw = (ShiftWorker) employee;
                    total += sw.calculateBaseSalary() + sw.getHolidayPremium();
                } else if (employee instanceof Worker) {
                    total += ((Worker) employee).calculateBaseSalary();
                } else if (employee instanceof SalesPeople) {
                    SalesPeople sp = (SalesPeople) employee;
                    total += sp.getSalary() + sp.getTotalCommission();
                } else if (employee instanceof Manager) {
                    total += ((Manager) employee).getSalary();
                }
            }
        }
        return roundToTwoDecimals(total);
    }

    /**
     * Calculates the total commission amount for all salespeople in the company.
     *
     * @return total commission rounded to two decimals
     */
    public double calculateTotalSalesPeopleCommission() {
        double total = 0.0;
        if (employees != null) {
            for (Employee employee : employees) {
                if (employee instanceof SalesPeople) {
                    total += ((SalesPeople) employee).getTotalCommission();
                }
            }
        }
        return roundToTwoDecimals(total);
    }

    /**
     * Calculates the total holiday premiums paid to all shift workers in the company.
     *
     * @return total holiday premiums rounded to two decimals
     */
    public double calculateTotalShiftWorkerHolidayPremiums() {
        double total = 0.0;
        if (employees != null) {
            for (Employee employee : employees) {
                if (employee instanceof ShiftWorker) {
                    total += ((ShiftWorker) employee).getHolidayPremium();
                }
            }
        }
        return roundToTwoDecimals(total);
    }

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}