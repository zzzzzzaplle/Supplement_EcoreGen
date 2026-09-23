import java.util.List;
import java.util.ArrayList;

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
        if (!this.customer.isEligibleForIPO()) {
            return false;
        }
        sendEmailsToCustomerAndCompany();
        this.status = ApplicationStatus.APPROVAL;
        return true;
    }

    public boolean reject() {
        if (this.status != ApplicationStatus.PENDING) {
            return false;
        }
        sendRejectionEmail();
        this.status = ApplicationStatus.REJECTED;
        return true;
    }

    public boolean cancel() {
        if (this.status != ApplicationStatus.PENDING) {
            return false;
        }
        this.status = ApplicationStatus.CANCELED;
        return true;
    }

    public void sendEmailsToCustomerAndCompany() {
        if (this.customer == null || this.company == null) {
            return;
        }
        String customerEmailContent = Email.createEmailContent(
                this.customer, this.company, this.share, this.amountOfMoney);
        Email customerEmail = new Email();
        customerEmail.setReceiver(this.customer.getEmail());
        customerEmail.setContent(customerEmailContent);
        this.emails.add(customerEmail);

        Email companyEmail = new Email();
        companyEmail.setReceiver(this.company.getEmail());
        companyEmail.setContent(customerEmailContent);
        this.emails.add(companyEmail);
    }

    public void sendRejectionEmail() {
        if (this.customer == null || this.company == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Customer: ").append(this.customer.getName()).append(" ")
          .append(this.customer.getSurname()).append("\n");
        sb.append("Email: ").append(this.customer.getEmail()).append("\n");
        sb.append("Telephone: ").append(this.customer.getTelephone()).append("\n");
        sb.append("Company: ").append(this.company.getName()).append("\n");
        sb.append("Shares: ").append(this.share).append("\n");
        sb.append("Amount: ").append(this.amountOfMoney).append("\n");
        Email rejectionEmail = new Email();
        rejectionEmail.setReceiver(this.customer.getEmail());
        rejectionEmail.setContent(sb.toString());
        this.emails.add(rejectionEmail);
    }
}
