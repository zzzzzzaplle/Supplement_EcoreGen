package edu.employee.test;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import edu.employee.EmployeeFactory;
import edu.employee.Company;
import edu.employee.ShiftWorker;

/**
 * CR4: Calculate total shift worker holiday premiums
 * Uses Company.calculateTotalShiftWorkerHolidayPremiums() from deepseek-v4-flash/employee1.
 * Sums holidayPremium for all ShiftWorker employees in the company.
 */
public class CR4Test {

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

    private ShiftWorker addShiftWorker(Company company, String name, String sin,
                                        int hours, double rate, double holidayPremium) {
        ShiftWorker w = factory.createShiftWorker();
        w.setName(name);
        w.setSocialInsuranceNumber(sin);
        w.setWeeklyWorkingHour(hours);
        w.setHourlyRates(rate);
        w.setHolidayPremium(holidayPremium);
        company.getEmployees().add(w);
        return w;
    }

    // ---- CR4 Test Cases ----

    /**
     * TC1: "Company without shift workers"
     * No employees → 0.00
     */
    @Test
    public void testCase1_NoShiftWorkers() {
        Company company = createCompany("Premium Co");

        double premium = company.calculateTotalShiftWorkerHolidayPremiums();

        assertEquals("Total holiday premium should be 0.00", 0.00, premium, 0.01);
    }

    /**
     * TC2: "Company with one shift worker premium"
     * holidayPremium=500.00 → 500.00
     */
    @Test
    public void testCase2_OneShiftWorker() {
        Company company = createCompany("Premium Co");
        addShiftWorker(company, "Alice", "SIN-301", 40, 20.00, 500.00);

        double premium = company.calculateTotalShiftWorkerHolidayPremiums();

        assertEquals("Total holiday premium should be 500.00", 500.00, premium, 0.01);
    }

    /**
     * TC3: "Several shift workers with different premiums"
     * W1: 200.00, W2: 300.00, W3: 400.00 → 900.00
     */
    @Test
    public void testCase3_DifferentPremiums() {
        Company company = createCompany("Premium Co");
        addShiftWorker(company, "Alice", "SIN-302", 40, 20.00, 200.00);
        addShiftWorker(company, "Bob", "SIN-303", 40, 20.00, 300.00);
        addShiftWorker(company, "Charlie", "SIN-304", 40, 20.00, 400.00);

        double premium = company.calculateTotalShiftWorkerHolidayPremiums();

        assertEquals("Total holiday premium should be 900.00", 900.00, premium, 0.01);
    }

    /**
     * TC4: "Shift workers with some zero premiums"
     * W1: 0.00, W2: 250.00, W3: 0.00, W4: 150.00 → 400.00
     */
    @Test
    public void testCase4_SomeZeroPremiums() {
        Company company = createCompany("Premium Co");
        addShiftWorker(company, "Harry", "SIN-305", 40, 20.00, 0.00);
        addShiftWorker(company, "Alice", "SIN-306", 40, 20.00, 250.00);
        addShiftWorker(company, "Bob", "SIN-307", 40, 20.00, 0.00);
        addShiftWorker(company, "Charlie", "SIN-308", 40, 20.00, 150.00);

        double premium = company.calculateTotalShiftWorkerHolidayPremiums();

        assertEquals("Total holiday premium should be 400.00", 400.00, premium, 0.01);
    }

    /**
     * TC5: "Single shift worker with zero holiday premium"
     * holidayPremium=0.00 → 0.00
     */
    @Test
    public void testCase5_ZeroPremium() {
        Company company = createCompany("Premium Co");
        addShiftWorker(company, "Alice", "SIN-309", 40, 20.00, 0.00);

        double premium = company.calculateTotalShiftWorkerHolidayPremiums();

        assertEquals("Total holiday premium should be 0.00", 0.00, premium, 0.01);
    }
}
