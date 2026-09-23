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
 * CR5: Cancel a pending application
 * Uses Customer.cancelApplication(String companyName) from deepseek-v4-flash/ipo1.
 * Cancels only PENDING applications; APPROVAL and REJECTED cannot be canceled.
 */
public class CR5Test {

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
        if (status != ApplicationStatus.PENDING) {
            app.setStatus(status);
        }
        return app;
    }

    // ---- CR5 Test Cases ----

    /**
     * TC1: "Customer cancels a pending application" → true.
     */
    @Test
    public void testCase1_CancelPending() {
        Customer customer = createCustomer("Benjamin", "Taylor",
            "b.taylor@example.com", "555-1010", true);
        Company company = createCompany("EcoWave", "ecowave@gmail.com");
        Document doc = createDocument("EW-2024-03");
        addApplication(customer, company, 15, 750.00, doc, ApplicationStatus.PENDING);

        boolean result = customer.cancelApplication("EcoWave");

        assertTrue("Canceling pending application should succeed", result);
    }

    /**
     * TC2: "Customer tries to cancel an approved application" → false.
     */
    @Test
    public void testCase2_CancelApproved() {
        Customer customer = createCustomer("Charlotte", "Lee",
            "c.lee@example.com", "555-2020", true);
        Company company = createCompany("SmartGrid", "smartgrid@business.com");
        Document doc = createDocument("SG-2024-01");
        addApplication(customer, company, 30, 3000.00, doc, ApplicationStatus.APPROVAL);

        boolean result = customer.cancelApplication("SmartGrid");

        assertFalse("Canceling approved application should fail", result);
    }

    /**
     * TC3: "Customer tries to cancel a rejected application" → false.
     */
    @Test
    public void testCase3_CancelRejected() {
        Customer customer = createCustomer("Lucas", "Martin",
            "l.martin@example.com", "555-3030", true);
        Company company = createCompany("MedLife", "medlife@health.com");
        Document doc = createDocument("SG-2024-03");
        addApplication(customer, company, 20, 1000.00, doc, ApplicationStatus.REJECTED);

        boolean result = customer.cancelApplication("MedLife");

        assertFalse("Canceling rejected application should fail", result);
    }

    /**
     * TC4: "Customer tries to cancel a company with no matching application" → false.
     */
    @Test
    public void testCase4_CancelNonExistentCompany() {
        Customer customer = createCustomer("Amelia", "Clark",
            "a.clark@example.com", "555-4040", true);

        boolean result = customer.cancelApplication("UnknownCorp");

        assertFalse("Canceling non-existent company should fail", result);
    }

    /**
     * TC5: "Cancel one pending application without affecting another" → true for UrbanTech.
     * AgroSeed application should remain PENDING.
     */
    @Test
    public void testCase5_CancelOneKeepsOther() {
        Customer customer = createCustomer("Mia", "Anderson",
            "m.anderson@example.com", "555-6060", true);

        Company urbanTech = createCompany("UrbanTech", "urbantech@innovate.com");
        Document docUT = createDocument("SG-2024-005");
        Application urbanApp = addApplication(customer, urbanTech, 25, 1250.00, docUT,
            ApplicationStatus.PENDING);

        Company agroSeed = createCompany("AgroSeed", "agroseed@agro.com");
        Document docAS = createDocument("SG-2024-006");
        Application agroApp = addApplication(customer, agroSeed, 40, 2000.00, docAS,
            ApplicationStatus.PENDING);

        // Cancel UrbanTech
        boolean result = customer.cancelApplication("UrbanTech");

        assertTrue("Canceling UrbanTech should succeed", result);
        assertEquals("AgroSeed should remain PENDING",
            ApplicationStatus.PENDING, agroApp.getStatus());
    }
}
