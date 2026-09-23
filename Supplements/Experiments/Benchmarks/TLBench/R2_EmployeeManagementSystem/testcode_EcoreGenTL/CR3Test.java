package edu.employee.test;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import edu.employee.EmployeeFactory;
import edu.employee.Company;
import edu.employee.SalesPeople;

/**
 * CR3: Calculate total salesperson commission
 * Uses Company.calculateTotalSalesPeopleCommission() from deepseek-v4-flash/employee1.
 * Sums amountOfSales * commissionPercentage for all SalesPeople in the company.
 */
public class CR3Test {

    private EmployeeFactory factory;

    @Before
    public void setUp() {
        factory = EmployeeFactory.eINSTANCE;
    }

    // ---- Helpers ----

    private Company createCompany(String name) {
        Company c = factory.createCompany();
        c.setName(name);
        return c;
    }

    private SalesPeople addSalesPeople(Company company, String name, String sin,
                                        double salary, double salesAmount,
                                        double commissionPercentage) {
        SalesPeople s = factory.createSalesPeople();
        s.setName(name);
        s.setSocialInsuranceNumber(sin);
        s.setSalary(salary);
        s.setAmountOfSales(salesAmount);
        s.setCommissionPercentage(commissionPercentage);
        company.getEmployees().add(s);
        return s;
    }

    // ---- CR3 Test Cases ----

    /**
     * TC1: "One salesperson with commissionable sales"
     * sales=1000.00, commission=0.10 → 100.00
     */
    @Test
    public void testCase1_OneSalesperson() {
        Company company = createCompany("Commission Co");
        addSalesPeople(company, "John Doe", "SIN-201",
            50000.00, 1000.00, 0.10);

        double commission = company.calculateTotalSalesPeopleCommission();

        assertEquals("Total commission should be 100.00", 100.00, commission, 0.01);
    }

    /**
     * TC2: "Company without salespeople"
     * No employees → 0.00
     */
    @Test
    public void testCase2_NoSalespeople() {
        Company company = createCompany("Commission Co");

        double commission = company.calculateTotalSalesPeopleCommission();

        assertEquals("Total commission should be 0.00", 0.00, commission, 0.01);
    }

    /**
     * TC3: "Two salespeople with positive sales"
     * S1: 2000.00 * 0.15 = 300.00
     * S2: 3000.00 * 0.20 = 600.00
     * Total: 900.00
     */
    @Test
    public void testCase3_TwoSalespeople() {
        Company company = createCompany("Commission Co");
        addSalesPeople(company, "Alice", "SIN-202",
            60000.00, 2000.00, 0.15);
        addSalesPeople(company, "Bob", "SIN-203",
            55000.00, 3000.00, 0.20);

        double commission = company.calculateTotalSalesPeopleCommission();

        assertEquals("Total commission should be 900.00", 900.00, commission, 0.01);
    }

    /**
     * TC4: "One salesperson with zero sales"
     * sales=0.00, commission=0.12 → 0.00
     */
    @Test
    public void testCase4_ZeroSales() {
        Company company = createCompany("Commission Co");
        addSalesPeople(company, "Charlie", "SIN-204",
            50000.00, 0.00, 0.12);

        double commission = company.calculateTotalSalesPeopleCommission();

        assertEquals("Total commission should be 0.00", 0.00, commission, 0.01);
    }

    /**
     * TC5: "Mixed sales amounts across three salespeople"
     * S1: 1500.00 * 0.08 = 120.00
     * S2: 0.00 * 0.10 = 0.00
     * S3: 4000.00 * 0.25 = 1000.00
     * Total: 1120.00
     */
    @Test
    public void testCase5_MixedSales() {
        Company company = createCompany("Commission Co");
        addSalesPeople(company, "Dave", "SIN-205",
            62000.00, 1500.00, 0.08);
        addSalesPeople(company, "Eve", "SIN-206",
            58000.00, 0.00, 0.10);
        addSalesPeople(company, "Frank", "SIN-207",
            59000.00, 4000.00, 0.25);

        double commission = company.calculateTotalSalesPeopleCommission();

        assertEquals("Total commission should be 1120.00", 1120.00, commission, 0.01);
    }
}
