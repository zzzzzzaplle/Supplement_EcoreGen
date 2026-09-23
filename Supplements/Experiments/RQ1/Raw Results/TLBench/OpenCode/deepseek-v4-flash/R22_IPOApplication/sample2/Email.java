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
        return "Customer: " + customer.getName() + " " + customer.getSurname()
            + ", Email: " + customer.getEmail()
            + ", Telephone: " + customer.getTelephone()
            + ", Company: " + company.getName()
            + ", Shares: " + shares
            + ", Amount: " + amount;
    }
}
