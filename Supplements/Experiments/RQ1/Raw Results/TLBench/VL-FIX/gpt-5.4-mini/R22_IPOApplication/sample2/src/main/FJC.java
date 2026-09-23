import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        if (company == null || doc == null || shares <= 0 || amount <= 0) {
            return false;
        }
        if (!isEligibleForIPO()) {
            return false;
        }
        for (Application application : applications) {
            if (application != null
                    && application.getCompany() != null
                    && application.getCompany().getName() != null
                    && company.getName() != null
                    && application.getCompany().getName().equals(company.getName())
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

    public int getApplicationCount() {
        int count = 0;
        for (Application application : applications) {
            if (application != null && (application.getStatus() == ApplicationStatus.APPROVAL
                    || application.getStatus() == ApplicationStatus.REJECTED)) {
                count++;
            }
        }
        return count;
    }

    public double getApprovedTotalAmount() {
        double total = 0.0;
        for (Application application : applications) {
            if (application != null && application.getStatus() == ApplicationStatus.APPROVAL) {
                total += application.getAmountOfMoney();
            }
        }
        return total;
    }

    public boolean cancelApplication(String companyName) {
        if (companyName == null) {
            return false;
        }
        for (Application application : applications) {
            if (application != null
                    && application.getStatus() == ApplicationStatus.PENDING
                    && application.getCompany() != null
                    && Objects.equals(application.getCompany().getName(), companyName)) {
                return application.cancel();
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
        this.status = ApplicationStatus.PENDING;
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
        if (status != ApplicationStatus.PENDING || customer == null || !customer.isEligibleForIPO()) {
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
        status = ApplicationStatus.REJECTED;
        return true;
    }

    public void sendEmailsToCustomerAndCompany() {
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

    public void sendRejectionEmail() {
        if (customer == null || company == null) {
            return;
        }
        String content = Email.createEmailContent(customer, company, share, amountOfMoney);

        Email rejectionEmail = new Email();
        rejectionEmail.setReceiver(customer.getEmail());
        rejectionEmail.setContent("IPO Application Rejected\n" + content);

        emails.add(rejectionEmail);
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
        sb.append("Customer Name: ").append(customer != null ? customer.getName() : "").append('\n');
        sb.append("Customer Surname: ").append(customer != null ? customer.getSurname() : "").append('\n');
        sb.append("Customer Email: ").append(customer != null ? customer.getEmail() : "").append('\n');
        sb.append("Customer Telephone: ").append(customer != null ? customer.getTelephone() : "").append('\n');
        sb.append("Company Name: ").append(company != null ? company.getName() : "").append('\n');
        sb.append("Number of Shares: ").append(shares).append('\n');
        sb.append("Paid Amount: ").append(amount);
        return sb.toString();
    }
}