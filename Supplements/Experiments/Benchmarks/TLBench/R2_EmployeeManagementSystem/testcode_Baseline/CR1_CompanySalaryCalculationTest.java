import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

public class CR1_CompanySalaryCalculationTest {
    private static final Date FIXED_DATE = new Date(0L);

    private Company createCompany(String name) {
        Company company = new Company();
        company.setName(name);
        return company;
    }

    private OffShiftWorker createOffShiftWorker(String department, String name, String socialInsuranceNumber,
            int weeklyWorkingHour, double hourlyRates, boolean weekendPermit, boolean officialHolidayPermit) {
        OffShiftWorker worker = new OffShiftWorker();
        worker.setDepartment(department);
        worker.setName(name);
        worker.setBirthDate(FIXED_DATE);
        worker.setSocialInsuranceNumber(socialInsuranceNumber);
        worker.setWeeklyWorkingHour(weeklyWorkingHour);
        worker.setHourlyRates(hourlyRates);
        worker.setWeekendPermit(weekendPermit);
        worker.setOfficialHolidayPermit(officialHolidayPermit);
        return worker;
    }

    private ShiftWorker createShiftWorker(String name, String socialInsuranceNumber, int weeklyWorkingHour,
            double hourlyRates, double holidayPremium) {
        ShiftWorker worker = new ShiftWorker();
        worker.setDepartment("Delivery");
        worker.setName(name);
        worker.setBirthDate(FIXED_DATE);
        worker.setSocialInsuranceNumber(socialInsuranceNumber);
        worker.setWeeklyWorkingHour(weeklyWorkingHour);
        worker.setHourlyRates(hourlyRates);
        worker.setHolidayPremium(holidayPremium);
        return worker;
    }

    private SalesPeople createSalesPeople(String department, String name, String socialInsuranceNumber, double salary,
            double amountOfSales, double commissionPercentage) {
        SalesPeople salesperson = new SalesPeople();
        salesperson.setDepartment(department);
        salesperson.setName(name);
        salesperson.setBirthDate(FIXED_DATE);
        salesperson.setSocialInsuranceNumber(socialInsuranceNumber);
        salesperson.setSalary(salary);
        salesperson.setAmountOfSales(amountOfSales);
        salesperson.setCommissionPercentage(commissionPercentage);
        return salesperson;
    }

    private Manager createManager(String department, String name, String socialInsuranceNumber, double salary,
            String position) {
        Manager manager = new Manager();
        manager.setDepartment(department);
        manager.setName(name);
        manager.setBirthDate(FIXED_DATE);
        manager.setSocialInsuranceNumber(socialInsuranceNumber);
        manager.setSalary(salary);
        manager.setPosition(position);
        return manager;
    }

    @Test
    public void calculateTotalSalaryForCompanyWithSingleWorker() {
        Company company = createCompany("Acme Payroll");
        company.getEmployees().add(createOffShiftWorker("Production", "John Doe", "SIN-001", 40, 20.00, true, false));

        assertEquals(800.00, company.calculateTotalEmployeeSalary(), 0.01);
    }

    @Test
    public void calculateTotalSalaryForCompanyWithSingleSalesPerson() {
        Company company = createCompany("Acme Payroll");
        company.getEmployees().add(createSalesPeople("Sales", "Jane Smith", "SIN-002", 3000.00, 2000.00, 0.10));

        assertEquals(3200.00, company.calculateTotalEmployeeSalary(), 0.01);
    }

    @Test
    public void calculateTotalSalaryForCompanyWithSingleManager() {
        Company company = createCompany("Acme Payroll");
        company.getEmployees().add(createManager("Management", "Bob Johnson", "SIN-003", 5000.00, "Project Manager"));

        assertEquals(5000.00, company.calculateTotalEmployeeSalary(), 0.01);
    }

    @Test
    public void calculateTotalSalaryForCompanyWithWorkerSalesPersonAndManager() {
        Company company = createCompany("Acme Payroll");
        company.getEmployees().add(createOffShiftWorker("Production", "John Doe", "SIN-004", 35, 22.00, true, false));
        company.getEmployees().add(createSalesPeople("Sales", "Jane Smith", "SIN-005", 2800.00, 1500.00, 0.15));
        company.getEmployees().add(createManager("Management", "Bob Johnson", "SIN-006", 4800.00, "Project Manager"));

        assertEquals(8595.00, company.calculateTotalEmployeeSalary(), 0.01);
    }

    @Test
    public void calculateTotalSalaryForCompanyWithMultipleWorkers() {
        Company company = createCompany("Acme Payroll");
        company.getEmployees().add(createOffShiftWorker("Production", "John Doe", "SIN-007", 45, 18.00, true, false));
        company.getEmployees().add(createOffShiftWorker("Production", "Alice Brown", "SIN-008", 38, 21.00, false, true));
        company.getEmployees().add(createShiftWorker("Mike Davis", "SIN-009", 30, 24.00, 200.00));

        assertEquals(2528.00, company.calculateTotalEmployeeSalary(), 0.01);
    }
}
