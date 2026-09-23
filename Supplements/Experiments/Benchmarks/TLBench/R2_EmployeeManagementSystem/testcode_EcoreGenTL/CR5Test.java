package edu.employee.test;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import edu.employee.EmployeeFactory;
import edu.employee.Company;
import edu.employee.Manager;
import edu.employee.OffShiftWorker;
import edu.employee.ShiftWorker;
import edu.employee.SalesPeople;
import edu.employee.Employee;

/**
 * CR5: Count direct subordinates for managers
 * Uses Manager.getDirectSubordinateEmployeesCount() from deepseek-v4-flash/employee1.
 * Returns subordinates.size().
 */
public class CR5Test {

    private EmployeeFactory factory;

    @Before
    public void setUp() {
        factory = EmployeeFactory.eINSTANCE;
    }

    // ---- Helpers ----

    private Manager createManager(String name, String sin, String dept,
                                   double salary, String position) {
        Manager m = factory.createManager();
        m.setName(name);
        m.setSocialInsuranceNumber(sin);
        m.setDepartment(dept);
        m.setSalary(salary);
        m.setPosition(position);
        return m;
    }

    private OffShiftWorker createOffShiftWorker(String name, String sin, String dept,
                                                 int hours, double rate,
                                                 boolean weekendPermit, boolean officialHolidayPermit) {
        OffShiftWorker w = factory.createOffShiftWorker();
        w.setName(name);
        w.setSocialInsuranceNumber(sin);
        w.setDepartment(dept);
        w.setWeeklyWorkingHour(hours);
        w.setHourlyRates(rate);
        w.setWeekendPermit(weekendPermit);
        w.setOfficialHolidayPermit(officialHolidayPermit);
        return w;
    }

    private ShiftWorker createShiftWorker(String name, String sin, String dept,
                                           int hours, double rate, double holidayPremium) {
        ShiftWorker w = factory.createShiftWorker();
        w.setName(name);
        w.setSocialInsuranceNumber(sin);
        w.setDepartment(dept);
        w.setWeeklyWorkingHour(hours);
        w.setHourlyRates(rate);
        w.setHolidayPremium(holidayPremium);
        return w;
    }

    private SalesPeople createSalesPeople(String name, String sin, String dept,
                                           double salary, double salesAmount,
                                           double commissionPercentage) {
        SalesPeople s = factory.createSalesPeople();
        s.setName(name);
        s.setSocialInsuranceNumber(sin);
        s.setDepartment(dept);
        s.setSalary(salary);
        s.setAmountOfSales(salesAmount);
        s.setCommissionPercentage(commissionPercentage);
        return s;
    }

    // ---- CR5 Test Cases ----

    /**
     * TC1: "Manager with three direct reports"
     * M1 supervises: W1 (OffShiftWorker), S1 (SalesPeople), M2 (Manager) → 3
     */
    @Test
    public void testCase1_ThreeDirectReports() {
        Manager m1 = createManager("M1", "SIN-401", "Sales", 75000.00, "Senior Manager");

        OffShiftWorker w1 = createOffShiftWorker("W1", "SIN-402", "Sales",
            40, 20.00, true, false);
        SalesPeople s1 = createSalesPeople("S1", "SIN-403", "Sales",
            50000.00, 200000.00, 0.05);
        Manager m2 = createManager("M2", "SIN-404", "Sales", 70000.00, "Junior Manager");

        m1.getSubordinates().add(w1);
        m1.getSubordinates().add(s1);
        m1.getSubordinates().add(m2);

        assertEquals("M1 should have 3 direct subordinates", 3, m1.getDirectSubordinateEmployeesCount());
    }

    /**
     * TC2: "Manager without direct reports"
     * M1 with empty subordinate list → 0
     */
    @Test
    public void testCase2_NoDirectReports() {
        Manager m1 = createManager("M1", "SIN-405", "Marketing", 80000.00, "Marketing Manager");

        assertEquals("M1 should have 0 direct subordinates", 0, m1.getDirectSubordinateEmployeesCount());
    }

