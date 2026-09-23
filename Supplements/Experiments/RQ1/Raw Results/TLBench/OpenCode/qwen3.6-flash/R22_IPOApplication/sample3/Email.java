public class Email {
    private String receiver;
    private String content;

    public Email() {
        this.receiver = "";
        this.content = "";
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
        return "IPO Application Information\n" +
                "Customer Name: " + customer.getName() + "\n" +
                "Customer Surname: " + customer.getSurname() + "\n" +
                "Customer Email: " + customer.getEmail() + "\n" +
                "Customer Telephone: " + customer.getTelephone() + "\n" +
                "Company Name: " + company.getName() + "\n" +
                "Shares: " + shares + "\n" +
                "Amount: " + amount;
    }
}
