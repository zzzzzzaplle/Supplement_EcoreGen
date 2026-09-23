import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Company company = new Company();
        
        // Setup Employee/Worker test
        ShiftWorker sw = new ShiftWorker();
        sw.setDepartment("DELIVERY");
        sw.setWeeklyWorkingHour(40);
        sw.setHourlyRates(10.0);
        sw.setHolidayPremium(50.0);
        
        company.getEmployees().add(sw);
        
        System.out.println("Total Salary: " + company.calculateTotalEmployeeSalary());
        System.out.println("Total Commission: " + company.calculateTotalSalesPeopleCommission());
        System.out.println("Total Holiday Premiums: " + company.calculateTotalShiftWorkerHolidayPremiums());
        
        Department dept = new Department();
        dept.getEmployees().add(sw);
        System.out.println("Average Hours: " + dept.calculateAverageWorkerWorkingHours());
    }
}
