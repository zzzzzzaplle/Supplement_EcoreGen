import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

public class CR2_DeliveryDepartmentWorkingHoursTest {
    private static final Date FIXED_DATE = new Date(0L);

    private Company createCompany(String name) {
        Company company = new Company();
        company.setName(name);
        return company;
    }

    private Department createDepartment(DepartmentType type) {
        Department department = new Department();
        department.setType(type);
        return department;
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

    @Test
    public void singleWorkerInDeliveryDepartment() {
        Department deliveryDepartment = createDepartment(DepartmentType.DELIVERY);
        deliveryDepartment.getEmployees().add(createShiftWorker("John Doe", "SIN-101", 40, 15.00, 5.00));

        assertEquals(40.00, deliveryDepartment.calculateAverageWorkerWorkingHours(), 0.01);
    }

    @Test
    public void multipleWorkersInDeliveryDepartmentWithSameHours() {
        Department deliveryDepartment = createDepartment(DepartmentType.DELIVERY);
        deliveryDepartment.getEmployees().add(createShiftWorker("John Doe", "SIN-102", 35, 15.00, 5.00));
        deliveryDepartment.getEmployees().add(createShiftWorker("Jane Roe", "SIN-103", 35, 15.00, 5.00));
        deliveryDepartment.getEmployees().add(createOffShiftWorker("Delivery", "Alex Smith", "SIN-104", 35, 15.00,
                true, true));

        assertEquals(35.00, deliveryDepartment.calculateAverageWorkerWorkingHours(), 0.01);
    }

    @Test
    public void multipleWorkersInDeliveryDepartmentWithDifferentHours() {
        Department deliveryDepartment = createDepartment(DepartmentType.DELIVERY);
        deliveryDepartment.getEmployees().add(createShiftWorker("John Doe", "SIN-105", 20, 15.00, 5.00));
        deliveryDepartment.getEmployees().add(createOffShiftWorker("Delivery", "Jane Roe", "SIN-106", 30, 15.00,
                false, true));

        assertEquals(25.00, deliveryDepartment.calculateAverageWorkerWorkingHours(), 0.01);
    }

    @Test
    public void deliveryDepartmentWithNoWorkers() {
        Department deliveryDepartment = createDepartment(DepartmentType.DELIVERY);

        assertEquals(0.00, deliveryDepartment.calculateAverageWorkerWorkingHours(), 0.01);
    }

    @Test
    public void deliveryDepartmentMixedWithOtherDepartments() {
        Company company = createCompany("Multi Department Co");
        Department deliveryDepartment = createDepartment(DepartmentType.DELIVERY);
        deliveryDepartment.getEmployees().add(createShiftWorker("John Doe", "SIN-107", 25, 15.00, 5.00));
        deliveryDepartment.getEmployees().add(createShiftWorker("Jane Roe", "SIN-108", 32, 15.00, 5.00));
        deliveryDepartment.getEmployees().add(createOffShiftWorker("Delivery", "Alex Smith", "SIN-109", 28, 15.00,
                true, false));

        Department productionDepartment = createDepartment(DepartmentType.PRODUCTION);
        productionDepartment.getEmployees().add(createOffShiftWorker("Production", "Mike Johnson", "SIN-110", 40,
                20.00, true, false));

        Department controlDepartment = createDepartment(DepartmentType.CONTROL);
        controlDepartment.getEmployees().add(createOffShiftWorker("Control", "Emily Williams", "SIN-111", 45,
                22.00, false, true));

        company.addDepartment(deliveryDepartment);
        company.addDepartment(productionDepartment);
        company.addDepartment(controlDepartment);

        assertEquals(28.33, deliveryDepartment.calculateAverageWorkerWorkingHours(), 0.01);
    }
}
