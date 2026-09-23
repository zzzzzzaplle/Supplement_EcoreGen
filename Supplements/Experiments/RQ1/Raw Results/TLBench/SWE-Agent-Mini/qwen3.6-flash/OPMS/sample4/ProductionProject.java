import java.util.Date;
import java.util.List;

public class ProductionProject extends Project {
    private String siteCode;

    public ProductionProject() {
        super();
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }
}
