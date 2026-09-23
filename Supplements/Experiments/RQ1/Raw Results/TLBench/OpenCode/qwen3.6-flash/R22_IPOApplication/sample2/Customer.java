import java.util.ArrayList;
import java.util.List;

public class Customer {
    private String name;
    private String surname;
    private String email;
    private String telephone;
    private boolean canApplyForIPO;
    private List<Application> applications;

    public Customer() {
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
        if (!canApplyForIPO || shares <= 0 || amount <= 0 || doc == null) {
            return false;
        }
        if (applications == null) {
            applications = new ArrayList<>();
        }
        for (Application app : applications) {
            if (app.getCompany() != null
                    && app.getCompany().getName().equals(company.getName())
                    && app.getStatus() == ApplicationStatus.APPROVAL) {
                return false;
            }
        }
        Application application = new Application();
        application.setCompany(company);
        application.setShare(shares);
        application.setAmountOfMoney(amount);
        application.setAllowance(doc);
        application.setCustomer(this);
        application.setStatus(ApplicationStatus.PENDING);
        application.setEmails(new ArrayList<>());
        applications.add(application);
        return true;
    }

    public int getApplicationCount() {
        if (applications == null) {
            return 0;
        }
        int count = 0;
        for (Application app : applications) {
            ApplicationStatus s = app.getStatus();
            if (s == ApplicationStatus.APPROVAL || s == ApplicationStatus.REJECTED) {
                count++;
            }
        }
        return count;
    }

    public double getApprovedTotalAmount() {
        if (applications == null) {
            return 0.0;
        }
        double total = 0.0;
        for (Application app : applications) {
            if (app.getStatus() == ApplicationStatus.APPROVAL) {
                total += app.getAmountOfMoney();
            }
        }
        return total;
    }

    public boolean cancelApplication(String companyName) {
        if (applications == null) {
            return false;
        }
        for (Application app : applications) {
            if (app.getCompany() != null
                    && app.getCompany().getName().equals(companyName)
                    && app.getStatus() == ApplicationStatus.PENDING) {
                app.cancel();
                return true;
            }
        }
        return false;
    }
}
