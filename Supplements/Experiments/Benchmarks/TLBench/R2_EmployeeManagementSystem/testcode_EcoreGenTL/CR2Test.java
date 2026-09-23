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

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * CR2: Calculate average working hours in the delivery department
 * Uses Department.calculateAverageWorkerWorkingHours() from deepseek-v4-flash/employee1.
 * Computes average of all Worker employees' weeklyWorkingHour in the department.
 */
public class CR2Test {

    private EmployeeFactory factory;

    @Before
    public void setUp() {
        factory = EmployeeFactory.eINSTANCE;
    }

    // ---- Helpers ----

    private Department createDeliveryDepartment() {
        Department dept = factory.createDepartment();
        dept.setType(DepartmentType.DELIVERY);
        return dept;
    }

    private ShiftWorker addShiftWorkerToDept(Department dept, String name, String sin,
                                              int hours, double rate, double holidayPremium) {
        ShiftWorker w = factory.createShiftWorker();
        w.setName(name);
        w.setSocialInsuranceNumber(sin);
        w.setDepartment("Delivery");
        w.setWeeklyWorkingHour(hours);
        w.setHourlyRates(rate);
        w.setHolidayPremium(holidayPremium);
        dept.getEmployees().add(w);
        return w;
    }

    private OffShiftWorker addOffShiftWorkerToDept(Department dept, String name, String sin,
                                                    int hours, double rate,
                                                    boolean weekendPermit, boolean officialHolidayPermit) {
        OffShiftWorker w = factory.createOffShiftWorker();
        w.setName(name);
        w.setSocialInsuranceNumber(sin);
        w.setDepartment("Delivery");
        w.setWeeklyWorkingHour(hours);
        w.setHourlyRates(rate);
        w.setWeekendPermit(weekendPermit);
        w.setOfficialHolidayPermit(officialHolidayPermit);
        dept.getEmployees().add(w);
        return w;
    }

    // ---- CR2 Test Cases ----

    /**
     * TC1: "Delivery department with one worker"
     * One ShiftWorker with 40 hours → avg = 40.00
     */
    @Test
    public void testCase1_OneWorker() {
        Department dept = createDeliveryDepartment();
        addShiftWorkerToDept(dept, "John Doe", "SIN-101", 40, 15.00, 5.00);

        double avg = dept.calculateAverageWorkerWorkingHours();

        assertEquals("Average should be 40.00", 40.00, avg, 0.01);
    }

    /**
     * TC2: "Delivery department with equal working hours"
     * ShiftWorker W1: 35h, ShiftWorker W2: 35h, OffShiftWorker W3: 35h → avg = 35.00
     */
    @Test
    public void testCase2_EqualHours() {
        Department dept = createDeliveryDepartment();
        addShiftWorkerToDept(dept, "John Doe", "SIN-102", 35, 15.00, 5.00);
        addShiftWorkerToDept(dept, "Jane Roe", "SIN-103", 35, 15.00, 5.00);
        addOffShiftWorkerToDept(dept, "Alex Smith", "SIN-104", 35, 15.00, true, true);

        double avg = dept.calculateAverageWorkerWorkingHours();

        assertEquals("Average should be 35.00", 35.00, avg, 0.01);
    }

    /**
     * TC3: "Delivery department with different working hours"
     * ShiftWorker W1: 20h, OffShiftWorker W2: 30h → avg = 25.00
     */
    @Test
    public void testCase3_DifferentHours() {
        Department dept = createDeliveryDepartment();
        addShiftWorkerToDept(dept, "John Doe", "SIN-105", 20, 15.00, 5.00);
        addOffShiftWorkerToDept(dept, "Jane Roe", "SIN-106", 30, 15.00, false, true);

        double avg = dept.calculateAverageWorkerWorkingHours();

        assertEquals("Average should be 25.00", 25.00, avg, 0.01);
    }

    /**
     * TC4: "Delivery department without workers"
     * Empty department → avg = 0.00
     */
    @Test
    public void testCase4_EmptyDepartment() {
        Department dept = createDeliveryDepartment();

        double avg = dept.calculateAverageWorkerWorkingHours();

        assertEquals("Average should be 0.00 for empty department", 0.00, avg, 0.01);
    }

    /**
     * TC5: "Delivery average unaffected by other departments"
     * Delivery: ShiftWorker W1(25h), ShiftWorker W2(32h), OffShiftWorker W3(28h) → avg = 28.33
     * Production and Control departments exist but should not affect delivery average.
     */
    @Test
    public void testCase5_UnaffectedByOtherDepartments() {
        // Delivery department
        Department deliveryDept = createDeliveryDepartment();
        addShiftWorkerToDept(deliveryDept, "John Doe", "SIN-107", 25, 15.00, 5.00);
        addShiftWorkerToDept(deliveryDept, "Jane Roe", "SIN-108", 32, 15.00, 5.00);
        addOffShiftWorkerToDept(deliveryDept, "Alex Smith", "SIN-109", 28, 15.00, true, false);

        // Production department (should not affect delivery average)
        Department productionDept = factory.createDepartment();
        productionDept.setType(DepartmentType.PRODUCTION);
        OffShiftWorker prodWorker = factory.createOffShiftWorker();
        prodWorker.setName("Mike Johnson");
        prodWorker.setSocialInsuranceNumber("SIN-110");
        prodWorker.setDepartment("Production");
        prodWorker.setWeeklyWorkingHour(40);
        prodWorker.setHourlyRates(20.00);
        prodWorker.setWeekendPermit(true);
        prodWorker.setOfficialHolidayPermit(false);
        productionDept.getEmployees().add(prodWorker);

        // Control department (should not affect delivery average)
        Department controlDept = factory.createDepartment();
        controlDept.setType(DepartmentType.CONTROL);
        OffShiftWorker ctrlWorker = factory.createOffShiftWorker();
        ctrlWorker.setName("Emily Williams");
        ctrlWorker.setSocialInsuranceNumber("SIN-111");
        ctrlWorker.setDepartment("Control");
        ctrlWorker.setWeeklyWorkingHour(45);
        ctrlWorker.setHourlyRates(22.00);
        ctrlWorker.setWeekendPermit(false);
        ctrlWorker.setOfficialHolidayPermit(true);
        controlDept.getEmployees().add(ctrlWorker);

        double avg = deliveryDept.calculateAverageWorkerWorkingHours();

        // (25 + 32 + 28) / 3 = 28.33
        assertEquals("Delivery average should be 28.33", 28.33, avg, 0.01);
    }
}
