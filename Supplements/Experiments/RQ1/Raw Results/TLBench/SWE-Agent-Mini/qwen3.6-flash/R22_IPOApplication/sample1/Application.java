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
        // Can only approve pending applications
        if (status != ApplicationStatus.PENDING) {
            return false;
        }

        // Customer must be eligible to apply for IPO
        if (customer != null && customer.isEligibleForIPO()) {
            status = ApplicationStatus.APPROVAL;
            sendEmailsToCustomerAndCompany();
            return true;
        }
        return false;
    }

    public boolean reject() {
        // Can only reject pending applications
        if (status != ApplicationStatus.PENDING) {
            return false;
        }

        status = ApplicationStatus.REJECTED;
        sendRejectionEmail();
        return true;
    }

    public boolean cancel() {
        // Can only cancel pending applications
        if (status != ApplicationStatus.PENDING) {
            return false;
        }

        status = ApplicationStatus.CANCELLED;
        return true;
    }

    public void sendEmailsToCustomerAndCompany() {
        if (customer == null || company == null) {
            return;
        }

        String customerContent = Email.createEmailContent(customer, company, share, amountOfMoney);
        Email customerEmail = new Email();
        customerEmail.setReceiver(customer.getEmail());
        customerEmail.setContent(customerContent);
        customerEmail.sendEmailToCustomer(customer);

        String companyContent = Email.createEmailContent(customer, company, share, amountOfMoney);
        Email companyEmail = new Email();
        companyEmail.setReceiver(company.getEmail());
        companyEmail.setContent(companyContent);
        companyEmail.sendEmailToCompany(company);

        emails.add(customerEmail);
        emails.add(companyEmail);
    }

    public void sendRejectionEmail() {
        if (customer == null || company == null) {
            return;
        }

        String rejectionContent = Email.createRejectionEmailContent(customer, company, share, amountOfMoney);
        Email rejectionEmail = new Email();
        rejectionEmail.setReceiver(customer.getEmail());
        rejectionEmail.setContent(rejectionContent);
        rejectionEmail.sendCustomerRejectionEmail(customer);

        emails.add(rejectionEmail);
    }
}
