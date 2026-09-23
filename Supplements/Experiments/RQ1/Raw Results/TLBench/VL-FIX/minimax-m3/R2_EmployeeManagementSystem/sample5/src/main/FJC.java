import java.util.ArrayList;
import java.util.Date;
import java.util.List;

abstract class Employee {
    private String department;
    private String name;
    private Date birthDate;
    private String socialInsuranceNumber;

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

class Manager extends Employee {
    private double salary;
    private String position;
    private List<Employee> subordinates;

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
        return subordinates;
    }

    public void setSubordinates(List<Employee> subordinates) {
        this.subordinates = subordinates;
    }

    public int getDirectSubordinateEmployeesCount() {
        if (subordinates == null) {
            return 0;
        }
        return subordinates.size();
    }
}

class SalesPeople extends Employee {
    private double salary;
    private double amountOfSales;
    private double commissionPercentage;

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

    public double getTotalCommission() {
        return amountOfSales * commissionPercentage;
    }
}

abstract class Worker extends Employee {
    private int weeklyWorkingHour;
    private double hourlyRates;

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

class ShiftWorker extends Worker {
    private double holidayPremium;

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
        this.employees = employees;
    }

    public double calculateAverageWorkerWorkingHours() {
        if (employees == null || employees.isEmpty()) {
            return 0.0;
        }
        int totalHours = 0;
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
        double average = (double) totalHours / workerCount;
        return Math.round(average * 100.0) / 100.0;
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

    public void addDepartment(Department department) {
        if (departments == null) {
            departments = new ArrayList<>();
        }
        departments.add(department);
    }

    public void removeDepartment(Department department) {
        if (departments != null) {
            departments.remove(department);
        }
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public double calculateTotalEmployeeSalary() {
        double totalSalary = 0.0;
        if (employees != null) {
            for (Employee employee : employees) {
                if (employee instanceof ShiftWorker) {
                    ShiftWorker shiftWorker = (ShiftWorker) employee;
                    totalSalary += shiftWorker.getWeeklyWorkingHour() * shiftWorker.getHourlyRates();
                    totalSalary += shiftWorker.calculateHolidayPremium();
                } else if (employee instanceof OffShiftWorker) {
                    OffShiftWorker offShiftWorker = (OffShiftWorker) employee;
                    totalSalary += offShiftWorker.getWeeklyWorkingHour() * offShiftWorker.getHourlyRates();
                } else if (employee instanceof SalesPeople) {
                    SalesPeople salesPeople = (SalesPeople) employee;
                    totalSalary += salesPeople.getSalary();
                    totalSalary += salesPeople.getAmountOfSales() * salesPeople.getCommissionPercentage();
                } else if (employee instanceof Manager) {
                    Manager manager = (Manager) employee;
                    totalSalary += manager.getSalary();
                }
            }
        }
        return Math.round(totalSalary * 100.0) / 100.0;
    }

    public double calculateTotalSalesPeopleCommission() {
        double totalCommission = 0.0;
        if (employees != null) {
            for (Employee employee : employees) {
                if (employee instanceof SalesPeople) {
                    SalesPeople salesPeople = (SalesPeople) employee;
                    totalCommission += salesPeople.getAmountOfSales() * salesPeople.getCommissionPercentage();
                }
            }
        }
        return Math.round(totalCommission * 100.0) / 100.0;
    }

    public double calculateTotalShiftWorkerHolidayPremiums() {
        double totalPremiums = 0.0;
        if (employees != null) {
            for (Employee employee : employees) {
                if (employee instanceof ShiftWorker) {
                    ShiftWorker shiftWorker = (ShiftWorker) employee;
                    totalPremiums += shiftWorker.calculateHolidayPremium();
                }
            }
        }
        return Math.round(totalPremiums * 100.0) / 100.0;
    }
}

enum DepartmentType {
    PRODUCTION,
    CONTROL,
    DELIVERY
}