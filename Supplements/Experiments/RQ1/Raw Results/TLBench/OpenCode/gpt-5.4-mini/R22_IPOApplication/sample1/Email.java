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
        StringBuilder builder = new StringBuilder();
        if (customer != null) {
            builder.append(customer.getName()).append(" ").append(customer.getSurname()).append(" ");
            builder.append(customer.getEmail()).append(" ").append(customer.getTelephone()).append(" ");
        }
        if (company != null) {
            builder.append(company.getName()).append(" ");
        }
        builder.append(shares).append(" ").append(amount);
        return builder.toString();
    }
}
