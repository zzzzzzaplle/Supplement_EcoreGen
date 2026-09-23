
import static org.junit.Assert.*;
import org.junit.Test;
import java.util.ArrayList;

public class CR5_ApplicationCancellationTest {

    // Helper method to create Application
    private Application createApplication(int share, double amountOfMoney, ApplicationStatus status, Customer customer,
            Company company, Document allowance) {
        Application app = new Application();
        app.setShare(share);
        app.setAmountOfMoney(amountOfMoney);
        app.setStatus(status);
        app.setCustomer(customer);
        app.setCompany(company);
        app.setAllowance(allowance);
        return app;
    }

    @Test
    public void testCancelStillPendingRequest() {
        // Test Case 1: "Cancel still-pending request"
        // Setup
        Customer customer = new Customer();
        customer.setName("Benjamin Taylor");
        customer.setEmail("b.taylor@example.com");
        customer.setTelephone("555-1010");
        customer.setCanApplyForIPO(true);

        Company company = new Company();
        company.setName("EcoWave");
        company.setEmail("ecowave@gmail.com");

        Document document = new Document();

        Application application = createApplication(15, 750, ApplicationStatus.PENDING, customer, company, document);
        customer.getApplications().add(application);

        // Expected Output
        boolean cancellationResult = customer.cancelApplication("EcoWave");

        // Assertions
        assertTrue("Cancel pending request failed", cancellationResult);
    }

    @Test
    public void testCancelApprovedRequest() {
        // Test Case 2: "Cancel approved request"
        // Setup
        Customer customer = new Customer();
        customer.setName("Charlotte Lee");
        customer.setEmail("c.lee@example.com");
        customer.setTelephone("555-2020");
        customer.setCanApplyForIPO(true);

        Company company = new Company();
        company.setName("SmartGrid");
        company.setEmail("smartgrid@business.com");

        Document document = new Document();

        Application application = createApplication(30, 3000, ApplicationStatus.APPROVAL, customer, company, document);
        customer.getApplications().add(application);

        // Expected Output
        boolean cancellationResult = customer.cancelApplication("SmartGrid");

        // Assertions
        assertFalse("Cancel approved request should not succeed", cancellationResult);
    }

    @Test
    public void testCancelRejectedRequest() {
        // Test Case 3: "Cancel rejected request"
        // Setup
        Customer customer = new Customer();
        customer.setName("Lucas Martin");
        customer.setEmail("l.martin@example.com");
        customer.setTelephone("555-3030");
        customer.setCanApplyForIPO(true);

        Company company = new Company();
        company.setName("MedLife");
        company.setEmail("medlife@health.com");

        Document document = new Document();

        Application application = createApplication(20, 1000, ApplicationStatus.REJECTED, customer, company, document);
        customer.getApplications().add(application);

        // Expected Output
        boolean cancellationResult = customer.cancelApplication("MedLife");

        // Assertions
        assertFalse("Cancel rejected request should not succeed", cancellationResult);
    }

    @Test
    public void testCancelNonexistentCompany() {
        // Test Case 4: "Cancel nonexistent company"
        // Setup
        Customer customer = new Customer();
        customer.setName("Amelia Clark");
        customer.setEmail("a.clark@example.com");
        customer.setTelephone("555-4040");
        customer.setCanApplyForIPO(true);

        // Company "UnknownCorp" does not exist in applications
        boolean cancellationResult = customer.cancelApplication("UnknownCorp");

        // Expected Output
        assertFalse("Cancel nonexistent company should not succeed", cancellationResult);
    }

    @Test
    public void testCancelAfterPriorCancellation() {
        // Test Case 5: "Cancel after prior cancellation"
        // Setup
        Customer customer = new Customer();
        customer.setName("Mia Anderson");
        customer.setEmail("m.anderson@example.com");
        customer.setTelephone("555-6060");
        customer.setCanApplyForIPO(true);

        Company urbantech = new Company();
        urbantech.setName("UrbanTech");
        urbantech.setEmail("urbantech@innovate.com");

        Company agroseed = new Company();
        agroseed.setName("AgroSeed");
        agroseed.setEmail("agroseed@agro.com");

        Document document1 = new Document();
        Document document2 = new Document();

        Application urbanTechApp = createApplication(25, 1250, ApplicationStatus.PENDING, customer, urbantech, document1);
        Application agroSeedApp = createApplication(40, 2000, ApplicationStatus.PENDING, customer, agroseed, document2);

        customer.getApplications().add(urbanTechApp);
        customer.getApplications().add(agroSeedApp);

        // Initial Cancellation
        boolean urbanTechCancellationResult = customer.cancelApplication("UrbanTech");
        assertTrue("UrbanTech application should be canceled", urbanTechCancellationResult);

        // Verify AgroSeed application remains
        boolean agroSeedExists = customer.getApplications().stream().anyMatch(
                app -> app.getCompany().getName().equals("AgroSeed") && app.getStatus() == ApplicationStatus.PENDING);
        assertTrue("AgroSeed application should remain unaffected", agroSeedExists);
    }
}

/*
 * compile_result:
 * 
 * 
 * 
 * run_result:
 * JUnit version 4.13.2
 * .....
 * Time: 0.013
 * 
 * OK (5 tests)
 * 
 * 
 */