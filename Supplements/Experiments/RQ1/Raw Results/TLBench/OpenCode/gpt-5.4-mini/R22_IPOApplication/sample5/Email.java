public class Email {
    private String receiver;
    private String content;

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
        StringBuilder builder = new StringBuilder();
        builder.append("Customer: ").append(customer.getName()).append(" ").append(customer.getSurname()).append("\n");
        builder.append("Email: ").append(customer.getEmail()).append("\n");
        builder.append("Telephone: ").append(customer.getTelephone()).append("\n");
        builder.append("Company: ").append(company.getName()).append("\n");
        builder.append("Shares: ").append(shares).append("\n");
        builder.append("Amount: ").append(amount);
        return builder.toString();
    }
}