    /**
     * TC3: "Two managers with different team sizes"
     * Manager A: 3 subordinates (SW1, OSW1, SP1)
     * Manager B: 7 subordinates (SW2, OSW2, SP2, SP3, SW3, OSW3, SP4)
     */
    @Test
    public void testCase3_TwoManagersDifferentSizes() {
        // Manager A's subordinates
        Manager managerA = createManager("A", "SIN-406", "IT", 90000.00, "IT Manager");
        ShiftWorker sw1 = createShiftWorker("SW1", "SIN-407", "Delivery", 40, 25.00, 200.00);
        OffShiftWorker osw1 = createOffShiftWorker("OSW1", "SIN-408", "IT", 35, 22.00, true, false);
        SalesPeople sp1 = createSalesPeople("SP1", "SIN-409", "IT", 60000.00, 250000.00, 0.04);
        managerA.getSubordinates().add(sw1);
        managerA.getSubordinates().add(osw1);
        managerA.getSubordinates().add(sp1);

        // Manager B's subordinates
        Manager managerB = createManager("B", "SIN-410", "IT", 95000.00, "Head of IT");
        ShiftWorker sw2 = createShiftWorker("SW2", "SIN-411", "Delivery", 40, 25.00, 300.00);
        OffShiftWorker osw2 = createOffShiftWorker("OSW2", "SIN-412", "IT", 35, 22.00, true, true);
        SalesPeople sp2 = createSalesPeople("SP2", "SIN-413", "IT", 65000.00, 300000.00, 0.05);
        SalesPeople sp3 = createSalesPeople("SP3", "SIN-414", "IT", 60000.00, 260000.00, 0.06);
        ShiftWorker sw3 = createShiftWorker("SW3", "SIN-415", "Delivery", 40, 25.00, 250.00);
        OffShiftWorker osw3 = createOffShiftWorker("OSW3", "SIN-416", "IT", 35, 22.00, false, true);
        SalesPeople sp4 = createSalesPeople("SP4", "SIN-417", "IT", 64000.00, 280000.00, 0.07);
        managerB.getSubordinates().add(sw2);
        managerB.getSubordinates().add(osw2);
        managerB.getSubordinates().add(sp2);
        managerB.getSubordinates().add(sp3);
        managerB.getSubordinates().add(sw3);
        managerB.getSubordinates().add(osw3);
        managerB.getSubordinates().add(sp4);

        assertEquals("Manager A should have 3 direct subordinates", 3, managerA.getDirectSubordinateEmployeesCount());
        assertEquals("Manager B should have 7 direct subordinates", 7, managerB.getDirectSubordinateEmployeesCount());
    }

    /**
     * TC4: "Manager with one direct report"
     * M1 supervises W1 → 1
     */
    @Test
    public void testCase4_OneDirectReport() {
        Manager m1 = createManager("M1", "SIN-418", "HR", 73000.00, "HR Manager");
        OffShiftWorker w1 = createOffShiftWorker("W1", "SIN-419", "HR", 40, 18.00, true, false);
        m1.getSubordinates().add(w1);

        assertEquals("M1 should have 1 direct subordinate", 1, m1.getDirectSubordinateEmployeesCount());
    }

    /**
     * TC5: "Manager supervising another manager"
     * Manager A: W1, S1, S2, S3, Manager B → 5
     * Manager B: W1 → 1
     */
    @Test
    public void testCase5_ManagerSupervisingManager() {
        // Create employees
        OffShiftWorker w1 = createOffShiftWorker("W1", "SIN-421", "Finance", 40, 20.00, true, false);
        SalesPeople s1 = createSalesPeople("S1", "SIN-422", "Finance", 75000.00, 350000.00, 0.06);
        SalesPeople s2 = createSalesPeople("S2", "SIN-423", "Finance", 70000.00, 270000.00, 0.05);
        SalesPeople s3 = createSalesPeople("S3", "SIN-424", "Finance", 68000.00, 280000.00, 0.06);

        // Manager B
        Manager managerB = createManager("B", "SIN-425", "Finance", 110000.00, "Junior Finance Manager");
        managerB.getSubordinates().add(w1);

        // Manager A (supervises W1, S1, S2, S3, and Manager B)
        Manager managerA = createManager("A", "SIN-420", "Finance", 120000.00, "Finance Head");
        managerA.getSubordinates().add(w1);
        managerA.getSubordinates().add(s1);
        managerA.getSubordinates().add(s2);
        managerA.getSubordinates().add(s3);
        managerA.getSubordinates().add(managerB);

        assertEquals("Manager A should have 5 direct subordinates", 5, managerA.getDirectSubordinateEmployeesCount());
        assertEquals("Manager B should have 1 direct subordinate", 1, managerB.getDirectSubordinateEmployeesCount());
    }
}
