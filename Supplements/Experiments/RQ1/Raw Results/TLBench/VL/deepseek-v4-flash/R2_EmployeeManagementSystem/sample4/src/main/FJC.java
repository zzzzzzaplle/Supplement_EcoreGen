import java.util.Date;
import java.util.List;
import java.util.ArrayList;

abstract class Employee {
    private String department;
    private String name;
    private Date birthDate;
    private String socialInsuranceNumber;

    // Unparameterized constructor
    public Employee() {
    }

    // Getters and setters
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

    // Unparameterized constructor
    public Manager() {
        this.subordinates = new ArrayList<>();
    }

    // Getters and setters
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

    // Business method: get number of direct subordinate employees
    public int getDirectSubordinateEmployeesCount() {
        return subordinates.size();
    }
}

class SalesPeople extends Employee {
    private double salary;
    private double amountOfSales;
    private double commissionPercentage;

    // Unparameterized constructor
    public SalesPeople() {
    }

    // Getters and setters
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

    // Business method: calculate total commission
    public double getTotalCommission() {
        return amountOfSales * commissionPercentage;
    }
}

abstract class Worker extends Employee {
    private int weeklyWorkingHour;
    private double hourlyRates;

    // Unparameterized constructor
    public Worker() {
    }

    // Getters and setters
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

    // Unparameterized constructor
    public ShiftWorker() {
    }

    // Getters and setters
    public double getHolidayPremium() {
        return holidayPremium;
    }

    public void setHolidayPremium(double holidayPremium) {
        this.holidayPremium = holidayPremium;
    }

    // Override setDepartment to enforce only Delivery department
    @Override
    public void setDepartment(String department) {
        if (!"DELIVERY".equals(department)) {
            throw new IllegalArgumentException("ShiftWorker can only belong to the Delivery department.");
        }
        super.setDepartment(department);
    }

    // Business method: calculate holiday premium
    public double calculateHolidayPremium() {
        return holidayPremium;
    }
}

class OffShiftWorker extends Worker {
    private boolean weekendPermit;
    private boolean officialHolidayPermit;

    // Unparameterized constructor
    public OffShiftWorker() {
    }

    // Getters and setters
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

enum DepartmentType {
    PRODUCTION,
    CONTROL,
    DELIVERY
}

class Department {
    private DepartmentType type;
    private Manager manager;
    private List<Employee> employees;

    // Unparameterized constructor
    public Department() {
        this.employees = new ArrayList<>();
    }

    // Getters and setters
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

    // Business method: calculate average worker working hours in this department
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
        double totalHours = 0.0;
        for (Worker w : workers) {
            totalHours += w.getWeeklyWorkingHour();
        }
        double average = totalHours / workers.size();
        // Round to two decimal places
        return Math.round(average * 100.0) / 100.0;
    }
}

class Company {
    private String name;
    private List<Department> departments;
    private List<Employee> employees;

    // Unparameterized constructor
    public Company() {
        this.departments = new ArrayList<>();
        this.employees = new ArrayList<>();
    }

    // Getters and setters
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

    // Utility methods for departments
    public void addDepartment(Department department) {
        departments.add(department);
    }

    public void removeDepartment(Department department) {
        departments.remove(department);
    }

    // Business method: calculate total salary of all employees
    public double calculateTotalEmployeeSalary() {
        double totalSalary = 0.0;
        for (Employee emp : employees) {
            if (emp instanceof Worker) {
                Worker worker = (Worker) emp;
                double workerSalary = worker.getWeeklyWorkingHour() * worker.getHourlyRates();
                if (worker instanceof ShiftWorker) {
                    workerSalary += ((ShiftWorker) worker).getHolidayPremium();
                }
                totalSalary += workerSalary;
            } else if (emp instanceof SalesPeople) {
                SalesPeople sp = (SalesPeople) emp;
                totalSalary += sp.getSalary() + (sp.getAmountOfSales() * sp.getCommissionPercentage());
            } else if (emp instanceof Manager) {
                Manager mgr = (Manager) emp;
                totalSalary += mgr.getSalary();
            }
        }
        // Round to two decimal places
        return Math.round(totalSalary * 100.0) / 100.0;
    }

    // Business method: calculate total commission of all salespeople
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

    // Business method: calculate total holiday premiums of all shift workers
    public double calculateTotalShiftWorkerHolidayPremiums() {
        double totalPremiums = 0.0;
        for (Employee emp : employees) {
            if (emp instanceof ShiftWorker) {
                totalPremiums += ((ShiftWorker) emp).getHolidayPremium();
            }
        }
        // Round to two decimal places
        return Math.round(totalPremiums * 100.0) / 100.0;
    }
}