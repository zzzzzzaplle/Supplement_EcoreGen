public class Customer {
    private String name;
    private String surname;
    private String email;
    private String telephone;
    private boolean canApplyForIPO;
    private java.util.List<Application> applications;

    public Customer() {
        this.applications = new java.util.ArrayList<Application>();
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

    public java.util.List<Application> getApplications() {
        return applications;
    }

    public void setApplications(java.util.List<Application> applications) {
        this.applications = applications;
    }

    public boolean createApplication(Company company, int shares, double amount, Document doc) {
        if (!isEligibleForIPO()) {
            return false;
        }
        if (company == null || doc == null) {
            return false;
        }
        if (shares <= 0 || amount <= 0) {
            return false;
        }
        if (applications == null) {
            applications = new java.util.ArrayList<Application>();
        }
        for (Application existing : applications) {
            if (existing.getCompany() != null
                    && existing.getCompany().getName() != null
                    && existing.getCompany().getName().equals(company.getName())
                    && existing.getStatus() == ApplicationStatus.APPROVAL) {
                return false;
            }
        }
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
        if (applications == null) {
            return 0;
        }
        int count = 0;
        for (Application app : applications) {
            if (app.getStatus() == ApplicationStatus.APPROVAL
                    || app.getStatus() == ApplicationStatus.REJECTED) {
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
        for (Application app : applications) {
            if (app.getStatus() == ApplicationStatus.APPROVAL) {
                total += app.getAmountOfMoney();
            }
        }
        return total;
    }

    public boolean cancelApplication(String companyName) {
        if (applications == null || companyName == null) {
            return false;
        }
        for (Application app : applications) {
            if (app.getCompany() != null
                    && companyName.equals(app.getCompany().getName())
                    && app.getStatus() == ApplicationStatus.PENDING) {
                return app.cancel();
            }
        }
        return false;
    }
}
