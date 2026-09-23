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
        sb.append("Customer Name: ").append(customer.getName()).append(" ").append(customer.getSurname());
        sb.append("\nCustomer Email: ").append(customer.getEmail());
        sb.append("\nCustomer Telephone: ").append(customer.getTelephone());
        sb.append("\nCompany Name: ").append(company.getName());
        sb.append("\nNumber of Shares: ").append(shares);
        sb.append("\nAmount Paid: ").append(amount);
        return sb.toString();
    }
}
