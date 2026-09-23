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
        sb.append("Customer Name: ").append(customer != null ? customer.getName() : null).append("\n");
        sb.append("Customer Surname: ").append(customer != null ? customer.getSurname() : null).append("\n");
        sb.append("Customer Email: ").append(customer != null ? customer.getEmail() : null).append("\n");
        sb.append("Customer Telephone: ").append(customer != null ? customer.getTelephone() : null).append("\n");
        sb.append("Company Name: ").append(company != null ? company.getName() : null).append("\n");
        sb.append("Purchased Shares: ").append(shares).append("\n");
        sb.append("Paid Amount: ").append(amount);
        return sb.toString();
    }
}
