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
        if (!isEligibleForIPO() || shares <= 0 || amount <= 0 || doc == null) {
            return false;
        }
        for (Application a : applications) {
            if (a.getCompany().getName().equals(company.getName()) && a.getStatus() != ApplicationStatus.REJECTED) {
                return false;
            }
        }
        Application app = new Application();
        app.setCompany(company);
        app.setShare(shares);
        app.setAmountOfMoney(amount);
        app.setAllowance(doc);
        app.setCustomer(this);
        app.setStatus(ApplicationStatus.PENDING);
        applications.add(app);
        return true;
    }

    public int getApplicationCount() {
        int count = 0;
        for (Application a : applications) {
            if (a.getStatus() == ApplicationStatus.APPROVAL || a.getStatus() == ApplicationStatus.REJECTED) {
                count++;
            }
        }
        return count;
    }

    public double getApprovedTotalAmount() {
        double total = 0;
        for (Application a : applications) {
            if (a.getStatus() == ApplicationStatus.APPROVAL) {
                total += a.getAmountOfMoney();
            }
        }
        return total;
    }

    public boolean cancelApplication(String companyName) {
        for (Application a : applications) {
            if (a.getCompany().getName().equals(companyName) && a.getStatus() == ApplicationStatus.PENDING) {
                applications.remove(a);
                return true;
            }
        }
        return false;
    }
}
