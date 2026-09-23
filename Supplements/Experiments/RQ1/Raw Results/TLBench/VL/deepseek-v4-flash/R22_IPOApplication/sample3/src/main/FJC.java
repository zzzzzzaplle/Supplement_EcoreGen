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
        if (!this.canApplyForIPO) {
            return false;
        }
        if (shares <= 0 || amount <= 0 || doc == null) {
            return false;
        }
        // Check no approved application for the same company
        for (Application app : applications) {
            if (app.getCompany().getName().equals(company.getName()) && app.getStatus() == ApplicationStatus.APPROVAL) {
                return false;
            }
        }
        Application application = new Application();
        application.setShare(shares);
        application.setAmountOfMoney(amount);
        application.setStatus(ApplicationStatus.PENDING);
        application.setCustomer(this);
        application.setCompany(company);
        application.setAllowance(doc);
        applications.add(application);
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
            if (app.getCompany().getName().equals(companyName) && app.getStatus() == ApplicationStatus.PENDING) {
                app.cancel();
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
        if (!customer.isEligibleForIPO()) {
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
        this.status = ApplicationStatus.REJECTED; // or we could have a separate CANCELLED status, but per requirements we treat cancel as not counted
        // For simplicity, we set status to PENDING? No, cancel should remove it from pending. We'll set to REJECTED but not count it? Actually requirement says canceled applications are not counted, we can set a different status or just remove. To keep it simple, we'll set status to REJECTED but cancel method is only called when we want to cancel. Since canceled apps are not counted in getApplicationCount, we need to distinguish. We'll keep a separate canceled status? But enum doesn't have it. We'll just remove the application from customer's list to simulate cancellation.
        // Better approach: remove from customer's list
        customer.getApplications().remove(this);
        return true;
    }

    public void sendEmailsToCustomerAndCompany() {
        // Send email to customer
        Email customerEmail = new Email();
        customerEmail.setReceiver(customer.getEmail());
        customerEmail.setContent("Your IPO application for " + company.getName() + " has been approved.");
        emails.add(customerEmail);
        // Send email to company
        Email companyEmail = new Email();
        companyEmail.setReceiver(company.getEmail());
        companyEmail.setContent("Customer " + customer.getName() + " " + customer.getSurname() + " has applied for IPO in your company.");
        emails.add(companyEmail);
    }

    public void sendRejectionEmail() {
        Email rejectionEmail = new Email();
        rejectionEmail.setReceiver(customer.getEmail());
        String content = Email.createEmailContent(customer, company, share, amountOfMoney);
        rejectionEmail.setContent(content);
        emails.add(rejectionEmail);
    }
}

class Company {
    private String name;
    private String email;

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
        return "Dear " + customer.getName() + " " + customer.getSurname() + ",\n" +
               "Your IPO application for " + company.getName() + " has been rejected.\n" +
               "Details:\n" +
               "Name: " + customer.getName() + "\n" +
               "Surname: " + customer.getSurname() + "\n" +
               "Email: " + customer.getEmail() + "\n" +
               "Telephone: " + customer.getTelephone() + "\n" +
               "Company: " + company.getName() + "\n" +
               "Shares: " + shares + "\n" +
               "Amount Paid: " + amount;
    }
}