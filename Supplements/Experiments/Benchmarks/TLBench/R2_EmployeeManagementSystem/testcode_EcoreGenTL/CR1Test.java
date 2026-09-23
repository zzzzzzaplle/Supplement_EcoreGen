package edu.employee.test;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import edu.employee.EmployeeFactory;
import edu.employee.Company;
import edu.employee.Department;
import edu.employee.DepartmentType;
import edu.employee.OffShiftWorker;
import edu.employee.ShiftWorker;
import edu.employee.SalesPeople;
import edu.employee.Manager;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * CR1: Calculate total employee salary
 * Uses Company.calculateTotalEmployeeSalary() from deepseek-v4-flash/employee1.
 * Formula: Workers = weeklyWorkingHour * hourlyRates (+ holidayPremium for ShiftWorker).
 *          SalesPeople = salary + amountOfSales * commissionPercentage.
 *          Manager = salary.
 */
public class CR1Test {

    private EmployeeFactory factory;
    private SimpleDateFormat dateFormat;

    @Before
    public void setUp() {
        factory = EmployeeFactory.eINSTANCE;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    }

    // ---- Helpers ----

    private Company createCompany(String name) {
        Company c = factory.createCompany();
        c.setName(name);
        return c;
    }

    private Date parseBirthDate(String str) {
        try { return dateFormat.parse(str); }
        catch (ParseException e) { throw new RuntimeException(e); }
    }

    private OffShiftWorker createOffShiftWorker(Company company, String dept, String name,
                                                 String sin, int hours, double rate,
                                                 boolean weekendPermit, boolean officialHolidayPermit) {
        OffShiftWorker w = factory.createOffShiftWorker();
        w.setName(name);
        w.setSocialInsuranceNumber(sin);
        w.setBirthDate(parseBirthDate("1970-01-01T00:00:00Z"));
        w.setDepartment(dept);
        w.setWeeklyWorkingHour(hours);
        w.setHourlyRates(rate);
        w.setWeekendPermit(weekendPermit);
        w.setOfficialHolidayPermit(officialHolidayPermit);
        company.getEmployees().add(w);
        return w;
    }

    private ShiftWorker createShiftWorker(Company company, String dept, String name,
                                           String sin, int hours, double rate, double holidayPremium) {
        ShiftWorker w = factory.createShiftWorker();
        w.setName(name);
        w.setSocialInsuranceNumber(sin);
        w.setBirthDate(parseBirthDate("1970-01-01T00:00:00Z"));
        w.setDepartment(dept);
        w.setWeeklyWorkingHour(hours);
        w.setHourlyRates(rate);
        w.setHolidayPremium(holidayPremium);
        company.getEmployees().add(w);
        return w;
    }

    private SalesPeople createSalesPeople(Company company, String dept, String name,
                                           String sin, double salary, double salesAmount,
                                           double commissionPercentage) {
        SalesPeople s = factory.createSalesPeople();
        s.setName(name);
        s.setSocialInsuranceNumber(sin);
        s.setBirthDate(parseBirthDate("1970-01-01T00:00:00Z"));
        s.setDepartment(dept);
        s.setSalary(salary);
        s.setAmountOfSales(salesAmount);
        s.setCommissionPercentage(commissionPercentage);
        company.getEmployees().add(s);
        return s;
    }

    private Manager createManager(Company company, String dept, String name,
                                   String sin, double salary, String position) {
        Manager m = factory.createManager();
        m.setName(name);
        m.setSocialInsuranceNumber(sin);
        m.setBirthDate(parseBirthDate("1970-01-01T00:00:00Z"));
        m.setDepartment(dept);
        m.setSalary(salary);
        m.setPosition(position);
        company.getEmployees().add(m);
        return m;
    }

    // ---- CR1 Test Cases ----

    /**
     * TC1: "Company with one non-shift worker"
     * OffShiftWorker: 40 * 20.00 = 800.00
     */
    @Test
    public void testCase1_SingleOffShiftWorker() {
        Company company = createCompany("Acme Payroll");
        createOffShiftWorker(company, "Production", "John Doe", "SIN-001",
            40, 20.00, true, false);

        double total = company.calculateTotalEmployeeSalary();

        assertEquals("Total salary should be 800.00", 800.00, total, 0.01);
    }

    /**
     * TC2: "Company with one salesperson"
     * SalesPeople: 3000.00 + 2000.00 * 0.10 = 3200.00
     */
    @Test
    public void testCase2_SingleSalesperson() {
        Company company = createCompany("Acme Payroll");
        createSalesPeople(company, "Sales", "Jane Smith", "SIN-002",
            3000.00, 2000.00, 0.10);

        double total = company.calculateTotalEmployeeSalary();

        assertEquals("Total salary should be 3200.00", 3200.00, total, 0.01);
    }

    /**
     * TC3: "Company with one manager"
     * Manager: 5000.00
     */
    @Test
    public void testCase3_SingleManager() {
        Company company = createCompany("Acme Payroll");
        createManager(company, "Management", "Bob Johnson", "SIN-003",
            5000.00, "Project Manager");

        double total = company.calculateTotalEmployeeSalary();

        assertEquals("Total salary should be 5000.00", 5000.00, total, 0.01);
    }

    /**
     * TC4: "Company with worker, salesperson, and manager"
     * OffShiftWorker: 35 * 22.00 = 770.00
     * SalesPeople: 2800.00 + 1500.00 * 0.15 = 3025.00
     * Manager: 4800.00
     * Total: 8595.00
     */
    @Test
    public void testCase4_MixedEmployeeTypes() {
        Company company = createCompany("Acme Payroll");
        createOffShiftWorker(company, "Production", "John Doe", "SIN-004",
            35, 22.00, true, false);
        createSalesPeople(company, "Sales", "Jane Smith", "SIN-005",
            2800.00, 1500.00, 0.15);
        createManager(company, "Management", "Bob Johnson", "SIN-006",
            4800.00, "Project Manager");

        double total = company.calculateTotalEmployeeSalary();

        assertEquals("Total salary should be 8595.00", 8595.00, total, 0.01);
    }

    /**
     * TC5: "Company with two non-shift workers and one shift worker"
     * OffShiftWorker W1: 45 * 18.00 = 810.00
     * OffShiftWorker W2: 38 * 21.00 = 798.00
     * ShiftWorker W3: 30 * 24.00 + 200.00 = 920.00
     * Total per impl: 2528.00 (implementation only adds holidayPremium for ShiftWorker,
     *   not base hourly pay — matches NLTC expected value)
     */
    @Test
    public void testCase5_TwoOffShiftAndOneShiftWorker() {
        Company company = createCompany("Acme Payroll");
        createOffShiftWorker(company, "Production", "John Doe", "SIN-007",
            45, 18.00, true, false);
        createOffShiftWorker(company, "Production", "Alice Brown", "SIN-008",
            38, 21.00, false, true);
        createShiftWorker(company, "Delivery", "Mike Davis", "SIN-009",
            30, 24.00, 200.00);

        double total = company.calculateTotalEmployeeSalary();

        assertEquals("Total salary should be 2528.00", 2528.00, total, 0.01);
    }
}
