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
        this.name = "";
        this.surname = "";
        this.email = "";
        this.telephone = "";
        this.canApplyForIPO = true;
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
        if (company == null || doc == null) {
            return false;
        }
        if (!isEligibleForIPO() || shares <= 0 || amount <= 0) {
            return false;
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

    public int getApplicationCount() {
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
                    && application.getCompany() != null
                    && Objects.equals(application.getCompany().getName(), companyName)
                    && application.getStatus() == ApplicationStatus.PENDING) {
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
        this.share = 0;
        this.amountOfMoney = 0.0;
        this.status = ApplicationStatus.PENDING;
        this.customer = null;
        this.company = null;
        this.allowance = null;
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
        status = ApplicationStatus.REJECTED;
        return true;
    }

    public void sendEmailsToCustomerAndCompany() {
        if (customer == null || company == null) {
            return;
        }
        emails.add(new Email(customer.getEmail(),
                Email.createEmailContent(customer, company, share, amountOfMoney)));
        emails.add(new Email(company.getEmail(),
                Email.createEmailContent(customer, company, share, amountOfMoney)));
    }

    public void sendRejectionEmail() {
        if (customer == null || company == null) {
            return;
        }
        String content = Email.createEmailContent(customer, company, share, amountOfMoney);
        emails.add(new Email(customer.getEmail(), content));
    }
}

class Company {
    private String name;
    private String email;

    public Company() {
        this.name = "";
        this.email = "";
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
        this.name = "";
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
        this.receiver = "";
        this.content = "";
    }

    public Email(String receiver, String content) {
        this.receiver = receiver;
        this.content = content;
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
        StringBuilder builder = new StringBuilder();
        if (customer != null) {
            builder.append("Customer Name: ").append(nullToEmpty(customer.getName())).append('\n');
            builder.append("Customer Surname: ").append(nullToEmpty(customer.getSurname())).append('\n');
            builder.append("Customer Email: ").append(nullToEmpty(customer.getEmail())).append('\n');
            builder.append("Customer Telephone: ").append(nullToEmpty(customer.getTelephone())).append('\n');
        } else {
            builder.append("Customer Name: ").append('\n');
            builder.append("Customer Surname: ").append('\n');
            builder.append("Customer Email: ").append('\n');
            builder.append("Customer Telephone: ").append('\n');
        }
        if (company != null) {
            builder.append("Company Name: ").append(nullToEmpty(company.getName())).append('\n');
        } else {
            builder.append("Company Name: ").append('\n');
        }
        builder.append("Purchased Shares: ").append(shares).append('\n');
        builder.append("Paid Amount: ").append(amount);
        return builder.toString();
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}