import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

public class CR5_ManagerSubordinatesTest {
    private static final Date FIXED_DATE = new Date(0L);

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

    @Test
    public void singleManagerWithMultipleSubordinates() {
        Manager manager = createManager("Sales", "M1", "SIN-401", 75000.00, "Senior Manager");
        manager.getSubordinates().add(createOffShiftWorker("Sales", "W1", "SIN-402", 40, 20.00, true, false));
        manager.getSubordinates().add(createSalesPeople("Sales", "S1", "SIN-403", 50000.00, 200000.00, 0.05));
        manager.getSubordinates().add(createManager("Sales", "M2", "SIN-404", 70000.00, "Junior Manager"));

        assertEquals(3, manager.getDirectSubordinateEmployeesCount());
    }

    @Test
    public void managerWithNoSubordinates() {
        Manager manager = createManager("Marketing", "M1", "SIN-405", 80000.00, "Marketing Manager");

        assertEquals(0, manager.getDirectSubordinateEmployeesCount());
    }

    @Test
    public void multipleManagersWithDifferentNumbersOfSubordinates() {
        Manager managerA = createManager("IT", "A", "SIN-406", 90000.00, "IT Manager");
        managerA.getSubordinates().add(createShiftWorker("SW1", "SIN-407", 40, 25.00, 200.00));
        managerA.getSubordinates().add(createOffShiftWorker("IT", "OSW1", "SIN-408", 35, 22.00, true, false));
        managerA.getSubordinates().add(createSalesPeople("IT", "SP1", "SIN-409", 60000.00, 250000.00, 0.04));

        Manager managerB = createManager("IT", "B", "SIN-410", 95000.00, "Head of IT");
        managerB.getSubordinates().add(createShiftWorker("SW2", "SIN-411", 40, 25.00, 300.00));
        managerB.getSubordinates().add(createOffShiftWorker("IT", "OSW2", "SIN-412", 35, 22.00, true, true));
        managerB.getSubordinates().add(createSalesPeople("IT", "SP2", "SIN-413", 65000.00, 300000.00, 0.05));
        managerB.getSubordinates().add(createSalesPeople("IT", "SP3", "SIN-414", 60000.00, 260000.00, 0.06));
        managerB.getSubordinates().add(createShiftWorker("SW3", "SIN-415", 40, 25.00, 250.00));
        managerB.getSubordinates().add(createOffShiftWorker("IT", "OSW3", "SIN-416", 35, 22.00, false, true));
        managerB.getSubordinates().add(createSalesPeople("IT", "SP4", "SIN-417", 64000.00, 280000.00, 0.07));

        assertEquals(3, managerA.getDirectSubordinateEmployeesCount());
        assertEquals(7, managerB.getDirectSubordinateEmployeesCount());
    }

    @Test
    public void managerWithASingleSubordinate() {
        Manager manager = createManager("HR", "M1", "SIN-418", 73000.00, "HR Manager");
        manager.getSubordinates().add(createOffShiftWorker("HR", "W1", "SIN-419", 40, 18.00, true, false));

        assertEquals(1, manager.getDirectSubordinateEmployeesCount());
    }

    @Test
    public void managerWithNestedManagerSubordinate() {
        Manager managerA = createManager("Finance", "A", "SIN-420", 120000.00, "Finance Head");
        managerA.getSubordinates().add(createOffShiftWorker("Finance", "W1", "SIN-421", 40, 20.00, true, false));
        managerA.getSubordinates().add(createSalesPeople("Finance", "S1", "SIN-422", 75000.00, 350000.00, 0.06));
        managerA.getSubordinates().add(createSalesPeople("Finance", "S2", "SIN-423", 70000.00, 270000.00, 0.05));
        managerA.getSubordinates().add(createSalesPeople("Finance", "S3", "SIN-424", 68000.00, 280000.00, 0.06));

        Manager managerB = createManager("Finance", "B", "SIN-425", 110000.00, "Junior Finance Manager");
        managerA.getSubordinates().add(managerB);
        managerB.getSubordinates().add(createOffShiftWorker("Finance", "W1", "SIN-421", 40, 20.00, true, false));

        assertEquals(5, managerA.getDirectSubordinateEmployeesCount());
        assertEquals(1, managerB.getDirectSubordinateEmployeesCount());
    }
}
