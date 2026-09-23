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
        sb.append("Dear Customer,\n\n");
        sb.append("Your IPO application has been rejected.\n\n");
        sb.append("Customer Details:\n");
        sb.append("Name: ").append(customer.getName()).append("\n");
        sb.append("Surname: ").append(customer.getSurname()).append("\n");
        sb.append("Email: ").append(customer.getEmail()).append("\n");
        sb.append("Telephone: ").append(customer.getTelephone()).append("\n\n");
        sb.append("Company: ").append(company.getName()).append("\n");
        sb.append("Number of Shares: ").append(shares).append("\n");
        sb.append("Amount Paid: ").append(amount).append("\n");
        return sb.toString();
    }
}
