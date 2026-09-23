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
        if (status != ApplicationStatus.PENDING || !customer.isEligibleForIPO()) return false;
        status = ApplicationStatus.APPROVAL;
        sendEmailsToCustomerAndCompany();
        return true;
    }

    public boolean reject() {
        if (status != ApplicationStatus.PENDING) return false;
        status = ApplicationStatus.REJECTED;
        sendRejectionEmail();
        return true;
    }

    public boolean cancel() {
        if (status != ApplicationStatus.PENDING) return false;
        customer.getApplications().remove(this);
        return true;
    }

    public void sendEmailsToCustomerAndCompany() {
        emails.add(new Email(customer.getEmail(), "Approved"));
        emails.add(new Email(company.getEmail(), "Approved"));
    }

    public void sendRejectionEmail() {
        String content = Email.createEmailContent(customer, company, share, amountOfMoney);
        emails.add(new Email(customer.getEmail(), content));
    }
}
