import java.util.ArrayList;
import java.util.List;

public class Application {
    private int share;
    private double amountOfMoney;
    private ApplicationStatus status;
    private Customer customer;
    private Company company;
    private Document allowance;
    private List<Email> emails = new ArrayList<>();

    public Application() {}

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
        if (status != ApplicationStatus.PENDING || !customer.isEligibleForIPO()) {
            return false;
        }
        this.status = ApplicationStatus.APPROVAL;
        sendEmailsToCustomerAndCompany();
        return true;
    }

    public boolean reject() {
        if (status != ApplicationStatus.PENDING) {
            return false;
        }
        this.status = ApplicationStatus.REJECTED;
        sendRejectionEmail();
        return true;
    }

    public boolean cancel() {
        if (status != ApplicationStatus.PENDING) {
            return false;
        }
        // Requirement implies it just shouldn't be counted in summary.
        // There is no explicit "CANCELED" status enum, but the summary logic excludes it.
        // Let's remove from list or handle as canceled.
        customer.getApplications().remove(this);
        return true;
    }

    public void sendEmailsToCustomerAndCompany() {
        String content = Email.createEmailContent(customer, company, share, amountOfMoney);
        Email e1 = new Email();
        e1.setReceiver(customer.getEmail());
        e1.setContent(content);
        emails.add(e1);
        Email e2 = new Email();
        e2.setReceiver(company.getEmail());
        e2.setContent(content);
        emails.add(e2);
    }

    public void sendRejectionEmail() {
        String content = Email.createEmailContent(customer, company, share, amountOfMoney);
        Email e = new Email();
        e.setReceiver(customer.getEmail());
        e.setContent("Rejected: " + content);
        emails.add(e);
    }
}
