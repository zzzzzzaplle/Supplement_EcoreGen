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
        this.emails = new ArrayList<Email>();
        this.status = ApplicationStatus.PENDING;
    }

    public int getShare() {
        return share;
    }

    public void setShare(int share) {
        this.share = share;
    }

    public double getAmountOfMoney() {
        return amountOfMoney;
    }

    public void setAmountOfMoney(double amountOfMoney) {
        this.amountOfMoney = amountOfMoney;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Document getAllowance() {
        return allowance;
    }

    public void setAllowance(Document allowance) {
        this.allowance = allowance;
    }

    public List<Email> getEmails() {
        return emails;
    }

    public void setEmails(List<Email> emails) {
        this.emails = emails;
    }

    public boolean approve() {
        if (this.status != ApplicationStatus.PENDING) {
            return false;
        }
        if (this.customer == null || !this.customer.isEligibleForIPO()) {
            return false;
        }
        this.status = ApplicationStatus.APPROVAL;
        sendEmailsToCustomerAndCompany();
        return true;
    }

    public boolean reject() {
        if (this.status != ApplicationStatus.PENDING) {
            return false;
        }
        this.status = ApplicationStatus.REJECTED;
        sendRejectionEmail();
        return true;
    }

    public boolean cancel() {
        if (this.status != ApplicationStatus.PENDING) {
            return false;
        }
        return true;
    }

    public void sendEmailsToCustomerAndCompany() {
        String content = Email.createEmailContent(this.customer, this.company, this.share, this.amountOfMoney);
        Email customerEmail = new Email();
        customerEmail.setReceiver(this.customer.getEmail());
        customerEmail.setContent(content);
        this.emails.add(customerEmail);

        Email companyEmail = new Email();
        companyEmail.setReceiver(this.company.getEmail());
        companyEmail.setContent(content);
        this.emails.add(companyEmail);
    }

    public void sendRejectionEmail() {
        String content = Email.createEmailContent(this.customer, this.company, this.share, this.amountOfMoney);
        Email rejectionEmail = new Email();
        rejectionEmail.setReceiver(this.customer.getEmail());
        rejectionEmail.setContent(content);
        this.emails.add(rejectionEmail);
    }
}
