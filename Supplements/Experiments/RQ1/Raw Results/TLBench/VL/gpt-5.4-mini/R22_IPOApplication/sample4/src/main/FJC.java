import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a retail customer who may apply for IPOs.
 */
class Customer {
    private String name;
    private String surname;
    private String email;
    private String telephone;
    private boolean canApplyForIPO;
    private List<Application> applications;

    /**
     * Creates an empty customer instance.
     */
    public Customer() {
        this.applications = new ArrayList<>();
        this.canApplyForIPO = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public boolean isEligibleForIPO() {
        return canApplyForIPO;
    }

    public void setCanApplyForIPO(boolean canApplyForIPO) {
        this.canApplyForIPO = canApplyForIPO;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }

    /**
     * Creates a new IPO application if the customer is eligible and does not already
     * have an approved application for the same company name.
     *
     * @param company target company
     * @param shares share count greater than 0
     * @param amount payment amount greater than 0
     * @param doc allowance document
     * @return true if application is created successfully; otherwise false
     */
    public boolean createApplication(Company company, int shares, double amount, Document doc) {
        if (company == null || doc == null || shares <= 0 || amount <= 0) {
            return false;
        }
        if (!isEligibleForIPO()) {
            return false;
        }
        if (applications == null) {
            applications = new ArrayList<>();
        }
        for (Application application : applications) {
            if (application != null
                    && application.getCompany() != null
                    && Objects.equals(application.getCompany().getName(), company.getName())
                    && application.getStatus() == ApplicationStatus.APPROVAL) {
                return false;
            }
        }

        Application application = new Application();
        application.setCustomer(this);
        application.setCompany(company);
        application.setShare(shares);
        application.setAmountOfMoney(amount);
        application.setAllowance(doc);
        application.setStatus(ApplicationStatus.PENDING);

        applications.add(application);
        return true;
    }

    /**
     * Returns the total count of completed applications, counting only approved and rejected ones.
     *
     * @return count of completed applications
     */
    public int getApplicationCount() {
        if (applications == null) {
            return 0;
        }
        int count = 0;
        for (Application application : applications) {
            if (application != null) {
                ApplicationStatus status = application.getStatus();
                if (status == ApplicationStatus.APPROVAL || status == ApplicationStatus.REJECTED) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Returns the total approved amount across all approved applications.
     *
     * @return sum of approved amounts
     */
    public double getApprovedTotalAmount() {
        if (applications == null) {
            return 0.0;
        }
        double total = 0.0;
        for (Application application : applications) {
            if (application != null && application.getStatus() == ApplicationStatus.APPROVAL) {
                total += application.getAmountOfMoney();
            }
        }
        return total;
    }

    /**
     * Cancels a pending application for the given company name.
     *
     * @param companyName target company name
     * @return true if cancellation succeeds; otherwise false
     */
    public boolean cancelApplication(String companyName) {
        if (companyName == null || applications == null) {
            return false;
        }
        for (Application application : applications) {
            if (application != null
                    && application.getCompany() != null
                    && Objects.equals(application.getCompany().getName(), companyName)
                    && application.getStatus() == ApplicationStatus.PENDING) {
                return application.cancel();
            }
        }
        return false;
    }
}

/**
 * Represents the status of an IPO application.
 */
enum ApplicationStatus {
    PENDING,
    APPROVAL,
    REJECTED
}

/**
 * Represents an IPO application record.
 */
class Application {
    private int share;
    private double amountOfMoney;
    private ApplicationStatus status;
    private Customer customer;
    private Company company;
    private Document allowance;
    private List<Email> emails;

    /**
     * Creates an empty application.
     */
    public Application() {
        this.emails = new ArrayList<>();
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

    /**
     * Approves a pending application only when the customer is eligible for IPO.
     *
     * @return true on success; otherwise false
     */
    public boolean approve() {
        if (status != ApplicationStatus.PENDING || customer == null || !customer.isEligibleForIPO()) {
            return false;
        }
        status = ApplicationStatus.APPROVAL;
        sendEmailsToCustomerAndCompany();
        return true;
    }

    /**
     * Rejects a pending application and sends a rejection email to the customer.
     *
     * @return true on success; otherwise false
     */
    public boolean reject() {
        if (status != ApplicationStatus.PENDING) {
            return false;
        }
        status = ApplicationStatus.REJECTED;
        sendRejectionEmail();
        return true;
    }

    /**
     * Cancels a pending application.
     *
     * @return true on success; otherwise false
     */
    public boolean cancel() {
        if (status != ApplicationStatus.PENDING) {
            return false;
        }
        status = ApplicationStatus.REJECTED;
        return true;
    }

    /**
     * Sends information emails to both customer and company after approval.
     */
    public void sendEmailsToCustomerAndCompany() {
        if (emails == null) {
            emails = new ArrayList<>();
        }
        if (customer == null || company == null) {
            return;
        }
        String content = Email.createEmailContent(customer, company, share, amountOfMoney);

        Email customerEmail = new Email();
        customerEmail.setReceiver(customer.getEmail());
        customerEmail.setContent(content);

        Email companyEmail = new Email();
        companyEmail.setReceiver(company.getEmail());
        companyEmail.setContent(content);

        emails.add(customerEmail);
        emails.add(companyEmail);
    }

    /**
     * Sends a rejection email to the customer.
     */
    public void sendRejectionEmail() {
        if (emails == null) {
            emails = new ArrayList<>();
        }
        if (customer == null || company == null) {
            return;
        }
        String content = "Dear " + safe(customer.getName()) + " " + safe(customer.getSurname()) + ", "
                + "your IPO application for company " + safe(company.getName()) + " has been rejected. "
                + "Customer info: name=" + safe(customer.getName())
                + ", surname=" + safe(customer.getSurname())
                + ", email=" + safe(customer.getEmail())
                + ", telephone=" + safe(customer.getTelephone())
                + ". Shares=" + share
                + ", amountPaid=" + amountOfMoney + ".";

        Email rejectionEmail = new Email();
        rejectionEmail.setReceiver(customer.getEmail());
        rejectionEmail.setContent(content);
        emails.add(rejectionEmail);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}

/**
 * Represents a company related to an IPO application.
 */
class Company {
    private String name;
    private String email;

    /**
     * Creates an empty company instance.
     */
    public Company() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

/**
 * Represents an allowance document required for IPO application.
 */
class Document {
    private String name;

    /**
     * Creates an empty document instance.
     */
    public Document() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

/**
 * Represents an email sent by the system.
 */
class Email {
    private String receiver;
    private String content;

    public Email() {
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Creates a standard IPO approval email content.
     *
     * @param customer the customer
     * @param company the company
     * @param shares number of shares
     * @param amount payment amount
     * @return formatted email content
     */
    public static String createEmailContent(Customer customer, Company company, int shares, double amount) {
        String customerName = customer == null ? "" : safe(customer.getName());
        String customerSurname = customer == null ? "" : safe(customer.getSurname());
        String customerEmail = customer == null ? "" : safe(customer.getEmail());
        String customerTelephone = customer == null ? "" : safe(customer.getTelephone());
        String companyName = company == null ? "" : safe(company.getName());

        return "IPO Application Information: "
                + "customerName=" + customerName
                + ", customerSurname=" + customerSurname
                + ", customerEmail=" + customerEmail
                + ", customerTelephone=" + customerTelephone
                + ", companyName=" + companyName
                + ", shares=" + shares
                + ", amountPaid=" + amount + ".";
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}