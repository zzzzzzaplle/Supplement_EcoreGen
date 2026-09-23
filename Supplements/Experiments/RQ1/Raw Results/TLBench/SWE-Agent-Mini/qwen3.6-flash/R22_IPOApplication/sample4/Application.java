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
        if (status != ApplicationStatus.PENDING) {
            return false;
        }
        if (customer != null && customer.isEligibleForIPO()) {
            status = ApplicationStatus.APPROVAL;
            sendEmailsToCustomerAndCompany();
            return true;
        }
        return false;
    }

    public boolean reject() {
        if (status == ApplicationStatus.PENDING) {
            status = ApplicationStatus.REJECTED;
            sendRejectionEmail();
            return true;
        }
        return false;
    }

    public boolean cancel() {
        if (status == ApplicationStatus.PENDING) {
            return true;
        }
        return false;
    }

    public void sendEmailsToCustomerAndCompany() {
        if (customer == null || company == null) {
            return;
        }
        String customerContent = Email.createEmailContent(customer, company, share, amountOfMoney);
        String companyContent = Email.createEmailContent(customer, company, share, amountOfMoney);

        Email emailToCustomer = new Email();
        emailToCustomer.setReceiver(customer.getEmail());
        emailToCustomer.setContent(customerContent);
        emails.add(emailToCustomer);

        Email emailToCompany = new Email();
        emailToCompany.setReceiver(company.getEmail());
        emailToCompany.setContent(companyContent);
        emails.add(emailToCompany);
    }

    public void sendRejectionEmail() {
        if (customer == null || company == null) {
            return;
        }
        String rejectionContent = "Application for " + company.getName()
                + " by customer: " + customer.getName() + " " + customer.getSurname()
                + ", Email: " + customer.getEmail()
                + ", Telephone: " + customer.getTelephone()
                + ", Shares: " + share
                + ", Amount: " + amountOfMoney
                + " has been rejected.";

        Email rejectionEmail = new Email();
        rejectionEmail.setReceiver(customer.getEmail());
        rejectionEmail.setContent(rejectionContent);
        emails.add(rejectionEmail);
    }
}
