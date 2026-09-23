import java.util.ArrayList;
import java.util.List;

enum ApplicationStatus {
    PENDING,
    APPROVAL,
    REJECTED
}

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
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    
    public boolean isEligibleForIPO() { return canApplyForIPO; }
    public void setCanApplyForIPO(boolean canApplyForIPO) { this.canApplyForIPO = canApplyForIPO; }
    
    public List<Application> getApplications() { return applications; }
    public void setApplications(List<Application> applications) { this.applications = applications; }
    
    public boolean createApplication(Company company, int shares, double amount, Document doc) {
        if (!canApplyForIPO) {
            return false;
        }
        if (company == null || shares <= 0 || amount <= 0 || doc == null) {
            return false;
        }
        for (Application app : applications) {
            if (app.getCompany() != null && 
                app.getCompany().getName() != null &&
                app.getCompany().getName().equals(company.getName()) &&
                app.getStatus() == ApplicationStatus.APPROVAL) {
                return false;
            }
        }
        Application app = new Application();
        app.setCustomer(this);
        app.setCompany(company);
        app.setShare(shares);
        app.setAmountOfMoney(amount);
        app.setAllowance(doc);
        app.setStatus(ApplicationStatus.PENDING);
        applications.add(app);
        return true;
    }
    
    public int getApplicationCount() {
        int count = 0;
        for (Application app : applications) {
            if (app.getStatus() == ApplicationStatus.APPROVAL || 
                app.getStatus() == ApplicationStatus.REJECTED) {
                count++;
            }
        }
        return count;
    }
    
    public double getApprovedTotalAmount() {
        double total = 0.0;
        for (Application app : applications) {
            if (app.getStatus() == ApplicationStatus.APPROVAL) {
                total += app.getAmountOfMoney();
            }
        }
        return total;
    }
    
    public boolean cancelApplication(String companyName) {
        for (Application app : applications) {
            if (app.getStatus() == ApplicationStatus.PENDING && 
                app.getCompany() != null &&
                app.getCompany().getName() != null &&
                app.getCompany().getName().equals(companyName)) {
                return app.cancel();
            }
        }
        return false;
    }
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
    
    public int getShare() { return share; }
    public void setShare(int share) { this.share = share; }
    
    public double getAmountOfMoney() { return amountOfMoney; }
    public void setAmountOfMoney(double amountOfMoney) { this.amountOfMoney = amountOfMoney; }
    
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    
    public Document getAllowance() { return allowance; }
    public void setAllowance(Document allowance) { this.allowance = allowance; }
    
    public List<Email> getEmails() { return emails; }
    public void setEmails(List<Email> emails) { this.emails = emails; }
    
    public boolean approve() {
        if (status != ApplicationStatus.PENDING) {
            return false;
        }
        if (customer == null || !customer.isEligibleForIPO()) {
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
        return true;
    }
    
    public void sendEmailsToCustomerAndCompany() {
        if (customer != null && company != null) {
            String content = Email.createEmailContent(customer, company, share, amountOfMoney);
            Email customerEmail = new Email();
            customerEmail.setReceiver(customer.getEmail());
            customerEmail.setContent(content);
            emails.add(customerEmail);
            
            Email companyEmail = new Email();
            companyEmail.setReceiver(company.getEmail());
            companyEmail.setContent(content);
            emails.add(companyEmail);
        }
    }
    
    public void sendRejectionEmail() {
        if (customer != null && company != null) {
            String content = Email.createEmailContent(customer, company, share, amountOfMoney);
            Email email = new Email();
            email.setReceiver(customer.getEmail());
            email.setContent(content);
            emails.add(email);
        }
    }
}

class Company {
    private String name;
    private String email;
    
    public Company() {
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

class Document {
    private String name;
    
    public Document() {
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

class Email {
    private String receiver;
    private String content;
    
    public Email() {
    }
    
    public String getReceiver() { return receiver; }
    public void setReceiver(String receiver) { this.receiver = receiver; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public static String createEmailContent(Customer customer, Company company, int shares, double amount) {
        StringBuilder sb = new StringBuilder();
        sb.append("Customer Name: ").append(customer.getName()).append(" ").append(customer.getSurname()).append("\n");
        sb.append("Customer Email: ").append(customer.getEmail()).append("\n");
        sb.append("Customer Telephone: ").append(customer.getTelephone()).append("\n");
        sb.append("Company Name: ").append(company.getName()).append("\n");
        sb.append("Number of Shares: ").append(shares).append("\n");
        sb.append("Amount Paid: ").append(amount).append("\n");
        return sb.toString();
    }
}