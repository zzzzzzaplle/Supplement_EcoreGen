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
        String customerName = customer == null ? "" : customer.getName();
        String customerSurname = customer == null ? "" : customer.getSurname();
        String customerEmail = customer == null ? "" : customer.getEmail();
        String customerTelephone = customer == null ? "" : customer.getTelephone();
        String companyName = company == null ? "" : company.getName();
        return customerName + " " + customerSurname + " " + customerEmail + " " + customerTelephone + " " + companyName + " " + shares + " " + amount;
    }
}
