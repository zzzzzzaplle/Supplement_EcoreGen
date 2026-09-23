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
        sb.append("Customer Name: ").append(customer.getName()).append("\n");
        sb.append("Customer Surname: ").append(customer.getSurname()).append("\n");
        sb.append("Customer Email: ").append(customer.getEmail()).append("\n");
        sb.append("Customer Telephone: ").append(customer.getTelephone()).append("\n");
        sb.append("Company Name: ").append(company.getName()).append("\n");
        sb.append("Number of Shares: ").append(shares).append("\n");
        sb.append("Paid Amount: ").append(amount);
        return sb.toString();
    }
}
