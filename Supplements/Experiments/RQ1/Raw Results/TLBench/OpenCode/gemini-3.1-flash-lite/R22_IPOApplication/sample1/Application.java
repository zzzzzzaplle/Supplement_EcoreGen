import java.util.ArrayList;
import java.util.List;

public class Application {
    private int share;
    private double amountOfMoney;
    private ApplicationStatus status;
    private Customer customer;
    private Company company;
    private Document allowance;
    private List<Email> emails;

    public Application() {
        this.emails = new ArrayList<>();
    }

    public int getShare() { return share; }
    public void setShare(int share) { this.share = share; }

    public double getAmountOfMoney() { return amountOfMoney; }
    public void setAmountOfMoney(double amountOfMoney) { this.amountOfMoney = amountOfMoney; }

    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public Document getAllowance() { return allowance; }
    public void setAllowance(Document allowance) { this.allowance = allowance; }

    public List<Email> getEmails() { return emails; }
    public void setEmails(List<Email> emails) { this.emails = emails; }

    public boolean approve() {
        if (status == ApplicationStatus.PENDING && customer.isEligibleForIPO()) {
            this.status = ApplicationStatus.APPROVAL;
            sendEmailsToCustomerAndCompany();
            return true;
        }
        return false;
    }

    public boolean reject() {
        if (status == ApplicationStatus.PENDING) {
            this.status = ApplicationStatus.REJECTED;
            sendRejectionEmail();
            return true;
        }
        return false;
    }

    public boolean cancel() {
        if (status == ApplicationStatus.PENDING) {
            this.status = ApplicationStatus.REJECTED; // Logic as per rules
            return true;
        }
        return false;
    }

    public void sendEmailsToCustomerAndCompany() {
        Email email1 = new Email();
        email1.setReceiver(customer.getEmail());
        email1.setContent("Approved");
        emails.add(email1);
        Email email2 = new Email();
        email2.setReceiver(company.getEmail());
        email2.setContent("Approved");
        emails.add(email2);
    }

    public void sendRejectionEmail() {
        Email email = new Email();
        email.setReceiver(customer.getEmail());
        email.setContent(Email.createEmailContent(customer, company, share, amountOfMoney));
        emails.add(email);
    }
}
