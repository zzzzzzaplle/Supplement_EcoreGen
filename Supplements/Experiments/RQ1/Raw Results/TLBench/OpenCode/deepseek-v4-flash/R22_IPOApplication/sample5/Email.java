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
        return "Dear " + customer.getName() + " " + customer.getSurname() + ",\n" +
               "Email: " + customer.getEmail() + "\n" +
               "Telephone: " + customer.getTelephone() + "\n" +
               "Company: " + company.getName() + "\n" +
               "Shares: " + shares + "\n" +
               "Amount: " + amount;
    }
}
