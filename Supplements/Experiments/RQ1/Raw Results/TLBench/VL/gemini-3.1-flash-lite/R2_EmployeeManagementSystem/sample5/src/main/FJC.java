import java.util.*;
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

class Manager extends Employee {
    private double salary;
    private String position;
    private List<Employee> subordinates = new ArrayList<>();

    public Manager() {}

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

    @Override
    public void setDepartment(String department) {
        if ("DELIVERY".equals(department)) {
            super.setDepartment(department);
        }
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

class Department {
    private DepartmentType type;
    private Manager manager;
    private List<Employee> employees = new ArrayList<>();

    public Department() {}

    public DepartmentType getType() { return type; }
    public void setType(DepartmentType type) { this.type = type; }
    public Manager getManager() { return manager; }
    public void setManager(Manager manager) { this.manager = manager; }
    public List<Employee> getEmployees() { return employees; }
    public void setEmployees(List<Employee> employees) { this.employees = employees; }

    public double calculateAverageWorkerWorkingHours() {
        List<Worker> workers = new ArrayList<>();
        for (Employee e : employees) {
            if (e instanceof Worker) workers.add((Worker) e);
        }
        if (workers.isEmpty()) return 0.0;
        double sum = 0;
        for (Worker w : workers) sum += w.getWeeklyWorkingHour();
        return new BigDecimal(sum / workers.size()).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}

class Company {
    private String name;
    private List<Department> departments = new ArrayList<>();
    private List<Employee> employees = new ArrayList<>();

    public Company() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Department> getDepartments() { return departments; }
    public void addDepartment(Department d) { departments.add(d); }
    public void removeDepartment(Department d) { departments.remove(d); }
    public List<Employee> getEmployees() { return employees; }
    public void setEmployees(List<Employee> employees) { this.employees = employees; }

    public double calculateTotalEmployeeSalary() {
        double total = 0;
        for (Employee e : employees) {
            if (e instanceof Worker) {
                Worker w = (Worker) e;
                total += (w.getWeeklyWorkingHour() * w.getHourlyRates());
                if (w instanceof ShiftWorker) total += ((ShiftWorker) w).getHolidayPremium();
            } else if (e instanceof SalesPeople) {
                SalesPeople s = (SalesPeople) e;
                total += (s.getSalary() + s.getTotalCommission());
            } else if (e instanceof Manager) {
                total += ((Manager) e).getSalary();
            }
        }
        return new BigDecimal(total).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public double calculateTotalSalesPeopleCommission() {
        double total = 0;
        for (Employee e : employees) {
            if (e instanceof SalesPeople) total += ((SalesPeople) e).getTotalCommission();
        }
        return new BigDecimal(total).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public double calculateTotalShiftWorkerHolidayPremiums() {
        double total = 0;
        for (Employee e : employees) {
            if (e instanceof ShiftWorker) total += ((ShiftWorker) e).getHolidayPremium();
        }
        return new BigDecimal(total).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}