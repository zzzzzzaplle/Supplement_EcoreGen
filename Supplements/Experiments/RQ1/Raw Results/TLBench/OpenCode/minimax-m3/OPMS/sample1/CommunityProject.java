public class CommunityProject extends Project {
    private FundingGroup fundingGroup;

    public CommunityProject() {
    }

    public FundingGroup getFundingGroup() {
        return this.fundingGroup;
    }

    public void setFundingGroup(FundingGroup fundingGroup) {
        this.fundingGroup = fundingGroup;
    }
}
