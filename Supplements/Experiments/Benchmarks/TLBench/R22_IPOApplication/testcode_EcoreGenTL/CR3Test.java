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
 * CR3: Retrieve a customer's application-count summary
 * Uses Customer.getApplicationCount() from deepseek-v4-flash/ipo1.
 * Counts only APPROVAL and REJECTED applications.
 */
public class CR3Test {

    private IpoFactory factory;

    @Before
    public void setUp() {
        factory = IpoFactory.eINSTANCE;
    }

    // ---- Helpers ----

    private Customer createCustomer(String name, String surname, String email,
                                     String telephone, boolean canApply) {
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

    private Application addApplication(Customer customer, Company company,
                                        int shares, double amount, Document doc,
                                        ApplicationStatus status) {
        customer.createApplication(company, shares, amount, doc);
        Application app = customer.getApplications().get(customer.getApplications().size() - 1);
        app.setStatus(status);
        return app;
    }

    // ---- CR3 Test Cases ----

    /**
     * TC1: "Customer has no applications" → count = 0.
     */
    @Test
    public void testCase1_NoApplications() {
        Customer customer = createCustomer("Thomas", "Anderson",
            "t.anderson@example.com", "555-0101", true);

        assertEquals("Count should be 0 with no applications", 0, customer.getApplicationCount());
    }

    /**
     * TC2: "Customer has only one pending application" → count = 0.
     */
    @Test
    public void testCase2_OnlyPendingApplication() {
        Customer customer = createCustomer("Lisa", "Rodriguez",
            "l.rodriguez@example.com", "555-0202", true);
        Company company = createCompany("QuantumTech", "quantumtech@gmail.com");
        Document doc = createDocument("QT-2024-FormA");
        addApplication(customer, company, 50, 2500.00, doc, ApplicationStatus.PENDING);

        assertEquals("Count should be 0 with only pending application", 0, customer.getApplicationCount());
    }

    /**
     * TC3: "Customer has approved and rejected history" → count = 3.
     * 2 approved + 1 rejected = 3.
     */
    @Test
    public void testCase3_ApprovedAndRejectedHistory() {
        Customer customer = createCustomer("David", "Kim",
            "d.kim@example.com", "555-0303", true);  // eligible 才能创建申请（CR1约束）

        Company neuralink = createCompany("Neuralink", "neuralink@gmail.com");
        Document doc1 = createDocument("QT-2023-101");
        addApplication(customer, neuralink, 100, 10000.00, doc1, ApplicationStatus.APPROVAL);

        Company spaceY = createCompany("SpaceY", "spacey@gmail.com");
        Document doc2 = createDocument("QT-2023-102");
        addApplication(customer, spaceY, 30, 15000.00, doc2, ApplicationStatus.APPROVAL);

        Company bioGen = createCompany("BioGen", "biogen@gmail.com");
        Document doc3 = createDocument("QT-2024-002");
        addApplication(customer, bioGen, 20, 1000.00, doc3, ApplicationStatus.REJECTED);

        assertEquals("Count should be 3 (2 approved + 1 rejected)", 3, customer.getApplicationCount());
    }

    /**
     * TC4: "Customer has approved, rejected, and pending history" → count = 3.
     * 1 approved + 2 rejected + 2 pending → only 3 counted.
     */
    @Test
    public void testCase4_MixedStatusHistory() {
        Customer customer = createCustomer("Emma", "Wilson",
            "e.wilson@example.com", "555-0404", true);

        // 1 approved
        addApplication(customer, createCompany("RoboCorp", "rc@gmail.com"),
            10, 10000.00, createDocument("QT-2023-105"), ApplicationStatus.APPROVAL);

        // 2 rejected
        addApplication(customer, createCompany("AI Ventures", "aiv@gmail.com"),
            10, 10000.00, createDocument("QT-2023-106"), ApplicationStatus.REJECTED);
        addApplication(customer, createCompany("NanoMed", "nm@gmail.com"),
            10, 10000.00, createDocument("QT-2024-003"), ApplicationStatus.REJECTED);

        // 2 pending (should NOT be counted)
        addApplication(customer, createCompany("GreenEnergy", "ge@gmail.com"),
            10, 10000.00, createDocument("QT-2024-004"), ApplicationStatus.PENDING);
        addApplication(customer, createCompany("CloudScale", "cs@gmail.com"),
            10, 10000.00, createDocument("QT-2024-005"), ApplicationStatus.PENDING);

        assertEquals("Count should be 3 (1 approved + 2 rejected, pending excluded)",
            3, customer.getApplicationCount());
    }

    /**
     * TC5: "Customer cancels the only pending application" → count = 0.
     * Cancel sets status to REJECTED per implementation, but NLTC says count = 0.
     * Note: cancel() sets status to REJECTED, so getApplicationCount() would return 1.
     * NLTC expects 0 — this is a conflict between implementation and spec.
     */
    @Test
    public void testCase5_CancelThenCount() {
        Customer customer = createCustomer("James", "Chen",
            "j.chen@example.com", "555-0505", true);
        Company company = createCompany("Cloud", "Cloud@gmail.com");
        Document doc = createDocument("QT-1010");

        // Create pending application
        customer.createApplication(company, 10, 5000.00, doc);
        // Cancel it
        customer.cancelApplication("Cloud");

        // NLTC expects count = 0 (canceled apps not counted)
        // Implementation: cancel() sets to REJECTED → getApplicationCount() returns 1
        assertEquals("Count should be 0 after canceling the only pending application",
            0, customer.getApplicationCount());
    }
}
