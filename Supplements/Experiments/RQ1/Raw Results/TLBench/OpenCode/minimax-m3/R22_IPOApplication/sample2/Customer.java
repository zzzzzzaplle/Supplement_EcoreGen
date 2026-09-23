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
        this.applications = new ArrayList<Application>();
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
        if (company == null || shares <= 0 || amount <= 0 || doc == null) {
            return false;
        }
        if (!this.canApplyForIPO) {
            return false;
        }
        for (Application app : this.applications) {
            if (app.getStatus() == ApplicationStatus.APPROVAL
                    && app.getCompany() != null
                    && app.getCompany().getName() != null
                    && app.getCompany().getName().equals(company.getName())) {
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
        this.applications.add(app);
        return true;
    }

    public int getApplicationCount() {
        int count = 0;
        for (Application app : this.applications) {
            if (app.getStatus() == ApplicationStatus.APPROVAL
                    || app.getStatus() == ApplicationStatus.REJECTED) {
                count++;
            }
        }
        return count;
    }

    public double getApprovedTotalAmount() {
        double total = 0.0;
        for (Application app : this.applications) {
            if (app.getStatus() == ApplicationStatus.APPROVAL) {
                total += app.getAmountOfMoney();
            }
        }
        return total;
    }

    public boolean cancelApplication(String companyName) {
        if (companyName == null) {
            return false;
        }
        for (Application app : this.applications) {
            if (app.getStatus() == ApplicationStatus.PENDING
                    && app.getCompany() != null
                    && companyName.equals(app.getCompany().getName())) {
                return app.cancel();
            }
        }
        return false;
    }
}
