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
 * CR4: Query total approved IPO application amount for a customer
 * Uses Customer.getApprovedTotalAmount() from deepseek-v4-flash/ipo1.
 * Sums amountOfMoney for all APPROVAL applications only.
 */
public class CR4Test {

    private IpoFactory factory;

    @Before
    public void setUp() {
        factory = IpoFactory.eINSTANCE;
    }

    // ---- Helpers ----

    private Customer createCustomer(String name, String surname, String email, String telephone) {
        Customer c = factory.createCustomer();
        c.setName(name);
        c.setSurname(surname);
        c.setEmail(email);
        c.setTelephone(telephone);
        c.setCanApplyForIPO(true);
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

    private void addApplication(Customer customer, Company company,
                                 int shares, double amount, Document doc,
                                 ApplicationStatus status) {
        customer.createApplication(company, shares, amount, doc);
        Application app = customer.getApplications().get(customer.getApplications().size() - 1);
        app.setStatus(status);
    }

    // ---- CR4 Test Cases ----

    /**
     * TC1: "Customer has no approved applications" → total = 0.00.
     * Only pending and rejected records.
     */
    @Test
    public void testCase1_NoApprovedApplications() {
        Customer customer = createCustomer("Emily", "Chen",
            "e.chen@example.com", "555-1212");

        addApplication(customer, createCompany("TechInc", "ti@gmail.com"),
            10, 1500.00, createDocument("QT-3001"), ApplicationStatus.PENDING);
        addApplication(customer, createCompany("BioMed", "bm@gmail.com"),
            10, 2000.00, createDocument("QT-3002"), ApplicationStatus.REJECTED);

        assertEquals("Total should be 0.00 with no approved applications",
            0.00, customer.getApprovedTotalAmount(), 0.01);
    }

    /**
     * TC2: "Customer has one approved application" → total = 4200.00.
     */
    @Test
    public void testCase2_OneApprovedApplication() {
        Customer customer = createCustomer("Robert", "Johnson",
            "r.johnson@example.com", "555-2323");

        addApplication(customer, createCompany("SolarMax", "sm@gmail.com"),
            84, 4200.00, createDocument("SM-2024-Q1"), ApplicationStatus.APPROVAL);

        assertEquals("Total should be 4200.00", 4200.00, customer.getApprovedTotalAmount(), 0.01);
    }

    /**
     * TC3: "Customer has approved applications from different companies" → total = 5500.00.
     */
    @Test
    public void testCase3_MultipleApprovedFromDifferentCompanies() {
        Customer customer = createCustomer("Sophia", "Williams",
            "s.williams@example.com", "555-3434");

        addApplication(customer, createCompany("QuantumTech", "qt@gmail.com"),
            40, 2000.00, createDocument("SM-2024-Q3004"), ApplicationStatus.APPROVAL);
        addApplication(customer, createCompany("Neuralink", "nl@gmail.com"),
            70, 3500.00, createDocument("SM-2024-Q3005"), ApplicationStatus.APPROVAL);

        assertEquals("Total should be 5500.00", 5500.00, customer.getApprovedTotalAmount(), 0.01);
    }

    /**
     * TC4: "Customer has a large approved portfolio" → total = 50000.00.
     * 5 approved applications each with amount 10000.00.
     */
    @Test
    public void testCase4_LargeApprovedPortfolio() {
        Customer customer = createCustomer("James", "Wilson",
            "j.wilson@vip.example.com", "555-4545");

        addApplication(customer, createCompany("TechGiant", "tg@gmail.com"),
            10, 10000.00, createDocument("SM-3006"), ApplicationStatus.APPROVAL);
        addApplication(customer, createCompany("AutoFuture", "af@gmail.com"),
            10, 10000.00, createDocument("SM-3007"), ApplicationStatus.APPROVAL);
        addApplication(customer, createCompany("AeroSpace", "as@gmail.com"),
            10, 10000.00, createDocument("SM-3008"), ApplicationStatus.APPROVAL);
        addApplication(customer, createCompany("BioGenius", "bg@gmail.com"),
            10, 10000.00, createDocument("SM-3009"), ApplicationStatus.APPROVAL);
        addApplication(customer, createCompany("GreenEnergy", "ge@gmail.com"),
            10, 10000.00, createDocument("SM-3010"), ApplicationStatus.APPROVAL);

        assertEquals("Total should be 50000.00", 50000.00, customer.getApprovedTotalAmount(), 0.01);
    }

    /**
     * TC5: "Approved applications are summed while pending ones are ignored" → total = 8750.00.
     * 3 approved (3000 + 2750 + 3000) + 2 pending (600 + 600 ignored).
     */
    @Test
    public void testCase5_ApprovedSummedPendingIgnored() {
        Customer customer = createCustomer("Olivia", "Brown",
            "o.brown@example.com", "555-5656");

        // 3 approved
        addApplication(customer, createCompany("CloudServ", "cs@gmail.com"),
            10, 3000.00, createDocument("SM-3011"), ApplicationStatus.APPROVAL);
        addApplication(customer, createCompany("DataCore", "dc@gmail.com"),
            10, 2750.00, createDocument("SM-3012"), ApplicationStatus.APPROVAL);
        addApplication(customer, createCompany("AI Ventures", "aiv@gmail.com"),
            10, 3000.00, createDocument("SM-3013"), ApplicationStatus.APPROVAL);

        // 2 pending (should be ignored)
        addApplication(customer, createCompany("NanoTech", "nt@gmail.com"),
            10, 600.00, createDocument("SM-3014"), ApplicationStatus.PENDING);
        addApplication(customer, createCompany("RoboWorks", "rw@gmail.com"),
            10, 600.00, createDocument("SM-3015"), ApplicationStatus.PENDING);

        assertEquals("Total should be 8750.00 (pending excluded)",
            8750.00, customer.getApprovedTotalAmount(), 0.01);
    }
}
