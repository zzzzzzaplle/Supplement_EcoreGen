public class ProductionProject extends Project {
    private String siteCode;

    public ProductionProject() {
        super();
    }

    public ProductionProject(String title, String description, double budget, java.util.Date deadline, String siteCode) {
        super(title, description, budget, deadline);
        this.siteCode = siteCode;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }
}
