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
            builder.append(customer.getName());
            builder.append(" ");
            builder.append(customer.getSurname());
            builder.append(" ");
            builder.append(customer.getEmail());
            builder.append(" ");
            builder.append(customer.getTelephone());
        }
        if (company != null) {
            builder.append(" ");
            builder.append(company.getName());
        }
        builder.append(" ");
        builder.append(shares);
        builder.append(" ");
        builder.append(amount);
        return builder.toString();
    }
}
