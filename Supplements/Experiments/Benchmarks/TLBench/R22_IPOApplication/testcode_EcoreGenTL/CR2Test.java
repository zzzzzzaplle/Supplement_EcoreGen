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
 * CR2: Approve or reject an application
 * Uses Application.approve() and Application.reject() from deepseek-v4-flash/ipo1.
 */
public class CR2Test {

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

    private Application createPendingApplication(Customer customer, Company company,
                                                  int shares, double amount, Document doc) {
        customer.createApplication(company, shares, amount, doc);
        return customer.getApplications().get(customer.getApplications().size() - 1);
    }

    // ---- CR2 Test Cases ----

    /**
     * TC1: "Bank approves a pending application for an eligible customer"
     * Eligible customer, pending application → approve → true, status=APPROVAL, 2 emails.
     */
    @Test
    public void testCase1_ApprovePending() {
        Customer customer = createCustomer("Michael", "Brown",
            "m.brown@example.com", "555-1122", true);
        Company company = createCompany("SolarMax", "solarmax@gmail.com");
        Document doc = createDocument("S");
        Application app = createPendingApplication(customer, company, 10, 200.00, doc);

        boolean result = app.approve();

        assertTrue("Approval should succeed", result);
        assertEquals("Status should be APPROVAL", ApplicationStatus.APPROVAL, app.getStatus());
        assertEquals("Should have 2 emails (customer + company)", 2, app.getEmails().size());
    }

    /**
     * TC2: "Bank rejects a pending application"
     * Pending application → reject → true, status=REJECTED, 1 rejection email.
     */
    @Test
    public void testCase2_RejectPending() {
        Customer customer = createCustomer("Olivia", "Lee",
            "olivia.l@example.com", "555-3344", true);
        Company company = createCompany("HealthPlus", "healthplus@gmail.com");
        Document doc = createDocument("H");
        Application app = createPendingApplication(customer, company, 10, 5000.00, doc);

        boolean result = app.reject();

        assertTrue("Rejection should succeed", result);
        assertEquals("Status should be REJECTED", ApplicationStatus.REJECTED, app.getStatus());
        assertEquals("Should have 1 rejection email", 1, app.getEmails().size());
        // Verify rejection email content includes required fields
        String content = app.getEmails().get(0).getContent();
        assertTrue("Email should contain customer name", content.contains("Olivia"));
        assertTrue("Email should contain company name", content.contains("HealthPlus"));
    }

    /**
     * TC3: "Bank re-approves an already approved application"
     * Already APPROVAL status → approve → false, stays APPROVAL.
     */
    @Test
    public void testCase3_ReApproveAlreadyApproved() {
        Customer customer = createCustomer("Daniel", "Kim",
            "d.kim@example.com", "555-5566", true);
        Company company = createCompany("HealthPlus", "healthplus@gmail.com");
        Document doc = createDocument("H");
        Application app = createPendingApplication(customer, company, 10, 5000.00, doc);
        app.setStatus(ApplicationStatus.APPROVAL);

        boolean result = app.approve();

        assertFalse("Re-approval should fail", result);
        assertEquals("Status should remain APPROVAL", ApplicationStatus.APPROVAL, app.getStatus());
    }

    /**
     * TC4: "Bank re-rejects an already rejected application"
     * Already REJECTED status → reject → false, stays REJECTED.
     */
    @Test
    public void testCase4_ReRejectAlreadyRejected() {
        Customer customer = createCustomer("Sophie", "Zhang",
            "s.zhang@example.com", "555-7788", true);
        Company company = createCompany("Health", "health@gmail.com");
        Document doc = createDocument("doc");
        Application app = createPendingApplication(customer, company, 10, 5000.00, doc);
        app.setStatus(ApplicationStatus.REJECTED);

        boolean result = app.reject();

        assertFalse("Re-rejection should fail", result);
        assertEquals("Status should remain REJECTED", ApplicationStatus.REJECTED, app.getStatus());
    }

    /**
     * TC5: "Bank tries to approve after customer becomes ineligible"
     * Customer canApplyForIPO=false, pending application → approve → false, stays PENDING.
     */
    @Test
    public void testCase5_ApproveAfterIneligible() {
        // CR.md #2: "Approval is allowed only when the related customer is still eligible"
        // 正确的时间线：先 eligible 创建 pending 申请 → 再变为 ineligible → approve 应失败
        Customer customer = createCustomer("William", "Wang",
            "will.w@example.com", "555-9900", true);  // 初始 eligible
        Company company = createCompany("Cloud", "Cloud@gmail.com");
        Document doc = createDocument("P");
        Application app = createPendingApplication(customer, company, 10, 5000.00, doc);

        // 申请创建后，客户变为 ineligible
        customer.setCanApplyForIPO(false);

        boolean result = app.approve();

        assertFalse("Approval should fail for ineligible customer", result);
        assertEquals("Status should remain PENDING", ApplicationStatus.PENDING, app.getStatus());
    }
}
