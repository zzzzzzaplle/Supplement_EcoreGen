import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

enum ApplicationStatus {
    PENDING, APPROVAL, REJECTED
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
               ", Company: " + company.getName() + ", Shares: " + shares +
               ", Amount: " + amount;
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

class Customer {
    private String name;
    private String surname;
    private String email;
    private String telephone;
    private boolean canApplyForIPO;
    private List<Application> applications = new ArrayList<>();

    public Customer() {
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
        if (!isEligibleForIPO() || shares <= 0 || amount <= 0 || doc == null) return false;
        if (applications.stream().anyMatch(a -> a.getCompany().getName().equals(company.getName()) && a.getStatus() != ApplicationStatus.REJECTED)) return false;

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
        return (int) applications.stream().filter(a -> a.getStatus() == ApplicationStatus.APPROVAL || a.getStatus() == ApplicationStatus.REJECTED).count();
    }

    public double getApprovedTotalAmount() {
        return applications.stream().filter(a -> a.getStatus() == ApplicationStatus.APPROVAL).mapToDouble(Application::getAmountOfMoney).sum();
    }

    public boolean cancelApplication(String companyName) {
        for (Application app : applications) {
            if (app.getCompany().getName().equals(companyName) && app.getStatus() == ApplicationStatus.PENDING) {
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
    private List<Email> emails = new ArrayList<>();

    public Application() {
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
        if (status != ApplicationStatus.PENDING || !customer.isEligibleForIPO()) return false;
        status = ApplicationStatus.APPROVAL;
        sendEmailsToCustomerAndCompany();
        return true;
    }

    public boolean reject() {
        if (status != ApplicationStatus.PENDING) return false;
        status = ApplicationStatus.REJECTED;
        sendRejectionEmail();
        return true;
    }

    public boolean cancel() {
        if (status != ApplicationStatus.PENDING) return false;
        customer.getApplications().remove(this);
        return true;
    }

    public void sendEmailsToCustomerAndCompany() {
        String content = Email.createEmailContent(customer, company, share, amountOfMoney);
        Email e1 = new Email(); e1.setReceiver(customer.getEmail()); e1.setContent(content);
        Email e2 = new Email(); e2.setReceiver(company.getEmail()); e2.setContent(content);
        emails.add(e1); emails.add(e2);
    }

    public void sendRejectionEmail() {
        Email e = new Email();
        e.setReceiver(customer.getEmail());
        e.setContent(Email.createEmailContent(customer, company, share, amountOfMoney));
        emails.add(e);
    }
}