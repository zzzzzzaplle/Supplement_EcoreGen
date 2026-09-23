public class Email {
    private String receiver;
    private String content;

    public Email() {
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static String createEmailContent(Customer customer, Company company, int shares, double amount) {
        StringBuilder sb = new StringBuilder();
        sb.append("Application Details:\n");
        sb.append("Name: ").append(customer.getName()).append("\n");
        sb.append("Surname: ").append(customer.getSurname()).append("\n");
        sb.append("Email: ").append(customer.getEmail()).append("\n");
        sb.append("Telephone: ").append(customer.getTelephone()).append("\n");
        sb.append("Company: ").append(company.getName()).append("\n");
        sb.append("Shares: ").append(shares).append("\n");
        sb.append("Amount: ").append(amount).append("\n");
        return sb.toString();
    }

    public static String createRejectionEmailContent(Customer customer, Company company, int shares, double amount) {
        StringBuilder sb = new StringBuilder();
        sb.append("Rejection Details:\n");
        sb.append("Name: ").append(customer.getName()).append("\n");
        sb.append("Surname: ").append(customer.getSurname()).append("\n");
        sb.append("Email: ").append(customer.getEmail()).append("\n");
        sb.append("Telephone: ").append(customer.getTelephone()).append("\n");
        sb.append("Company: ").append(company.getName()).append("\n");
        sb.append("Shares: ").append(shares).append("\n");
        sb.append("Amount: ").append(amount).append("\n");
        return sb.toString();
    }

    public void sendEmailToCustomer(Customer customer) {
        System.out.println("Sending email to customer at: " + receiver);
        System.out.println("Content: " + content);
    }

    public void sendEmailToCompany(Company company) {
        System.out.println("Sending email to company at: " + receiver);
        System.out.println("Content: " + content);
    }

    public void sendCustomerRejectionEmail(Customer customer) {
        System.out.println("Sending rejection email to customer at: " + receiver);
        System.out.println("Content: " + content);
    }
}
