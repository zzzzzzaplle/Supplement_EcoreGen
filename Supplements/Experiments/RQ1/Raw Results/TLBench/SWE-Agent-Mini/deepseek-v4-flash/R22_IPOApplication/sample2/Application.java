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
        if (status != ApplicationStatus.PENDING) {
            return false;
        }
        if (!customer.isEligibleForIPO()) {
            return false;
        }
        status = ApplicationStatus.APPROVAL;
        sendEmailsToCustomerAndCompany();
        return true;
    }

    public boolean reject() {
        if (status != ApplicationStatus.PENDING) {
            return false;
        }
        status = ApplicationStatus.REJECTED;
        sendRejectionEmail();
        return true;
    }

    public boolean cancel() {
        if (status != ApplicationStatus.PENDING) {
            return false;
        }
        status = null; // Canceled - not a standard enum value, but requirement says canceled
        return true;
    }

    public void sendEmailsToCustomerAndCompany() {
        // Email to customer
        Email customerEmail = new Email();
        customerEmail.setReceiver(customer.getEmail());
        String content = "Your IPO application to " + company.getName() + 
                         " has been approved. Shares: " + share + 
                         ", Amount: " + amountOfMoney;
        customerEmail.setContent(content);
        emails.add(customerEmail);

        // Email to company
        Email companyEmail = new Email();
        companyEmail.setReceiver(company.getEmail());
        String companyContent = "Customer " + customer.getName() + " " + customer.getSurname() + 
                                " has applied for IPO. Shares: " + share + 
                                ", Amount: " + amountOfMoney;
        companyEmail.setContent(companyContent);
        emails.add(companyEmail);
    }

    public void sendRejectionEmail() {
        Email email = new Email();
        email.setReceiver(customer.getEmail());
        String content = Email.createEmailContent(customer, company, share, amountOfMoney);
        email.setContent(content);
        emails.add(email);
    }
}
