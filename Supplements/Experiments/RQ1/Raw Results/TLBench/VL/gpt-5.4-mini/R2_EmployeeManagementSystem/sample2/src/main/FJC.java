import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

enum DepartmentType {
    PRODUCTION,
    CONTROL,
    DELIVERY
}

abstract class Employee {
    private String department;
    private String name;
    private Date birthDate;
    private String socialInsuranceNumber;

    public Employee() {
    }

    public Employee(String department, String name, Date birthDate, String socialInsuranceNumber) {
        this.department = department;
        this.name = name;
        this.birthDate = birthDate;
        this.socialInsuranceNumber = socialInsuranceNumber;
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

abstract class Worker extends Employee {
    private int weeklyWorkingHour;
    private double hourlyRates;

    public Worker() {
        super();
    }

    public Worker(String department, String name, Date birthDate, String socialInsuranceNumber,
                  int weeklyWorkingHour, double hourlyRates) {
        super(department, name, birthDate, socialInsuranceNumber);
        this.weeklyWorkingHour = weeklyWorkingHour;
        this.hourlyRates = hourlyRates;
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

class OffShiftWorker extends Worker {
    private boolean weekendPermit;
    private boolean officialHolidayPermit;

    public OffShiftWorker() {
        super();
    }

    public OffShiftWorker(String department, String name, Date birthDate, String socialInsuranceNumber,
                          int weeklyWorkingHour, double hourlyRates,
                          boolean weekendPermit, boolean officialHolidayPermit) {
        super(department, name, birthDate, socialInsuranceNumber, weeklyWorkingHour, hourlyRates);
        this.weekendPermit = weekendPermit;
        this.officialHolidayPermit = officialHolidayPermit;
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

class ShiftWorker extends Worker {
    private double holidayPremium;

    public ShiftWorker() {
        super();
    }

    public ShiftWorker(String department, String name, Date birthDate, String socialInsuranceNumber,
                       int weeklyWorkingHour, double hourlyRates, double holidayPremium) {
        super(department, name, birthDate, socialInsuranceNumber, weeklyWorkingHour, hourlyRates);
        this.holidayPremium = holidayPremium;
    }

    public double getHolidayPremium() {
        return holidayPremium;
    }

    public void setHolidayPremium(double holidayPremium) {
        this.holidayPremium = holidayPremium;
    }

    @Override
    public void setDepartment(String department) {
        if (department != null && !DepartmentType.DELIVERY.name().equalsIgnoreCase(department)) {
            throw new IllegalArgumentException("ShiftWorker can only belong to the Delivery department.");
        }
        super.setDepartment(department);
    }

    public double calculateHolidayPremium() {
        return holidayPremium;
    }
}

class SalesPeople extends Employee {
    private double salary;
    private double amountOfSales;
    private double commissionPercentage;

    public SalesPeople() {
        super();
    }

    public SalesPeople(String department, String name, Date birthDate, String socialInsuranceNumber,
                       double salary, double amountOfSales, double commissionPercentage) {
        super(department, name, birthDate, socialInsuranceNumber);
        this.salary = salary;
        this.amountOfSales = amountOfSales;
        this.commissionPercentage = commissionPercentage;
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
}

class Manager extends Employee {
    private double salary;
    private String position;
    private List<Employee> subordinates;

    public Manager() {
        super();
        this.subordinates = new ArrayList<>();
    }

    public Manager(String department, String name, Date birthDate, String socialInsuranceNumber,
                   double salary, String position, List<Employee> subordinates) {
        super(department, name, birthDate, socialInsuranceNumber);
        this.salary = salary;
        this.position = position;
        this.subordinates = (subordinates == null) ? new ArrayList<>() : subordinates;
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

    public int getDirectSubordinateEmployeesCount() {
        return getSubordinates().size();
    }
}

class Department {
    private DepartmentType type;
    private Manager manager;
    private List<Employee> employees;

    public Department() {
        this.employees = new ArrayList<>();
    }

    public Department(DepartmentType type, Manager manager, List<Employee> employees) {
        this.type = type;
        this.manager = manager;
        this.employees = (employees == null) ? new ArrayList<>() : employees;
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

    public double calculateAverageWorkerWorkingHours() {
        int totalHours = 0;
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
        return roundTwoDecimals((double) totalHours / count);
    }

    private double roundTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}

class Company {
    private String name;
    private List<Department> departments;
    private List<Employee> employees;

    public Company() {
        this.departments = new ArrayList<>();
        this.employees = new ArrayList<>();
    }

    public Company(String name, List<Department> departments, List<Employee> employees) {
        this.name = name;
        this.departments = (departments == null) ? new ArrayList<>() : departments;
        this.employees = (employees == null) ? new ArrayList<>() : employees;
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

    public double calculateTotalEmployeeSalary() {
        double total = 0.0;
        for (Employee employee : getEmployees()) {
            if (employee instanceof ShiftWorker) {
                ShiftWorker worker = (ShiftWorker) employee;
                total += worker.getWeeklyWorkingHour() * worker.getHourlyRates() + worker.getHolidayPremium();
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
        return roundTwoDecimals(total);
    }

    public double calculateTotalSalesPeopleCommission() {
        double total = 0.0;
        for (Employee employee : getEmployees()) {
            if (employee instanceof SalesPeople) {
                total += ((SalesPeople) employee).getTotalCommission();
            }
        }
        return roundTwoDecimals(total);
    }

    public double calculateTotalShiftWorkerHolidayPremiums() {
        double total = 0.0;
        for (Employee employee : getEmployees()) {
            if (employee instanceof ShiftWorker) {
                total += ((ShiftWorker) employee).getHolidayPremium();
            }
        }
        return roundTwoDecimals(total);
    }

    private double roundTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}