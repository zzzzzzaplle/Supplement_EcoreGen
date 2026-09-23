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
        this.status = ApplicationStatus.PENDING;
        this.emails = new ArrayList<>();
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
        if (customer == null || !customer.isEligibleForIPO()) {
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
        this.status = ApplicationStatus.REJECTED;
        return true;
    }

    public void sendEmailsToCustomerAndCompany() {
        if (customer == null || company == null) {
            return;
        }
        String customerEmailContent = Email.createEmailContent(customer, company, share, amountOfMoney);
        String companyEmailContent = Email.createEmailContent(customer, company, share, amountOfMoney);

        Email customerEmail = new Email();
        customerEmail.setReceiver(customer.getEmail());
        customerEmail.setContent(customerEmailContent);
        emails.add(customerEmail);

        Email companyEmail = new Email();
        companyEmail.setReceiver(company.getEmail());
        companyEmail.setContent(companyEmailContent);
        emails.add(companyEmail);
    }

    public void sendRejectionEmail() {
        if (customer == null || company == null) {
            return;
        }
        String rejectionContent = Email.createEmailContent(customer, company, share, amountOfMoney);

        Email rejectionEmail = new Email();
        rejectionEmail.setReceiver(customer.getEmail());
        rejectionEmail.setContent(rejectionContent);
        emails.add(rejectionEmail);
    }
}
