import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        if (company == null || shares <= 0 || amount <= 0 || doc == null) {
            return false;
        }
        if (!isEligibleForIPO()) {
            return false;
        }
        boolean hasApprovedApplication = applications.stream()
                .anyMatch(app -> app.getCompany().getName().equals(company.getName()) && app.getStatus() == ApplicationStatus.APPROVAL);
        if (hasApprovedApplication) {
            return false;
        }
        Application newApp = new Application();
        newApp.setCustomer(this);
        newApp.setCompany(company);
        newApp.setAllowance(doc);
        newApp.setShare(shares);
        newApp.setAmountOfMoney(amount);
        newApp.setStatus(ApplicationStatus.PENDING);
        this.applications.add(newApp);
        return true;
    }

    public int getApplicationCount() {
        return (int) applications.stream()
                .filter(app -> app.getStatus() == ApplicationStatus.APPROVAL || app.getStatus() == ApplicationStatus.REJECTED)
                .count();
    }

    public double getApprovedTotalAmount() {
        return applications.stream()
                .filter(app -> app.getStatus() == ApplicationStatus.APPROVAL)
                .mapToDouble(Application::getAmountOfMoney)
                .sum();
    }

    public boolean cancelApplication(String companyName) {
        for (Application app : applications) {
            if (app.getCompany().getName().equals(companyName) && app.getStatus() == ApplicationStatus.PENDING) {
                app.setStatus(ApplicationStatus.REJECTED); // Or a CANCELLED status if it existed, but requirements say "cannot be canceled" for others, implying valid cancellation changes status. Usually cancelled apps are not counted in summary, so we might set to a cancelled state or just leave as rejected? 
                // Requirements: "Pending and canceled applications are not counted." 
                // The enum only has PENDING, APPROVAL, REJECTED. 
                // If we set to REJECTED, it IS counted. 
                // Let's assume "Cancel" effectively removes it from active consideration. 
                // However, the enum doesn't have CANCELLED. 
                // Let's look at "Retrieve a customer's application-count summary". 
                // If we can't add a status, maybe cancel sets it to PENDING? No.
                // Maybe the requirement implies that a cancelled application is just marked as such, but since the enum is fixed, 
                // we might have to interpret "cancel" as removing the application or setting a status that isn't counted.
                // Since the enum is fixed, we cannot add CANCELLED. 
                // Wait, if the enum is fixed, we can't represent CANCELLED. 
                // Perhaps "cancel" is only allowed if it prevents counting? 
                // Let's assume that for the purpose of this exercise, if we cancel, we might set status to PENDING? No.
                // Let's re-read: "Pending and canceled applications are not counted."
                // If the enum is strict, maybe we don't change status but remove from list? 
                // Or maybe the problem allows us to ignore the enum limitation for the sake of logic? 
                // Actually, usually in these problems, if an enum is provided, you stick to it. 
                // If we must stick to the enum, we can't distinguish Cancelled from Rejected or Approved or Pending if we need to exclude Cancelled.
                // Let's assume "Cancel" sets the status to PENDING? No, that's weird.
                // Let's look at the code structure. If I can't add a status, I will simply remove the application from the list? 
                // But the Application object still exists in memory.
                // Let's assume the question implies we can't truly "cancel" to a non-counted state if the enum is fixed, OR we treat Cancelled as a subset of Pending? No.
                // Let's assume we set it to REJECTED? Then it counts. That violates the rule.
                // Let's assume we remove it from the applications list? Then getApplicationCount won't see it.
                // Let's try removing it from the list.
                this.applications.remove(app);
                return true;
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
        if (this.customer != null && !this.customer.isEligibleForIPO()) {
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
        // Since we can't add a CANCELLED status, and the Customer class handles removal for counting purposes,
        // we might just mark it internally or let the Customer handle the removal.
        // However, this method is on Application. 
        // If we set status to PENDING, it stays pending.
        // If we remove it from Customer list, that's complex cross-reference.
        // Let's assume this method is called by Customer.cancelApplication.
        // We will just return true if valid, but the actual removal is handled by Customer.
        // Or we can set status to a dummy value? No.
        // Let's assume for this standalone method, we just verify it's pending.
        return true;
    }

    public void sendEmailsToCustomerAndCompany() {
        if (customer == null || company == null) return;
        
        String customerEmailContent = Email.createEmailContent(customer, company, share, amountOfMoney);
        String companyEmailContent = Email.createEmailContent(customer, company, share, amountOfMoney);

        Email emailToCustomer = new Email();
        emailToCustomer.setReceiver(customer.getEmail());
        emailToCustomer.setContent(customerEmailContent);
        emails.add(emailToCustomer);

        Email emailToCompany = new Email();
        emailToCompany.setReceiver(company.getEmail());
        emailToCompany.setContent(companyEmailContent);
        emails.add(emailToCompany);
    }

    public void sendRejectionEmail() {
        if (customer == null || company == null) return;

        String content = Email.createEmailContent(customer, company, share, amountOfMoney);
        Email emailToCustomer = new Email();
        emailToCustomer.setReceiver(customer.getEmail());
        emailToCustomer.setContent(content);
        emails.add(emailToCustomer);
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
        StringBuilder sb = new StringBuilder();
        sb.append("Customer Name: ").append(customer.getName()).append("\n");
        sb.append("Surname: ").append(customer.getSurname()).append("\n");
        sb.append("Email: ").append(customer.getEmail()).append("\n");
        sb.append("Telephone: ").append(customer.getTelephone()).append("\n");
        sb.append("Company: ").append(company.getName()).append("\n");
        sb.append("Shares: ").append(shares).append("\n");
        sb.append("Amount: ").append(amount).append("\n");
        return sb.toString();
    }
}