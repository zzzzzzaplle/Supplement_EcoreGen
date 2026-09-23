import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.RoundingMode;

enum DepartmentType {
    PRODUCTION, CONTROL, DELIVERY
}

abstract class Employee {
    private String department;
    private String name;
    private Date birthDate;
    private String socialInsuranceNumber;

    public Employee() {}

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Date getBirthDate() { return birthDate; }
    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }
    public String getSocialInsuranceNumber() { return socialInsuranceNumber; }
    public void setSocialInsuranceNumber(String socialInsuranceNumber) { this.socialInsuranceNumber = socialInsuranceNumber; }
}

abstract class Worker extends Employee {
    private int weeklyWorkingHour;
    private double hourlyRates;

    public Worker() {}

    public int getWeeklyWorkingHour() { return weeklyWorkingHour; }
    public void setWeeklyWorkingHour(int weeklyWorkingHour) { this.weeklyWorkingHour = weeklyWorkingHour; }
    public double getHourlyRates() { return hourlyRates; }
    public void setHourlyRates(double hourlyRates) { this.hourlyRates = hourlyRates; }
}

class ShiftWorker extends Worker {
    private double holidayPremium;

    public ShiftWorker() {}

    public double getHolidayPremium() { return holidayPremium; }
    public void setHolidayPremium(double holidayPremium) { this.holidayPremium = holidayPremium; }

    public void setDepartment(String department) {
        super.setDepartment(department);
    }

    public double calculateHolidayPremium() {
        return holidayPremium;
    }
}

class OffShiftWorker extends Worker {
    private boolean weekendPermit;
    private boolean officialHolidayPermit;

    public OffShiftWorker() {}

    public boolean isWeekendPermit() { return weekendPermit; }
    public void setWeekendPermit(boolean weekendPermit) { this.weekendPermit = weekendPermit; }
    public boolean isOfficialHolidayPermit() { return officialHolidayPermit; }
    public void setOfficialHolidayPermit(boolean officialHolidayPermit) { this.officialHolidayPermit = officialHolidayPermit; }
}

class SalesPeople extends Employee {
    private double salary;
    private double amountOfSales;
    private double commissionPercentage;

    public SalesPeople() {}

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
    public double getAmountOfSales() { return amountOfSales; }
    public void setAmountOfSales(double amountOfSales) { this.amountOfSales = amountOfSales; }
    public double getCommissionPercentage() { return commissionPercentage; }
    public void setCommissionPercentage(double commissionPercentage) { this.commissionPercentage = commissionPercentage; }

    public double getTotalCommission() {
        return amountOfSales * commissionPercentage;
    }
}

class Manager extends Employee {
    private double salary;
    private String position;
    private List<Employee> subordinates;

    public Manager() {
        this.subordinates = new ArrayList<>();
    }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public List<Employee> getSubordinates() { return subordinates; }
    public void setSubordinates(List<Employee> subordinates) { this.subordinates = subordinates; }

    public int getDirectSubordinateEmployeesCount() {
        return subordinates.size();
    }
}

class Department {
    private DepartmentType type;
    private Manager manager;
    private List<Employee> employees;

    public Department() {
        this.employees = new ArrayList<>();
    }

    public DepartmentType getType() { return type; }
    public void setType(DepartmentType type) { this.type = type; }
    public Manager getManager() { return manager; }
    public void setManager(Manager manager) { this.manager = manager; }
    public List<Employee> getEmployees() { return employees; }
    public void setEmployees(List<Employee> employees) { this.employees = employees; }

    public double calculateAverageWorkerWorkingHours() {
        int totalHours = 0;
        int workerCount = 0;
        for (Employee emp : employees) {
            if (emp instanceof Worker) {
                Worker worker = (Worker) emp;
                totalHours += worker.getWeeklyWorkingHour();
                workerCount++;
            }
        }
        if (workerCount == 0) {
            return 0.0;
        }
        double average = (double) totalHours / workerCount;
        BigDecimal bd = new BigDecimal(average).setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
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

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Department> getDepartments() { return departments; }
    public void setDepartments(List<Department> departments) { this.departments = departments; }
    public List<Employee> getEmployees() { return employees; }
    public void setEmployees(List<Employee> employees) { this.employees = employees; }

    public void addDepartment(Department department) {
        departments.add(department);
    }

    public void removeDepartment(Department department) {
        departments.remove(department);
    }

    public double calculateTotalEmployeeSalary() {
        double totalSalary = 0.0;
        for (Employee emp : employees) {
            if (emp instanceof Worker) {
                Worker worker = (Worker) emp;
                double workerSalary = worker.getWeeklyWorkingHour() * worker.getHourlyRates();
                if (emp instanceof ShiftWorker) {
                    ShiftWorker shiftWorker = (ShiftWorker) emp;
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
        BigDecimal bd = new BigDecimal(totalSalary).setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public double calculateTotalSalesPeopleCommission() {
        double totalCommission = 0.0;
        for (Employee emp : employees) {
            if (emp instanceof SalesPeople) {
                SalesPeople sp = (SalesPeople) emp;
                totalCommission += sp.getAmountOfSales() * sp.getCommissionPercentage();
            }
        }
        BigDecimal bd = new BigDecimal(totalCommission).setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public double calculateTotalShiftWorkerHolidayPremiums() {
        double totalPremium = 0.0;
        for (Employee emp : employees) {
            if (emp instanceof ShiftWorker) {
                ShiftWorker sw = (ShiftWorker) emp;
                totalPremium += sw.getHolidayPremium();
            }
        }
        if (totalPremium == 0.0) {
            return 0.0;
        }
        BigDecimal bd = new BigDecimal(totalPremium).setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}