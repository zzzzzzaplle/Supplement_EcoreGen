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
        if (shares <= 0 || amount <= 0.0 || doc == null) {
            return false;
        }
        // Check if there is already an approved application for the same company
        for (Application app : applications) {
            if (app.getCompany().getName().equals(company.getName()) && app.getStatus() == ApplicationStatus.APPROVAL) {
                return false;
            }
        }
        Application newApp = new Application();
        newApp.setShare(shares);
        newApp.setAmountOfMoney(amount);
        newApp.setStatus(ApplicationStatus.PENDING);
        newApp.setCustomer(this);
        newApp.setCompany(company);
        newApp.setAllowance(doc);
        newApp.setEmails(new ArrayList<>());
        applications.add(newApp);
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
                return app.cancel();
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
        this.status = ApplicationStatus.REJECTED; // or could be a separate CANCELED status, but design uses REJECTED
        return true;
    }

    public void sendEmailsToCustomerAndCompany() {
        // Email to customer
        Email customerEmail = new Email();
        customerEmail.setReceiver(this.customer.getEmail());
        customerEmail.setContent(Email.createEmailContent(this.customer, this.company, this.share, this.amountOfMoney));
        this.emails.add(customerEmail);

        // Email to company
        Email companyEmail = new Email();
        companyEmail.setReceiver(this.company.getEmail());
        companyEmail.setContent("IPO application approved for " + this.customer.getName() + " " + this.customer.getSurname());
        this.emails.add(companyEmail);
    }

    public void sendRejectionEmail() {
        Email rejectionEmail = new Email();
        rejectionEmail.setReceiver(this.customer.getEmail());
        String content = "Dear " + this.customer.getName() + " " + this.customer.getSurname() + ",\n" +
                "Your IPO application for company " + this.company.getName() + " has been rejected.\n" +
                "Details:\n" +
                "Name: " + this.customer.getName() + "\n" +
                "Surname: " + this.customer.getSurname() + "\n" +
                "Email: " + this.customer.getEmail() + "\n" +
                "Telephone: " + this.customer.getTelephone() + "\n" +
                "Company: " + this.company.getName() + "\n" +
                "Shares: " + this.share + "\n" +
                "Amount Paid: " + this.amountOfMoney;
        rejectionEmail.setContent(content);
        this.emails.add(rejectionEmail);
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
        return "Dear " + customer.getName() + " " + customer.getSurname() + ",\n" +
                "Your IPO application for company " + company.getName() + " has been approved.\n" +
                "Details:\n" +
                "Shares: " + shares + "\n" +
                "Amount: " + amount;
    }
}