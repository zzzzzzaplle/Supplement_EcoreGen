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
        sb.append("Name: ").append(customer.getName());
        sb.append(", Surname: ").append(customer.getSurname());
        sb.append(", Email: ").append(customer.getEmail());
        sb.append(", Telephone: ").append(customer.getTelephone());
        sb.append(", Company: ").append(company.getName());
        sb.append(", Shares: ").append(shares);
        sb.append(", Amount: ").append(amount);
        return sb.toString();
    }
}
