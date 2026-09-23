
import static org.junit.Assert.*;
import org.junit.Test;
import java.util.List;
import java.util.ArrayList;

public class CR1_IPOApplicationTest {

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
    public void testStandardEligibleSubmission() {
        Customer customer = new Customer();
        customer.setName("John");
        customer.setSurname("Smith");
        customer.setEmail("john.smith@example.com");
        customer.setTelephone("555-1234");
        customer.setCanApplyForIPO(true);

        Company company = new Company();
        company.setName("TechCorp");
        company.setEmail("techcorp@gmail.com");

        Document document = new Document();

        boolean result = customer.createApplication(company, 100, 5000.0, document);

        assertTrue("Application should be successfully created when eligible.", result);
        assertEquals("Expected application count mismatch.", 1, customer.getApplications().size());
    }

    @Test
    public void testCustomerNotEligible() {
        Customer customer = new Customer();
        customer.setName("Alice");
        customer.setSurname("Johnson");
        customer.setEmail("alice.j@example.com");
        customer.setTelephone("555-5678");
        customer.setCanApplyForIPO(false);

        Company company = new Company();
        company.setName("BioMed");
        company.setEmail("biomed@gmail.com");

        Document document = new Document();

        boolean result = customer.createApplication(company, 50, 2500.0, document);

        assertFalse("Application should be rejected when customer is not eligible.", result);
        assertEquals("Expected application count mismatch.", 0, customer.getApplications().size());
    }

    @Test
    public void testDuplicateApprovedApplication() {
        Customer customer = new Customer();
        customer.setName("Robert");
        customer.setSurname("Chen");
        customer.setEmail("r.chen@example.com");
        customer.setTelephone("555-9012");
        customer.setCanApplyForIPO(true);

        Company approvedCompany = new Company();
        approvedCompany.setName("GreenEnergy");
        approvedCompany.setEmail("greenenergy@gmail.com");

        Company requestedCompany = new Company();
        requestedCompany.setName("GreenEnergy");
        requestedCompany.setEmail("greenenergy@gmail.com");

        Document document = new Document();
        document.setName("G");

        // Create and approve an initial application
        Application approvedApplication = createApplication(10, 300.0, ApplicationStatus.APPROVAL, customer,
                approvedCompany,
                document);
        customer.getApplications().add(approvedApplication);

        boolean result = customer.createApplication(requestedCompany, 10, 300.0, document);

        assertFalse("Application should be rejected for duplicate approved applications.", result);
        assertEquals("Expected application count mismatch.", 1, customer.getApplications().size());
    }

    @Test
    public void testMissingDocument() {
        Customer customer = new Customer();
        customer.setName("Emma");
        customer.setSurname("Davis");
        customer.setEmail("emma.d@example.com");
        customer.setTelephone("555-3456");
        customer.setCanApplyForIPO(true);

        Company company = new Company();
        company.setName("AutoFuture");
        company.setEmail("autofuture@gmail.com");

        boolean result = customer.createApplication(company, 25, 1000.0, null); // Missing document

        assertFalse("Application should be rejected when document is missing.", result);
        assertEquals("Expected application count mismatch.", 0, customer.getApplications().size());
    }

    @Test
    public void testZeroShareApplication() {
        Customer customer = new Customer();
        customer.setName("James");
        customer.setSurname("Wilson");
        customer.setEmail("j.wilson@example.com");
        customer.setTelephone("555-7890");
        customer.setCanApplyForIPO(true);

        Company company = new Company();
        company.setName("NanoChip");
        company.setEmail("nanotech@gmail.com");

        Document document = new Document();

        boolean result = customer.createApplication(company, 0, 0.0, document);

        assertFalse("Application should be rejected for zero shares/amount.", result);
        assertEquals("Expected application count mismatch.", 0, customer.getApplications().size());
    }

    @Test
    public void testNegativeShareCount() {
        Customer customer = new Customer();
        customer.setName("Sophia");
        customer.setSurname("Martinez");
        customer.setEmail("s.m@example.com");
        customer.setTelephone("555-2345");
        customer.setCanApplyForIPO(true);

        Company company = new Company();
        company.setName("CloudServ");
        company.setEmail("cloudserv@gmail.com");

        Document document = new Document();

        boolean result = customer.createApplication(company, -5, -200.0, document);

        assertFalse("Application should be rejected for negative shares/amount.", result);
        assertEquals("Expected application count mismatch.", 0, customer.getApplications().size());
    }

}

/*
 * compile_result:
 * 
 * 
 * 
 * run_result:
 * JUnit version 4.13.2
 * ......
 * Time: 0.012
 * 
 * OK (6 tests)
 * 
 * 
 */
