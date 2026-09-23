import java.util.Date;
import java.util.List;

public class EducationProject extends Project {
    private FundingGroup fundingGroup;

    public EducationProject() {
        super();
    }

    public FundingGroup getFundingGroup() {
        return fundingGroup;
    }

    public void setFundingGroup(FundingGroup group) {
        this.fundingGroup = group;
    }
}
