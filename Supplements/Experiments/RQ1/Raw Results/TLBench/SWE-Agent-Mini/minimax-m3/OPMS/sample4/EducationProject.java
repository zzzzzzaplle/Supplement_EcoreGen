public class EducationProject extends Project {
    private FundingGroup fundingGroup;

    public EducationProject() {
        super();
    }

    public EducationProject(String title, String description, double budget, java.util.Date deadline, FundingGroup fundingGroup) {
        super(title, description, budget, deadline);
        this.fundingGroup = fundingGroup;
    }

    public FundingGroup getFundingGroup() {
        return fundingGroup;
    }

    public void setFundingGroup(FundingGroup fundingGroup) {
        this.fundingGroup = fundingGroup;
    }
}
