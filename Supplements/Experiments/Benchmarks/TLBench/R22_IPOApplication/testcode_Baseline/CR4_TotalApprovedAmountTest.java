
import static org.junit.Assert.*;
import org.junit.Test;
import java.util.List;
import java.util.ArrayList;

public class CR4_TotalApprovedAmountTest {

    private Customer customer;

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
    public void testNoApprovedRequests() {
        // Setup
        customer = new Customer();
        customer.setName("Emily");
        customer.setSurname("Chen");
        customer.setEmail("e.chen@example.com");
        customer.setTelephone("555-1212");
        customer.setCanApplyForIPO(true);

        Company techInc = new Company();
        techInc.setName("TechInc");

        Company bioMed = new Company();
        bioMed.setName("BioMed");

        Document doc1 = new Document(); // assuming Document setup
        Document doc2 = new Document(); // assuming Document setup

        Application app1 = createApplication(10, 1500.0, ApplicationStatus.PENDING, customer, techInc, doc1);
        Application app2 = createApplication(10, 2000.0, ApplicationStatus.REJECTED, customer, bioMed, doc2);

        customer.getApplications().add(app1);
        customer.getApplications().add(app2);

        // Testing: getApprovedTotalAmount()
        double approvedTotalAmount = customer.getApprovedTotalAmount();

        // Assertion
        assertEquals("No approved requests", 0.0, approvedTotalAmount, 0.01);
    }

    @Test
    public void testSingleApproval() {
        // Setup
        customer = new Customer();
        customer.setName("Robert");
        customer.setSurname("Johnson");
        customer.setEmail("r.johnson@example.com");
        customer.setTelephone("555-2323");

        Company solarMax = new Company();
        solarMax.setName("SolarMax");

        Document doc = new Document(); // assuming Document setup

        Application app = createApplication(84, 4200.0, ApplicationStatus.APPROVAL, customer, solarMax, doc);

        customer.getApplications().add(app);

        // Testing: getApprovedTotalAmount()
        double approvedTotalAmount = customer.getApprovedTotalAmount();

        // Assertion
        assertEquals("Single approval request failed", 4200.0, approvedTotalAmount, 0.01);
    }

    @Test
    public void testMultipleApprovalsDifferentFirms() {
        // Setup
        customer = new Customer();
        customer.setName("Sophia");
        customer.setSurname("Williams");
        customer.setEmail("s.williams@example.com");
        customer.setTelephone("555-3434");

        Company quantumTech = new Company();
        quantumTech.setName("QuantumTech");

        Company neuralink = new Company();
        neuralink.setName("Neuralink");

        Document doc1 = new Document(); // assuming Document setup
        Document doc2 = new Document(); // assuming Document setup

        Application app1 = createApplication(40, 2000.0, ApplicationStatus.APPROVAL, customer, quantumTech, doc1);
        Application app2 = createApplication(70, 3500.0, ApplicationStatus.APPROVAL, customer, neuralink, doc2);

        customer.getApplications().add(app1);
        customer.getApplications().add(app2);

        // Testing: getApprovedTotalAmount()
        double approvedTotalAmount = customer.getApprovedTotalAmount();

        // Assertion
        assertEquals("Multiple approvals request failed", 5500.0, approvedTotalAmount, 0.01);
    }

    @Test
    public void testLargePortfolio() {
        // Setup
        customer = new Customer();
        customer.setName("James");
        customer.setSurname("Wilson");
        customer.setEmail("j.wilson@vip.example.com");
        customer.setTelephone("555-4545");

        Company techGiant = new Company();
        techGiant.setName("TechGiant");

        Company autoFuture = new Company();
        autoFuture.setName("AutoFuture");

        Company aeroSpace = new Company();
        aeroSpace.setName("AeroSpace");

        Company bioGenius = new Company();
        bioGenius.setName("BioGenius");

        Company greenEnergy = new Company();
        greenEnergy.setName("GreenEnergy");

        Document doc1 = new Document(); // Assuming Document setup
        Document doc2 = new Document(); // Assuming Document setup
        Document doc3 = new Document(); // Assuming Document setup
        Document doc4 = new Document(); // Assuming Document setup
        Document doc5 = new Document(); // Assuming Document setup

        Application app1 = createApplication(200, 10000.0, ApplicationStatus.APPROVAL, customer, techGiant, doc1);
        Application app2 = createApplication(250, 10000.0, ApplicationStatus.APPROVAL, customer, autoFuture, doc2);
        Application app3 = createApplication(125, 10000.0, ApplicationStatus.APPROVAL, customer, aeroSpace, doc3);
        Application app4 = createApplication(500, 10000.0, ApplicationStatus.APPROVAL, customer, bioGenius, doc4);
        Application app5 = createApplication(200, 10000.0, ApplicationStatus.APPROVAL, customer, greenEnergy, doc5);

        customer.getApplications().add(app1);
        customer.getApplications().add(app2);
        customer.getApplications().add(app3);
        customer.getApplications().add(app4);
        customer.getApplications().add(app5);

        // Testing: getApprovedTotalAmount()
        double approvedTotalAmount = customer.getApprovedTotalAmount();

        // Assertion
        assertEquals("Large portfolio request failed", 50000.0, approvedTotalAmount, 0.01);
    }

    @Test
    public void testApprovalsPlusPending() {
        // Setup
        customer = new Customer();
        customer.setName("Olivia");
        customer.setSurname("Brown");
        customer.setEmail("o.brown@example.com");
        customer.setTelephone("555-5656");

        Company cloudServ = new Company();
        cloudServ.setName("CloudServ");

        Company dataCore = new Company();
        dataCore.setName("DataCore");

        Company aiVentures = new Company();
        aiVentures.setName("AI Ventures");

        Company nanoTech = new Company();
        nanoTech.setName("NanoTech");

        Company roboWorks = new Company();
        roboWorks.setName("RoboWorks");

        Document doc1 = new Document(); // Assuming Document setup
        Document doc2 = new Document(); // Assuming Document setup
        Document doc3 = new Document(); // Assuming Document setup
        Document doc4 = new Document(); // Assuming Document setup
        Document doc5 = new Document(); // Assuming Document setup

        Application app1 = createApplication(100, 3000.0, ApplicationStatus.APPROVAL, customer, cloudServ, doc1);
        Application app2 = createApplication(20, 2750.0, ApplicationStatus.APPROVAL, customer, dataCore, doc2);
        Application app3 = createApplication(30, 3000.0, ApplicationStatus.APPROVAL, customer, aiVentures, doc3);

        Application app4 = createApplication(10, 600.0, ApplicationStatus.PENDING, customer, nanoTech, doc4);
        Application app5 = createApplication(50, 600.0, ApplicationStatus.PENDING, customer, roboWorks, doc5);

        customer.getApplications().add(app1);
        customer.getApplications().add(app2);
        customer.getApplications().add(app3);
        customer.getApplications().add(app4);
        customer.getApplications().add(app5);

        // Testing: getApprovedTotalAmount()
        double approvedTotalAmount = customer.getApprovedTotalAmount();

        // Assertion
        assertEquals("Approvals plus pending requests failed", 8750.0, approvedTotalAmount, 0.01);
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
 * Time: 0.015
 * 
 * OK (5 tests)
 * 
 * 
 */