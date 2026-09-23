import java.util.ArrayList;
import java.util.List;

public class Customer {
    private String name;
    private String surname;
    private String email;
    private String telephone;
    private boolean canApplyForIPO = true;
    private List<Application> applications = new ArrayList<>();

    public Customer() {}

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
        if (!isEligibleForIPO() || shares <= 0 || amount <= 0 || doc == null) {
            return false;
        }
        for (Application app : applications) {
            if (app.getCompany().getName().equals(company.getName()) && app.getStatus() == ApplicationStatus.APPROVAL) {
                return false;
            }
        }
        Application newApp = new Application();
        newApp.setCustomer(this);
        newApp.setCompany(company);
        newApp.setShare(shares);
        newApp.setAmountOfMoney(amount);
        newApp.setAllowance(doc);
        newApp.setStatus(ApplicationStatus.PENDING);
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
            if (app.getCompany().getName().equals(companyName) && app.getStatus() == ApplicationStatus.PENDING) {
                return app.cancel();
            }
        }
        return false;
    }
}
