import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

public class CR4_CompanyShiftWorkersHolidayPremiumTest {
    private static final Date FIXED_DATE = new Date(0L);

    private Company createCompany(String name) {
        Company company = new Company();
        company.setName(name);
        return company;
    }

    private ShiftWorker createShiftWorker(String name, String socialInsuranceNumber, double holidayPremium) {
        ShiftWorker worker = new ShiftWorker();
        worker.setDepartment("Delivery");
        worker.setName(name);
        worker.setBirthDate(FIXED_DATE);
        worker.setSocialInsuranceNumber(socialInsuranceNumber);
        worker.setWeeklyWorkingHour(40);
        worker.setHourlyRates(20.00);
        worker.setHolidayPremium(holidayPremium);
        return worker;
    }

    @Test
    public void noShiftWorkersInTheCompany() {
        Company company = createCompany("Premium Co");

        assertEquals(0.00, company.calculateTotalShiftWorkerHolidayPremiums(), 0.01);
    }

    @Test
    public void oneShiftWorkerInTheCompany() {
        Company company = createCompany("Premium Co");
        company.getEmployees().add(createShiftWorker("Alice", "SIN-301", 500.00));

        assertEquals(500.00, company.calculateTotalShiftWorkerHolidayPremiums(), 0.01);
    }

    @Test
    public void multipleShiftWorkersWithDifferentPremiums() {
        Company company = createCompany("Premium Co");
        company.getEmployees().add(createShiftWorker("Alice", "SIN-302", 200.00));
        company.getEmployees().add(createShiftWorker("Bob", "SIN-303", 300.00));
        company.getEmployees().add(createShiftWorker("Charlie", "SIN-304", 400.00));

        assertEquals(900.00, company.calculateTotalShiftWorkerHolidayPremiums(), 0.01);
    }

    @Test
    public void multipleShiftWorkersWithSomeZeroPremiums() {
        Company company = createCompany("Premium Co");
        company.getEmployees().add(createShiftWorker("Harry", "SIN-305", 0.00));
        company.getEmployees().add(createShiftWorker("Alice", "SIN-306", 250.00));
        company.getEmployees().add(createShiftWorker("Bob", "SIN-307", 0.00));
        company.getEmployees().add(createShiftWorker("Charlie", "SIN-308", 150.00));

        assertEquals(400.00, company.calculateTotalShiftWorkerHolidayPremiums(), 0.01);
    }

    @Test
    public void singleShiftWorkerWithZeroPremium() {
        Company company = createCompany("Premium Co");
        company.getEmployees().add(createShiftWorker("Alice", "SIN-309", 0.00));

        assertEquals(0.00, company.calculateTotalShiftWorkerHolidayPremiums(), 0.01);
    }
}
