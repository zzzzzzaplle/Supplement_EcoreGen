public class Test {
    public static void main(String[] args) {
        Customer customer = new Customer();
        customer.setName("John");
        customer.setSurname("Doe");
        customer.setEmail("john.doe@example.com");
        customer.setTelephone("123456789");

        Company company = new Company();
        company.setName("TechCorp");
        company.setEmail("info@techcorp.com");

        Document doc = new Document();
        doc.setName("ID");

        if (customer.createApplication(company, 10, 1000.0, doc)) {
            System.out.println("Application created.");
        }

        Application app = customer.getApplications().get(0);
        if (app.approve()) {
            System.out.println("Application approved.");
        }
        
        System.out.println("App count: " + customer.getApplicationCount());
        System.out.println("Total amount: " + customer.getApprovedTotalAmount());
    }
}
