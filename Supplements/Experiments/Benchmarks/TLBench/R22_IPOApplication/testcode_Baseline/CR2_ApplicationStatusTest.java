
import static org.junit.Assert.*;
import org.junit.Test;
import java.util.*;

// Other required imports

public class CR2_ApplicationStatusTest {
    private Customer customer;
    private Company company;
    private Document document;
    private Application application;

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
    public void testApprovePendingRequest() {
        // Setup
        customer = new Customer();
        customer.setName("Michael Brown");
        customer.setSurname("Brown");
        customer.setEmail("m.brown@example.com");
        customer.setTelephone("555-1122");
        customer.setCanApplyForIPO(true);

        company = new Company();
        company.setName("SolarMax");
        company.setEmail("solarmax@gmail.com");

        document = new Document(); // Create document 'S' as needed for compatibility

        application = createApplication(10, 200.0, ApplicationStatus.PENDING, customer, company, document);
        customer.getApplications().add(application);

        // Test logic
        boolean result = application.approve();

        // Assertions
        assertTrue("Approval status should change to APPROVAL", result);
        assertEquals("Status should be APPROVAL", ApplicationStatus.APPROVAL, application.getStatus());
        assertEquals("Email count mismatch", 2, application.getEmails().size());

        Email customerEmail = application.getEmails().get(0);
        Email companyEmail = application.getEmails().get(1);

        assertEquals("Customer email receiver mismatch", "m.brown@example.com", customerEmail.getReceiver());
        assertEquals("Company email receiver mismatch", "solarmax@gmail.com", companyEmail.getReceiver());
    }

    @Test
    public void testRejectPendingRequest() {
        // Setup
        customer = new Customer();
        customer.setName("Olivia Lee");
        customer.setSurname("Lee");
        customer.setEmail("olivia.l@example.com");
        customer.setTelephone("555-3344");
        customer.setCanApplyForIPO(true);

        company = new Company();
        company.setName("HealthPlus");
        company.setEmail("healthplus@gmail.com");

        document = new Document(); // Create document 'H' as needed for compatibility

        application = createApplication(10, 5000.0, ApplicationStatus.PENDING, customer, company, document);
        customer.getApplications().add(application);

        // Test logic
        boolean result = application.reject();

        // Assertions
        assertTrue("Rejection status should change to REJECTED", result);
        assertEquals("Status should be REJECTED", ApplicationStatus.REJECTED, application.getStatus());
        assertEquals("Email count mismatch", 1, application.getEmails().size());

        Email rejectionEmail = application.getEmails().get(0);
        assertEquals("Rejection email receiver mismatch", "olivia.l@example.com", rejectionEmail.getReceiver());
        assertTrue("Rejection email should mention the customer name", rejectionEmail.getContent().contains("Olivia"));
        assertTrue("Rejection email should mention the customer surname", rejectionEmail.getContent().contains("Lee"));
        assertTrue("Rejection email should mention the customer email", rejectionEmail.getContent().contains("olivia.l@example.com"));
        assertTrue("Rejection email should mention the customer telephone", rejectionEmail.getContent().contains("555-3344"));
        assertTrue("Rejection email should mention the company name", rejectionEmail.getContent().contains("HealthPlus"));
        assertTrue("Rejection email should mention the purchased shares", rejectionEmail.getContent().contains("10"));
        assertTrue("Rejection email should mention the paid amount", rejectionEmail.getContent().contains("5000.00"));
    }

    @Test
    public void testApproveAlreadyApprovedRecord() {
        // Setup
        customer = new Customer();
        customer.setName("Daniel Kim");
        customer.setSurname("Kim");
        customer.setEmail("d.kim@example.com");
        customer.setTelephone("555-5566");
        customer.setCanApplyForIPO(true);

        company = new Company();
        company.setName("HealthPlus");
        company.setEmail("healthplus@gmail.com");

        document = new Document(); // Create document 'H' as needed for compatibility

        application = createApplication(10, 5000.0, ApplicationStatus.APPROVAL, customer, company, document);
        customer.getApplications().add(application);

        // Test logic
        boolean result = application.approve();

        // Assertions
        assertFalse("Should not be able to approve an already approved application", result);
        assertEquals("Status should remain APPROVAL", ApplicationStatus.APPROVAL, application.getStatus());
    }

    @Test
    public void testRejectAlreadyRejectedRecord() {
        // Setup
        customer = new Customer();
        customer.setName("Sophie Zhang");
        customer.setSurname("Zhang");
        customer.setEmail("s.zhang@example.com");
        customer.setTelephone("555-7788");
        customer.setCanApplyForIPO(true);

        company = new Company();
        company.setName("Health");
        company.setEmail("health@gmail.com");

        document = new Document(); // Document handling for rejection reason

        application = createApplication(10, 5000.0, ApplicationStatus.REJECTED, customer, company, document);
        customer.getApplications().add(application);

        // Test logic
        boolean result = application.reject();

        // Assertions
        assertFalse("Should not be able to reject an already rejected application", result);
        assertEquals("Status should remain REJECTED", ApplicationStatus.REJECTED, application.getStatus());
    }

    @Test
    public void testApproveRecordTiedToIneligibleCustomer() {
        // Setup
        customer = new Customer();
        customer.setName("William Wang");
        customer.setSurname("Wang");
        customer.setEmail("will.w@example.com");
        customer.setTelephone("555-9900");
        customer.setCanApplyForIPO(false); // Customer ineligible

        company = new Company();
        company.setName("Cloud");
        company.setEmail("Cloud@gmail.com");

        document = new Document(); // Document needed for pending status

        application = createApplication(10, 5000.0, ApplicationStatus.PENDING, customer, company, document);
        customer.getApplications().add(application);

        // Test logic
        boolean result = application.approve();

        // Assertions
        assertFalse("Should not approve application for ineligible customer", result);
        assertEquals("Status should remain PENDING", ApplicationStatus.PENDING, application.getStatus());
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
 * Time: 0.034
 * 
 * OK (5 tests)
 * 
 * 
 */
