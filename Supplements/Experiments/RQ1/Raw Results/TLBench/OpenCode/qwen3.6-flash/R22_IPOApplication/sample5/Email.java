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
        StringBuilder content = new StringBuilder();
        content.append("Customer Name: ").append(customer.getName());
        content.append("\nSurname: ").append(customer.getSurname());
        content.append("\nEmail: ").append(customer.getEmail());
        content.append("\nTelephone: ").append(customer.getTelephone());
        content.append("\nCompany Name: ").append(company.getName());
        content.append("\nNumber of Shares: ").append(shares);
        content.append("\nAmount Paid: $").append(amount);
        return content.toString();
    }
}
