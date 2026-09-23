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
        if (!isEligibleForIPO() || company == null || doc == null || shares <= 0 || amount <= 0) {
            return false;
        }
        if (applications == null) {
            applications = new ArrayList<Application>();
        }
        for (Application application : applications) {
            if (application != null && application.getCompany() != null && company.getName() != null && company.getName().equals(application.getCompany().getName()) && application.getStatus() == ApplicationStatus.APPROVAL) {
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
        if (applications == null) {
            return 0;
        }
        int count = 0;
        for (Application application : applications) {
            if (application != null && (application.getStatus() == ApplicationStatus.APPROVAL || application.getStatus() == ApplicationStatus.REJECTED)) {
                count++;
            }
        }
        return count;
    }

    public double getApprovedTotalAmount() {
        if (applications == null) {
            return 0;
        }
        double total = 0;
        for (Application application : applications) {
            if (application != null && application.getStatus() == ApplicationStatus.APPROVAL) {
                total += application.getAmountOfMoney();
            }
        }
        return total;
    }

    public boolean cancelApplication(String companyName) {
        if (applications == null || companyName == null) {
            return false;
        }
        for (Application application : applications) {
            if (application != null && application.getCompany() != null && companyName.equals(application.getCompany().getName()) && application.getStatus() == ApplicationStatus.PENDING) {
                return application.cancel();
            }
        }
        return false;
    }
}
