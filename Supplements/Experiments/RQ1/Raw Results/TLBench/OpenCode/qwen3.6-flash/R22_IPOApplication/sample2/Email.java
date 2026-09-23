import java.util.ArrayList;
import java.util.List;

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
        sb.append("Customer Name: ").append(customer.getName()).append(", ");
        sb.append("Surname: ").append(customer.getSurname()).append(", ");
        sb.append("Email: ").append(customer.getEmail()).append(", ");
        sb.append("Telephone: ").append(customer.getTelephone()).append(", ");
        sb.append("Company: ").append(company.getName()).append(", ");
        sb.append("Shares: ").append(shares).append(", ");
        sb.append("Amount: ").append(amount);
        return sb.toString();
    }
}
