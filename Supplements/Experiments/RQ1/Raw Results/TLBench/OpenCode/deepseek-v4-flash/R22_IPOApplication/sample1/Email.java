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
        return "Dear " + customer.getName() + " " + customer.getSurname() + ","
                + " Your IPO application for " + company.getName()
                + " for " + shares + " shares with amount " + amount
                + " has been processed.";
    }
}
