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
 * Base abstract employee class.
 */
abstract class Employee {
    private String department;
    private String name;
    private Date birthDate;
    private String socialInsuranceNumber;

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
 * Abstract worker class.
 */
abstract class Worker extends Employee {
    private int weeklyWorkingHour;
    private double hourlyRates;

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

    public double calculateBaseSalary() {
        return weeklyWorkingHour * hourlyRates;
    }
}

/**
 * Off-shift worker with permit attributes.
 */
class OffShiftWorker extends Worker {
    private boolean weekendPermit;
    private boolean officialHolidayPermit;

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
 * Shift worker with holiday premium. Must belong to Delivery department.
 */
class ShiftWorker extends Worker {
    private double holidayPremium;

    public ShiftWorker() {
        super();
        this.holidayPremium = 0.0;
        super.setDepartment(DepartmentType.DELIVERY.name());
    }

    public double getHolidayPremium() {
        return holidayPremium;
    }

    public void setHolidayPremium(double holidayPremium) {
        this.holidayPremium = holidayPremium;
    }

    @Override
    public void setDepartment(String department) {
        if (department != null && !DepartmentType.DELIVERY.name().equals(department)) {
            throw new IllegalArgumentException("Shift workers can only belong to the Delivery department.");
        }
        super.setDepartment(department);
    }

    public double calculateHolidayPremium() {
        return holidayPremium;
    }

    public double calculateSalary() {
        return getWeeklyWorkingHour() * getHourlyRates() + holidayPremium;
    }
}

/**
 * Salespeople employee class.
 */
class SalesPeople extends Employee {
    private double salary;
    private double amountOfSales;
    private double commissionPercentage;

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

    public double getTotalCommission() {
        return amountOfSales * commissionPercentage;
    }

    public double calculateSalary() {
        return salary + getTotalCommission();
    }
}

/**
 * Manager employee class.
 */
class Manager extends Employee {
    private double salary;
    private String position;
    private List<Employee> subordinates;

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
        this.subordinates = (subordinates == null) ? new ArrayList<>() : subordinates;
    }

    public int getDirectSubordinateEmployeesCount() {
        return subordinates == null ? 0 : subordinates.size();
    }

    public double calculateSalary() {
        return salary;
    }
}

/**
 * Department class containing manager and employees.
 */
class Department {
    private DepartmentType type;
    private Manager manager;
    private List<Employee> employees;

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
        this.employees = (employees == null) ? new ArrayList<>() : employees;
    }

    public double calculateAverageWorkerWorkingHours() {
        int count = 0;
        double total = 0.0;
        if (employees != null) {
            for (Employee employee : employees) {
                if (employee instanceof Worker) {
                    total += ((Worker) employee).getWeeklyWorkingHour();
                    count++;
                }
            }
        }
        if (count == 0) {
            return 0.0;
        }
        return roundToTwoDecimals(total / count);
    }

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}

/**
 * Company class containing departments and employees.
 */
class Company {
    private String name;
    private List<Department> departments;
    private List<Employee> employees;

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

    public void setDepartments(List<Department> departments) {
        this.departments = (departments == null) ? new ArrayList<>() : departments;
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
        this.employees = (employees == null) ? new ArrayList<>() : employees;
    }

    public double calculateTotalEmployeeSalary() {
        double total = 0.0;
        if (employees != null) {
            for (Employee employee : employees) {
                total += calculateEmployeeSalary(employee);
            }
        }
        return roundToTwoDecimals(total);
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
        return roundToTwoDecimals(total);
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
        return roundToTwoDecimals(total);
    }

    private double calculateEmployeeSalary(Employee employee) {
        if (employee instanceof ShiftWorker) {
            ShiftWorker sw = (ShiftWorker) employee;
            return sw.getWeeklyWorkingHour() * sw.getHourlyRates() + sw.getHolidayPremium();
        } else if (employee instanceof SalesPeople) {
            SalesPeople sp = (SalesPeople) employee;
            return sp.getSalary() + sp.getAmountOfSales() * sp.getCommissionPercentage();
        } else if (employee instanceof Manager) {
            return ((Manager) employee).getSalary();
        } else if (employee instanceof Worker) {
            Worker worker = (Worker) employee;
            return worker.getWeeklyWorkingHour() * worker.getHourlyRates();
        }
        return 0.0;
    }

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}