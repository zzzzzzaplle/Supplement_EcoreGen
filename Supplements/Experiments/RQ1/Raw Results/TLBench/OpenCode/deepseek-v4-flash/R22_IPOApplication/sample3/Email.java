import java.util.List;
import java.util.ArrayList;

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
        StringBuilder sb = new StringBuilder();
        sb.append("Customer: ").append(customer.getName()).append(" ").append(customer.getSurname()).append("\n");
        sb.append("Email: ").append(customer.getEmail()).append("\n");
        sb.append("Telephone: ").append(customer.getTelephone()).append("\n");
        sb.append("Company: ").append(company.getName()).append("\n");
        sb.append("Shares: ").append(shares).append("\n");
        sb.append("Amount: ").append(amount);
        return sb.toString();
    }
}
