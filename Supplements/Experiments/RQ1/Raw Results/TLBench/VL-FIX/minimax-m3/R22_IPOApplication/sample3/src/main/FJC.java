import java.util.ArrayList;
import java.util.List;

enum ApplicationStatus {
    PENDING,
    APPROVAL,
    REJECTED,
    CANCELED
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
        return "Customer Name: " + customer.getName() + " " + customer.getSurname() +
               "\nCustomer Email: " + customer.getEmail() +
               "\nCustomer Telephone: " + customer.getTelephone() +
               "\nCompany Name: " + company.getName() +
               "\nNumber of Shares: " + shares +
               "\nAmount Paid: " + amount;
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
        status = ApplicationStatus.CANCELED;
        return true;
    }
    
    public void sendEmailsToCustomerAndCompany() {
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
    
    public void sendRejectionEmail() {
        String content = Email.createEmailContent(customer, company, share, amountOfMoney);
        
        Email rejectionEmail = new Email();
        rejectionEmail.setReceiver(customer.getEmail());
        rejectionEmail.setContent(content);
        emails.add(rejectionEmail);
    }
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
    
    public boolean createApplication(Company company, int shares, double amount, Document doc) {
        if (!canApplyForIPO) {
            return false;
        }
        if (shares <= 0) {
            return false;
        }
        if (amount <= 0) {
            return false;
        }
        if (doc == null) {
            return false;
        }
        for (Application app : applications) {
            if (app.getCompany().getName().equals(company.getName())
                && app.getStatus() == ApplicationStatus.APPROVAL) {
                return false;
            }
        }
        
        Application app = new Application();
        app.setShare(shares);
        app.setAmountOfMoney(amount);
        app.setStatus(ApplicationStatus.PENDING);
        app.setCustomer(this);
        app.setCompany(company);
        app.setAllowance(doc);
        applications.add(app);
        return true;
    }
    
    public int getApplicationCount() {
        int count = 0;
        for (Application app : applications) {
            if (app.getStatus() == ApplicationStatus.APPROVAL
                || app.getStatus() == ApplicationStatus.REJECTED) {
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
            if (app.getCompany().getName().equals(companyName)
                && app.getStatus() == ApplicationStatus.PENDING) {
                return app.cancel();
            }
        }
        return false;
    }
}