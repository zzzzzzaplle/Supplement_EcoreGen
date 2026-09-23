import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
        this.department = "";
        this.name = "";
        this.birthDate = null;
        this.socialInsuranceNumber = "";
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

class Manager extends Employee {
    private double salary;
    private String position;
    private List<Employee> subordinates;

    public Manager() {
        super();
        this.salary = 0.0;
        this.position = "";
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

    public int getDirectSubordinateEmployeesCount() {
        return subordinates.size();
    }
}

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
}

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
}

class ShiftWorker extends Worker {
    private double holidayPremium;

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
        if (!"Delivery".equals(department)) {
            throw new IllegalArgumentException("Shift workers can only belong to the Delivery department.");
        }
        super.setDepartment(department);
    }

    public double calculateHolidayPremium() {
        return holidayPremium;
    }
}

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

    public double calculateAverageWorkerWorkingHours() {
        List<Worker> workers = employees.stream()
                .filter(e -> e instanceof Worker)
                .map(e -> (Worker) e)
                .collect(Collectors.toList());
        
        if (workers.isEmpty()) {
            return 0.0;
        }
        
        double totalHours = workers.stream()
                .mapToDouble(Worker::getWeeklyWorkingHour)
                .sum();
        
        return Math.round((totalHours / workers.size()) * 100.0) / 100.0;
    }
}

class Company {
    private String name;
    private List<Department> departments;
    private List<Employee> employees;

    public Company() {
        this.name = "";
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
        departments.add(department);
        // Add department employees to company employees if not already present
        for (Employee emp : department.getEmployees()) {
            if (!employees.contains(emp)) {
                employees.add(emp);
            }
        }
    }

    public void removeDepartment(Department department) {
        departments.remove(department);
        employees.removeAll(department.getEmployees());
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    private double roundTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public double calculateTotalEmployeeSalary() {
        double totalSalary = 0.0;

        for (Employee emp : employees) {
            if (emp instanceof Worker) {
                Worker worker = (Worker) emp;
                double salary = worker.getWeeklyWorkingHour() * worker.getHourlyRates();
                if (worker instanceof ShiftWorker) {
                    salary += ((ShiftWorker) worker).calculateHolidayPremium();
                }
                totalSalary += salary;
            } else if (emp instanceof SalesPeople) {
                SalesPeople sp = (SalesPeople) emp;
                totalSalary += sp.getSalary() + (sp.getAmountOfSales() * sp.getCommissionPercentage());
            } else if (emp instanceof Manager) {
                Manager m = (Manager) emp;
                totalSalary += m.getSalary();
            }
        }

        return roundTwoDecimals(totalSalary);
    }

    public double calculateTotalSalesPeopleCommission() {
        double totalCommission = 0.0;
        for (Employee emp : employees) {
            if (emp instanceof SalesPeople) {
                SalesPeople sp = (SalesPeople) emp;
                totalCommission += sp.getTotalCommission();
            }
        }
        return roundTwoDecimals(totalCommission);
    }

    public double calculateTotalShiftWorkerHolidayPremiums() {
        double totalPremiums = 0.0;
        for (Employee emp : employees) {
            if (emp instanceof ShiftWorker) {
                totalPremiums += ((ShiftWorker) emp).calculateHolidayPremium();
            }
        }
        return roundTwoDecimals(totalPremiums);
    }
}