import java.util.*;
import java.text.DecimalFormat;

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
        if (!"DELIVERY".equals(department)) {
            throw new IllegalArgumentException("Shift workers can only belong to the DELIVERY department.");
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
        List<Worker> workers = new ArrayList<>();
        for (Employee emp : employees) {
            if (emp instanceof Worker) {
                workers.add((Worker) emp);
            }
        }
        if (workers.isEmpty()) {
            return 0;
        }
        double totalHours = 0;
        for (Worker w : workers) {
            totalHours += w.getWeeklyWorkingHour();
        }
        double average = totalHours / workers.size();
        return roundToTwoDecimals(average);
    }

    private double roundToTwoDecimals(double value) {
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.parseDouble(df.format(value));
    }
}

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

    public void addDepartment(Department department) {
        departments.add(department);
        if (department.getManager() != null) {
            employees.add(department.getManager());
        }
        employees.addAll(department.getEmployees());
    }

    public void removeDepartment(Department department) {
        departments.remove(department);
        if (department.getManager() != null) {
            employees.remove(department.getManager());
        }
        employees.removeAll(department.getEmployees());
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public double calculateTotalEmployeeSalary() {
        double totalSalary = 0.0;
        for (Employee emp : employees) {
            if (emp instanceof Worker) {
                Worker worker = (Worker) emp;
                double salary = worker.getWeeklyWorkingHour() * worker.getHourlyRates();
                if (emp instanceof ShiftWorker) {
                    ShiftWorker shiftWorker = (ShiftWorker) emp;
                    salary += shiftWorker.getHolidayPremium();
                }
                totalSalary += salary;
            } else if (emp instanceof SalesPeople) {
                SalesPeople salesPerson = (SalesPeople) emp;
                double salary = salesPerson.getSalary() + (salesPerson.getAmountOfSales() * salesPerson.getCommissionPercentage());
                totalSalary += salary;
            } else if (emp instanceof Manager) {
                Manager manager = (Manager) emp;
                totalSalary += manager.getSalary();
            }
        }
        return roundToTwoDecimals(totalSalary);
    }

    public double calculateTotalSalesPeopleCommission() {
        double totalCommission = 0.0;
        for (Employee emp : employees) {
            if (emp instanceof SalesPeople) {
                SalesPeople salesPerson = (SalesPeople) emp;
                totalCommission += salesPerson.getTotalCommission();
            }
        }
        return roundToTwoDecimals(totalCommission);
    }

    public double calculateTotalShiftWorkerHolidayPremiums() {
        double totalPremiums = 0.0;
        for (Employee emp : employees) {
            if (emp instanceof ShiftWorker) {
                ShiftWorker shiftWorker = (ShiftWorker) emp;
                totalPremiums += shiftWorker.getHolidayPremium();
            }
        }
        return roundToTwoDecimals(totalPremiums);
    }

    public double calculateAverageWorkingHoursDeliveryDepartment() {
        for (Department dept : departments) {
            if (dept.getType() == DepartmentType.DELIVERY) {
                return dept.calculateAverageWorkerWorkingHours();
            }
        }
        return 0;
    }

    private double roundToTwoDecimals(double value) {
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.parseDouble(df.format(value));
    }
}