import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

public class CR3_CompanyCommissionTest {
    private static final Date FIXED_DATE = new Date(0L);

    private Company createCompany(String name) {
        Company company = new Company();
        company.setName(name);
        return company;
    }

    private SalesPeople createSalesPeople(String name, String socialInsuranceNumber, double salary,
            double amountOfSales, double commissionPercentage) {
        SalesPeople salesperson = new SalesPeople();
        salesperson.setDepartment("Sales");
        salesperson.setName(name);
        salesperson.setBirthDate(FIXED_DATE);
        salesperson.setSocialInsuranceNumber(socialInsuranceNumber);
        salesperson.setSalary(salary);
        salesperson.setAmountOfSales(amountOfSales);
        salesperson.setCommissionPercentage(commissionPercentage);
        return salesperson;
    }

    @Test
    public void singleSalespersonWithNonZeroSales() {
        Company company = createCompany("Commission Co");
        company.getEmployees().add(createSalesPeople("John Doe", "SIN-201", 50000.00, 1000.00, 0.10));

        assertEquals(100.00, company.calculateTotalSalesPeopleCommission(), 0.01);
    }

    @Test
    public void zeroSalespersonsInCompany() {
        Company company = createCompany("Commission Co");

        assertEquals(0.00, company.calculateTotalSalesPeopleCommission(), 0.01);
    }

    @Test
    public void multipleSalespersonsWithNonZeroSales() {
        Company company = createCompany("Commission Co");
        company.getEmployees().add(createSalesPeople("Alice", "SIN-202", 60000.00, 2000.00, 0.15));
        company.getEmployees().add(createSalesPeople("Bob", "SIN-203", 55000.00, 3000.00, 0.20));

        assertEquals(900.00, company.calculateTotalSalesPeopleCommission(), 0.01);
    }

    @Test
    public void singleSalespersonWithZeroSales() {
        Company company = createCompany("Commission Co");
        company.getEmployees().add(createSalesPeople("Charlie", "SIN-204", 50000.00, 0.00, 0.12));

        assertEquals(0.00, company.calculateTotalSalesPeopleCommission(), 0.01);
    }

    @Test
    public void multipleSalespersonsWithMixedSales() {
        Company company = createCompany("Commission Co");
        company.getEmployees().add(createSalesPeople("Dave", "SIN-205", 62000.00, 1500.00, 0.08));
        company.getEmployees().add(createSalesPeople("Eve", "SIN-206", 58000.00, 0.00, 0.10));
        company.getEmployees().add(createSalesPeople("Frank", "SIN-207", 59000.00, 4000.00, 0.25));

        assertEquals(1120.00, company.calculateTotalSalesPeopleCommission(), 0.01);
    }
}
