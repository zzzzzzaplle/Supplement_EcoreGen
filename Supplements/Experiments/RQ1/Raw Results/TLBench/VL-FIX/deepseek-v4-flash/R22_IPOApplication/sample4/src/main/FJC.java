import java.util.ArrayList;
import java.util.List;

// ============================================================
// Enum: ApplicationStatus
// ============================================================
enum ApplicationStatus {
    PENDING,
    APPROVAL,
    REJECTED,
    CANCELED
}

// ============================================================
// Class: Document
// ============================================================
class Document {
    private String name;

    // Unparameterized constructor
    public Document() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

// ============================================================
// Class: Company
// ============================================================
class Company {
    private String name;
    private String email;

    // Unparameterized constructor
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

// ============================================================
// Class: Email
// ============================================================
class Email {
    private String receiver;
    private String content;

    // Unparameterized constructor
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

    // Static method to create rejection email content
    public static String createEmailContent(Customer customer, Company company, int shares, double amount) {
        return "Customer Name: " + customer.getName() + " " + customer.getSurname() +
               ", Email: " + customer.getEmail() +
               ", Telephone: " + customer.getTelephone() +
               ", Company: " + company.getName() +
               ", Shares: " + shares +
               ", Amount Paid: " + amount;
    }
}

// ============================================================
// Class: Application
// ============================================================
class Application {
    private int share;
    private double amountOfMoney;
    private ApplicationStatus status;
    private Customer customer;
    private Company company;
    private Document allowance;
    private List<Email> emails;

    // Unparameterized constructor
    public Application() {
        this.status = ApplicationStatus.PENDING;
        this.emails = new ArrayList<>();
    }

    // Getters and Setters
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

    // Business methods

    /**
     * Approves a pending application.
     * Only allowed if the customer is still eligible for IPO.
     * Sends information emails to customer and company on success.
     * @return true if approval successful, false otherwise.
     */
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

    /**
     * Rejects a pending application.
     * Sends a rejection email to the customer with full details.
     * @return true if rejection successful, false otherwise.
     */
    public boolean reject() {
        if (this.status != ApplicationStatus.PENDING) {
            return false;
        }
        this.status = ApplicationStatus.REJECTED;
        sendRejectionEmail();
        return true;
    }

    /**
     * Cancels a pending application.
     * @return true if cancellation successful, false otherwise.
     */
    public boolean cancel() {
        if (this.status != ApplicationStatus.PENDING) {
            return false;
        }
        this.status = ApplicationStatus.CANCELED;
        return true;
    }

    /**
     * Sends two information emails: one to the customer and one to the company.
     */
    private void sendEmailsToCustomerAndCompany() {
        // Email to customer
        Email customerEmail = new Email();
        customerEmail.setReceiver(this.customer.getEmail());
        customerEmail.setContent("Your IPO application for " + this.company.getName() + " has been approved.");
        this.emails.add(customerEmail);

        // Email to company
        Email companyEmail = new Email();
        companyEmail.setReceiver(this.company.getEmail());
        companyEmail.setContent("IPO application from " + this.customer.getName() + " " + this.customer.getSurname() + " has been approved.");
        this.emails.add(companyEmail);
    }

    /**
     * Sends a rejection email to the customer with full details.
     */
    private void sendRejectionEmail() {
        Email rejectionEmail = new Email();
        rejectionEmail.setReceiver(this.customer.getEmail());
        rejectionEmail.setContent(Email.createEmailContent(this.customer, this.company, this.share, this.amountOfMoney));
        this.emails.add(rejectionEmail);
    }
}

// ============================================================
// Class: Customer
// ============================================================
class Customer {
    private String name;
    private String surname;
    private String email;
    private String telephone;
    private boolean canApplyForIPO;
    private List<Application> applications;

    // Unparameterized constructor
    public Customer() {
        this.canApplyForIPO = true; // Default eligible
        this.applications = new ArrayList<>();
    }

    // Getters and Setters
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

    // Business methods

    /**
     * Creates a new IPO application.
     * Checks eligibility, no existing approved application for the same company,
     * and valid parameters.
     * @param company the target company
     * @param shares number of shares (must be > 0)
     * @param amount payment amount (must be > 0)
     * @param doc allowance document (must be non-null)
     * @return true if application created successfully, false otherwise
     */
    public boolean createApplication(Company company, int shares, double amount, Document doc) {
        if (!this.canApplyForIPO) {
            return false;
        }
        if (shares <= 0 || amount <= 0 || doc == null) {
            return false;
        }
        // Check if there is already an approved application for the same company
        for (Application app : this.applications) {
            if (app.getCompany() != null && app.getCompany().getName().equals(company.getName()) && app.getStatus() == ApplicationStatus.APPROVAL) {
                return false;
            }
        }
        Application newApp = new Application();
        newApp.setShare(shares);
        newApp.setAmountOfMoney(amount);
        newApp.setAllowance(doc);
        newApp.setCompany(company);
        newApp.setCustomer(this);
        newApp.setStatus(ApplicationStatus.PENDING);
        this.applications.add(newApp);
        return true;
    }

    /**
     * Returns the count of completed applications (APPROVAL and REJECTED only).
     * @return count
     */
    public int getApplicationCount() {
        int count = 0;
        for (Application app : this.applications) {
            if (app.getStatus() == ApplicationStatus.APPROVAL || app.getStatus() == ApplicationStatus.REJECTED) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the total paid amount of all approved applications.
     * @return total amount
     */
    public double getApprovedTotalAmount() {
        double total = 0.0;
        for (Application app : this.applications) {
            if (app.getStatus() == ApplicationStatus.APPROVAL) {
                total += app.getAmountOfMoney();
            }
        }
        return total;
    }

    /**
     * Cancels a pending application for the given company name.
     * @param companyName the name of the company
     * @return true if cancellation succeeded, false otherwise
     */
    public boolean cancelApplication(String companyName) {
        for (Application app : this.applications) {
            if (app.getCompany() != null && app.getCompany().getName().equals(companyName) && app.getStatus() == ApplicationStatus.PENDING) {
                return app.cancel();
            }
        }
        return false;
    }
}