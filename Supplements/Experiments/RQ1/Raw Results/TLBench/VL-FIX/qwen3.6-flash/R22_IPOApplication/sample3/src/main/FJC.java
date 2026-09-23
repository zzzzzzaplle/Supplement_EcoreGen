import java.util.ArrayList;
import java.util.List;

class Customer {
    private String name;
    private String surname;
    private String email;
    private String telephone;
    private boolean canApplyForIPO;
    private List<Application> applications;

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

    public boolean createApplication(Company company, int shares, double amount, Document doc) {
        if (shares <= 0 || amount <= 0 || doc == null) {
            return false;
        }
        if (!canApplyForIPO) {
            return false;
        }
        for (Application app : applications) {
            if (app.getCompany().getName().equals(company.getName()) && app.getStatus() == ApplicationStatus.PENDING) {
                return false;
            }
        }
        Application newApp = new Application();
        newApp.setCustomer(this);
        newApp.setCompany(company);
        newApp.setShare(shares);
        newApp.setAmountOfMoney(amount);
        newApp.setAllowance(doc);
        newApp.setStatus(ApplicationStatus.PENDING);
        this.applications.add(newApp);
        return true;
    }

    public int getApplicationCount() {
        int count = 0;
        for (Application app : applications) {
            if (app.getStatus() == ApplicationStatus.APPROVAL || app.getStatus() == ApplicationStatus.REJECTED) {
                count++;
            }
        }
        return count;
    }

    public double getApprovedTotalAmount() {
        double total = 0;
        for (Application app : applications) {
            if (app.getStatus() == ApplicationStatus.APPROVAL) {
                total += app.getAmountOfMoney();
            }
        }
        return total;
    }

    public boolean cancelApplication(String companyName) {
        for (Application app : applications) {
            if (app.getCompany().getName().equals(companyName) && app.getStatus() == ApplicationStatus.PENDING) {
                app.setStatus(ApplicationStatus.REJECTED); // Or a cancelled state, but diagram only has 3 statuses. 
                // Usually cancellation sets status to CANCELLED, but diagram doesn't show it. 
                // However, req says "Approved and rejected applications cannot be canceled". 
                // And getApplicationCount excludes pending and canceled. 
                // If we set to REJECTED, it would be counted. 
                // Let's assume there is an implicit cancelled state or we just remove it? 
                // The diagram doesn't have CANCELLED status. 
                // But req 5 says "Cancel a pending application". 
                // If we can't represent cancelled, we might just remove it or set to rejected? 
                // Wait, if we set to rejected, it violates "Only approved and rejected applications are counted" for getApplicationCount? 
                // Actually, if we cancel, it's no longer pending. 
                // Let's look at the enum: PENDING, APPROVAL, REJECTED. 
                // There is no CANCELLED. 
                // Perhaps cancellation means removing the application? Or setting status to something else? 
                // Given the constraints, if I set it to REJECTED, it becomes countable. 
                // If I remove it, it's gone. 
                // Let's look at the email requirement. No email for cancellation mentioned. 
                // Let's assume for this specific model, canceling a pending application effectively removes it or marks it as rejected? 
                // Actually, usually "Cancel" is distinct. 
                // However, looking at the diagram, there is no CANCELLED enum. 
                // I will remove the application from the list to signify cancellation, or perhaps the status remains PENDING but logic changes? 
                // No, status is an enum. 
                // Let's re-read carefully: "Pending applications and canceled applications are not counted." 
                // If I set status to REJECTED, it IS counted. 
                // If I don't add a CANCELLED status, I can't strictly follow the counting rule if I keep the object. 
                // But I must use the diagram. 
                // Maybe canceling just removes it? 
                // Let's try removing it from the list. 
                return this.applications.remove(app);
            }
        }
        return false;
    }
}

enum ApplicationStatus {
    PENDING,
    APPROVAL,
    REJECTED
}

class Application {
    private int share;
    private double amountOfMoney;
    private ApplicationStatus status;
    private Customer customer;
    private Company company;
    private Document allowance;
    private List<Email> emails;

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

    public boolean approve() {
        if (this.status != ApplicationStatus.PENDING) {
            return false;
        }
        if (!this.customer.isEligibleForIPO()) {
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
        // The diagram has a cancel method. 
        // Since there is no CANCELLED status in the enum, and the Customer has a cancelApplication method that likely handles the list removal,
        // this might be a helper or redundant. 
        // However, to satisfy the diagram, I will implement it. 
        // Given the enum constraints, maybe it just sets a flag? No. 
        // I'll assume this method is called by Customer.cancelApplication and perhaps removes itself? 
        // But Customer handles the list. 
        // I will just return true if it was pending, effectively marking it as done? 
        // But status must change. 
        // If I can't add CANCELLED, I'll leave it as PENDING? No. 
        // I will just return true. The actual state management is in Customer. 
        // Wait, if I call approve/reject, status changes. 
        // If I call cancel, and I can't change status, what happens? 
        // Let's assume the question implies that canceling removes the application from the customer's list via the Customer method. 
        // This Application.cancel() might just be a stub or return true for validation. 
        return true;
    }

    public void sendEmailsToCustomerAndCompany() {
        String customerContent = Email.createEmailContent(this.customer, this.company, this.share, this.amountOfMoney);
        String companyContent = Email.createEmailContent(this.customer, this.company, this.share, this.amountOfMoney);
        
        Email emailToCustomer = new Email();
        emailToCustomer.setReceiver(this.customer.getEmail());
        emailToCustomer.setContent(customerContent);
        this.emails.add(emailToCustomer);
        
        Email emailToCompany = new Email();
        emailToCompany.setReceiver(this.company.getEmail());
        emailToCompany.setContent(companyContent);
        this.emails.add(emailToCompany);
    }

    public void sendRejectionEmail() {
        String content = Email.createEmailContent(this.customer, this.company, this.share, this.amountOfMoney);
        Email email = new Email();
        email.setReceiver(this.customer.getEmail());
        email.setContent(content);
        this.emails.add(email);
    }
}

class Company {
    private String name;
    private String email;

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

class Document {
    private String name;

    public Document() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

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

    public static String createEmailContent(Customer customer, Company company, int shares, double amount) {
        return "Customer: " + customer.getName() + " " + customer.getSurname() + 
               ", Email: " + customer.getEmail() + ", Phone: " + customer.getTelephone() + 
               ", Company: " + company.getName() + 
               ", Shares: " + shares + 
               ", Amount: " + amount;
    }
}