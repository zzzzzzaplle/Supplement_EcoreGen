import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        if (department != null && !department.equals("DELIVERY")) {
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
        this.type = DepartmentType.PRODUCTION;
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

        return Math.round((double) totalHours / workerCount * 100.0) / 100.0;
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
        if (!departments.contains(department)) {
            departments.add(department);
        }
    }

    public void removeDepartment(Department department) {
        departments.remove(department);
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public double calculateTotalEmployeeSalary() {
        double totalSalary = 0.0;

        for (Employee employee : employees) {
            if (employee instanceof Worker) {
                Worker worker = (Worker) employee;
                double workerSalary = worker.getWeeklyWorkingHour() * worker.getHourlyRates();
                if (employee instanceof ShiftWorker) {
                    ShiftWorker shiftWorker = (ShiftWorker) employee;
                    workerSalary += shiftWorker.calculateHolidayPremium();
                }
                totalSalary += workerSalary;
            } else if (employee instanceof SalesPeople) {
                SalesPeople salesPerson = (SalesPeople) employee;
                double salesPersonSalary = salesPerson.getSalary() + (salesPerson.getAmountOfSales() * salesPerson.getCommissionPercentage());
                totalSalary += salesPersonSalary;
            } else if (employee instanceof Manager) {
                Manager manager = (Manager) employee;
                totalSalary += manager.getSalary();
            }
        }

        return Math.round(totalSalary * 100.0) / 100.0;
    }

    public double calculateTotalSalesPeopleCommission() {
        double totalCommission = 0.0;

        for (Employee employee : employees) {
            if (employee instanceof SalesPeople) {
                SalesPeople salesPerson = (SalesPeople) employee;
                totalCommission += salesPerson.getTotalCommission();
            }
        }

        return Math.round(totalCommission * 100.0) / 100.0;
    }

    public double calculateTotalShiftWorkerHolidayPremiums() {
        double totalPremiums = 0.0;
        boolean hasShiftWorkers = false;

        for (Employee employee : employees) {
            if (employee instanceof ShiftWorker) {
                ShiftWorker shiftWorker = (ShiftWorker) employee;
                totalPremiums += shiftWorker.calculateHolidayPremium();
                hasShiftWorkers = true;
            }
        }

        if (!hasShiftWorkers) {
            return 0.0;
        }

        return Math.round(totalPremiums * 100.0) / 100.0;
    }
}