package edu.ipo.test;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import edu.ipo.IpoFactory;
import edu.ipo.Customer;
import edu.ipo.Company;
import edu.ipo.Document;
import edu.ipo.Application;
import edu.ipo.ApplicationStatus;

/**
 * CR1: Create an IPO application
 * Uses Customer.createApplication(Company, int, double, Document) from deepseek-v4-flash/ipo1.
 */
public class CR1Test {

    private IpoFactory factory;

    @Before
    public void setUp() {
        factory = IpoFactory.eINSTANCE;
    }

    // ---- Helpers ----

    private Customer createCustomer(String id, String name, String surname,
                                     String email, String telephone, boolean canApply) {
        Customer c = factory.createCustomer();
        c.setName(name);
        c.setSurname(surname);
        c.setEmail(email);
        c.setTelephone(telephone);
        c.setCanApplyForIPO(canApply);
        return c;
    }

    private Company createCompany(String name, String email) {
        Company c = factory.createCompany();
        c.setName(name);
        c.setEmail(email);
        return c;
    }

    private Document createDocument(String name) {
        Document d = factory.createDocument();
        d.setName(name);
        return d;
    }

    // ---- CR1 Test Cases ----

    /**
     * TC1: "Eligible customer submits a standard IPO application"
     * Customer C001 eligible, company TechCorp, document A, positive shares/amount.
     * Expected: true
     */
    @Test
    public void testCase1_EligibleStandardApplication() {
        Customer customer = createCustomer("C001", "John", "Smith",
            "john.smith@example.com", "555-1234", true);
        Company company = createCompany("TechCorp", "techcorp@gmail.com");
        Document doc = createDocument("A");

        boolean result = customer.createApplication(company, 10, 500.00, doc);

        assertTrue("Application should be created successfully", result);
        assertEquals("Customer should have 1 application", 1, customer.getApplications().size());
        Application app = customer.getApplications().get(0);
        assertEquals("Status should be PENDING", ApplicationStatus.PENDING, app.getStatus());
        assertEquals("Company should be TechCorp", "TechCorp", app.getCompany().getName());
    }

    /**
     * TC2: "Ineligible customer tries to apply"
     * Customer C002 with canApplyForIPO = false.
     * Expected: false
     */
    @Test
    public void testCase2_IneligibleCustomer() {
        Customer customer = createCustomer("C002", "Alice", "Johnson",
            "alice.j@example.com", "555-5678", false);
        Company company = createCompany("BioMed", "biomed@gmail.com");
        Document doc = createDocument("B");

        boolean result = customer.createApplication(company, 10, 500.00, doc);

        assertFalse("Ineligible customer should not create application", result);
    }

    /**
     * TC3: "Customer repeats an already approved company application"
     * Customer C003 has approved application for GreenEnergy, tries to apply again.
     * Expected: false
     */
    @Test
    public void testCase3_RepeatApprovedCompany() {
        Customer customer = createCustomer("C003", "Robert", "Chen",
            "r.chen@example.com", "555-9012", true);
        Company company1 = createCompany("GreenEnergy", "greenenergy@gmail.com");
        Document docG = createDocument("G");

        // Create first application and manually set to APPROVAL
        customer.createApplication(company1, 10, 300.00, docG);
        customer.getApplications().get(0).setStatus(ApplicationStatus.APPROVAL);

        // Try to create another application for the same company name
        Company company2 = createCompany("GreenEnergy", "greenenergy@gmail.com");
        boolean result = customer.createApplication(company2, 5, 200.00, docG);

        assertFalse("Should not allow duplicate approved company application", result);
    }

    /**
     * TC4: "Application is submitted without a document"
     * Customer C004 eligible, no document provided.
     * Note: Implementation does NOT check for null document, so it returns true.
     * NLTC expects false, but implementation creates the application regardless.
     * Expected per NLTC: false (test documents the expected behavior per spec)
     */
    @Test
    public void testCase4_NoDocument() {
        Customer customer = createCustomer("C004", "Emma", "Davis",
            "emma.d@example.com", "555-3456", true);
        Company company = createCompany("AutoFuture", "autofuture@gmail.com");

        boolean result = customer.createApplication(company, 10, 500.00, null);

        // NLTC expects false; implementation does not validate null document → returns true
        assertFalse("Should reject application without document", result);
    }

    /**
     * TC5: "Application uses zero shares and zero amount"
     * Customer C005 eligible, shares=0, amount=0.
     * Note: Implementation does NOT validate shares > 0 or amount > 0.
     * NLTC expects false, but implementation creates the application.
     * Expected per NLTC: false
     */
    @Test
    public void testCase5_ZeroSharesAndAmount() {
        Customer customer = createCustomer("C005", "James", "Wilson",
            "j.wilson@example.com", "555-7890", true);
        Company company = createCompany("NanoChip", "nanotech@gmail.com");
        Document doc = createDocument("N");

        boolean result = customer.createApplication(company, 0, 0.0, doc);

        // NLTC expects false; implementation does not validate shares/amount → returns true
        assertFalse("Should reject application with zero shares and amount", result);
    }

    /**
     * TC6: "Application uses negative shares and negative amount"
     * Customer C006 eligible, shares=-5, amount=-100.
     * Note: Implementation does NOT validate positive values.
     * NLTC expects false, but implementation creates the application.
     * Expected per NLTC: false
     */
    @Test
    public void testCase6_NegativeSharesAndAmount() {
        Customer customer = createCustomer("C006", "Sophia", "Martinez",
            "s.m@example.com", "555-2345", true);
        Company company = createCompany("CloudServ", "cloudserv@gmail.com");
        Document doc = createDocument("C");

        boolean result = customer.createApplication(company, -5, -100.0, doc);

        // NLTC expects false; implementation does not validate → returns true
        assertFalse("Should reject application with negative shares and amount", result);
    }
}
