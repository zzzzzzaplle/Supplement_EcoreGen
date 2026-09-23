public class Email {
    private String receiver;
    private String content;

    public Email() {}
    public Email(String receiver, String content) {
        this.receiver = receiver;
        this.content = content;
    }

    public String getReceiver() { return receiver; }
    public void setReceiver(String receiver) { this.receiver = receiver; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public static String createEmailContent(Customer c, Company comp, int s, double a) {
        return "Customer: " + c.getName() + " " + c.getSurname() + ", Email: " + c.getEmail() + 
               ", Telephone: " + c.getTelephone() + ", Company: " + comp.getName() + 
               ", Shares: " + s + ", Amount: " + a;
    }
}
