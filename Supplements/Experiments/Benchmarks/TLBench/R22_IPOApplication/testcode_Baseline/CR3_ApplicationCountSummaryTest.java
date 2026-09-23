
import static org.junit.Assert.*;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

// Other required imports

public class CR3_ApplicationCountSummaryTest {

    private Customer customer;
    private Company company;
    private Document document;

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
    public void testNoApplicationsAtAll() {
        // Setup
        customer = new Customer();
        customer.setName("Thomas Anderson");
        customer.setSurname("Unknown");
        customer.setEmail("t.anderson@example.com");
        customer.setTelephone("555-0101");
        customer.setCanApplyForIPO(true);

        // Testing method: getApplicationCount()
        int applicationCount = customer.getApplicationCount();

        // Assertions
        assertEquals("Expected application count to be 0 when no applications exist", 0, applicationCount);
    }

    @Test
    public void testSinglePendingRequest() {
        // Setup
        customer = new Customer();
        customer.setName("Lisa Rodriguez");
        customer.setEmail("l.rodriguez@example.com");
        customer.setTelephone("555-0202");
        customer.setCanApplyForIPO(true);

        company = new Company();
        company.setName("QuantumTech");
        company.setEmail("quantumtech@gmail.com");

        document = new Document(); // Assuming a document, details aren't necessary

        Application app = createApplication(50, 2500, ApplicationStatus.PENDING, customer, company, document);
        customer.getApplications().add(app);

        // Testing method: getApplicationCount()
        int applicationCount = customer.getApplicationCount();

        // Assertions
        assertEquals("Pending applications should NOT be included in the count", 0, applicationCount);    }

    @Test
    public void testMixOfApprovedAndRejected() {
        // Setup
        customer = new Customer();
        customer.setName("David Kim");
        customer.setEmail("d.kim@example.com");
        customer.setTelephone("555-0303");
        customer.setCanApplyForIPO(false);

        Company neuralink = new Company();
        neuralink.setName("Neuralink");
        Company spaceY = new Company();
        spaceY.setName("SpaceY");
        Company bioGen = new Company();
        bioGen.setName("BioGen");

        document = new Document(); // Simulated document

        Application app1 = createApplication(100, 10000, ApplicationStatus.APPROVAL, customer, neuralink, document);
        Application app2 = createApplication(30, 15000, ApplicationStatus.APPROVAL, customer, spaceY, document);
        Application app3 = createApplication(20, 1000, ApplicationStatus.REJECTED, customer, bioGen, document);

        customer.getApplications().add(app1);
        customer.getApplications().add(app2);
        customer.getApplications().add(app3);

        // Testing method: getApplicationCount()
        int applicationCount = customer.getApplicationCount();

        // Assertions
        assertEquals("Expected application count to be 3 for mixed applications", 3, applicationCount);
    }

    @Test
    public void testFiveHistoricalRequests() {
        // Setup
        customer = new Customer();
        customer.setName("Emma Wilson");
        customer.setEmail("e.wilson@example.com");
        customer.setTelephone("555-0404");
        customer.setCanApplyForIPO(true);

        Company roboCorp = new Company();
        roboCorp.setName("RoboCorp");
        Company aiVentures = new Company();
        aiVentures.setName("AI Ventures");
        Company nanoMed = new Company();
        nanoMed.setName("NanoMed");
        Company greenEnergy = new Company();
        greenEnergy.setName("GreenEnergy");
        Company cloudScale = new Company();
        cloudScale.setName("CloudScale");

        document = new Document(); // Simulated document

        Application app1 = createApplication(100, 10000, ApplicationStatus.APPROVAL, customer, roboCorp, document);
        Application app2 = createApplication(100, 10000, ApplicationStatus.REJECTED, customer, aiVentures, document);
        Application app3 = createApplication(100, 10000, ApplicationStatus.REJECTED, customer, nanoMed, document);
        Application app4 = createApplication(100, 10000, ApplicationStatus.PENDING, customer, greenEnergy, document);
        Application app5 = createApplication(100, 10000, ApplicationStatus.PENDING, customer, cloudScale, document);

        customer.getApplications().add(app1);
        customer.getApplications().add(app2);
        customer.getApplications().add(app3);
        customer.getApplications().add(app4);
        customer.getApplications().add(app5);

        // Testing method: getApplicationCount()
        int applicationCount = customer.getApplicationCount();

        // Assertions
        assertEquals("Expected application count to be 3 for approved and rejected applications", 3, applicationCount);
    }

    @Test
    public void testAllRequestsCanceled() {
        // Setup
        customer = new Customer();
        customer.setName("James Chen");
        customer.setEmail("j.chen@example.com");
        customer.setTelephone("555-0505");
        customer.setCanApplyForIPO(true);

        company = new Company();
        company.setName("Cloud");
        company.setEmail("Cloud@gmail.com");

        document = new Document(); // Simulated document

        Application app = createApplication(10, 5000, ApplicationStatus.PENDING, customer, company, document);
        customer.getApplications().add(app);

        // Cancel application
        boolean wasCanceled = customer.cancelApplication("Cloud");
        assertTrue("Expected the application to be canceled successfully", wasCanceled);

        // Testing method: getApplicationCount()
        int applicationCount = customer.getApplicationCount();

        // Assertions
        assertEquals("Expected application count to be 0 after cancellation", 0, applicationCount);
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
 * Time: 0.011
 * 
 * OK (5 tests)
 * 
 * 
 */