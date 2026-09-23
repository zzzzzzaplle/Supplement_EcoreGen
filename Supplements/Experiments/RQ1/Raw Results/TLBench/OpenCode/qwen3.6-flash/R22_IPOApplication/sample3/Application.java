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
        this.share = 0;
        this.amountOfMoney = 0.0;
        this.status = ApplicationStatus.PENDING;
        this.customer = null;
        this.company = null;
        this.allowance = null;
        this.emails = null;
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
        status = ApplicationStatus.PENDING;
        return true;
    }

    public void sendEmailsToCustomerAndCompany() {
        String customerContent = Email.createEmailContent(customer, company, share, amountOfMoney);
        String companyContent = Email.createEmailContent(customer, company, share, amountOfMoney);

        Email customerEmail = new Email();
        customerEmail.setReceiver(customer.getEmail());
        customerEmail.setContent(customerContent);
        emails.add(customerEmail);

        Email companyEmail = new Email();
        companyEmail.setReceiver(company.getEmail());
        companyEmail.setContent(companyContent);
        emails.add(companyEmail);
    }

    public void sendRejectionEmail() {
        String content = "Rejection Notice\n" +
                "Customer Name: " + customer.getName() + "\n" +
                "Customer Surname: " + customer.getSurname() + "\n" +
                "Customer Email: " + customer.getEmail() + "\n" +
                "Customer Telephone: " + customer.getTelephone() + "\n" +
                "Company Name: " + company.getName() + "\n" +
                "Number of Shares: " + share + "\n" +
                "Amount Paid: " + amountOfMoney;

        Email rejectionEmail = new Email();
        rejectionEmail.setReceiver(customer.getEmail());
        rejectionEmail.setContent(content);
        emails.add(rejectionEmail);
    }
}
