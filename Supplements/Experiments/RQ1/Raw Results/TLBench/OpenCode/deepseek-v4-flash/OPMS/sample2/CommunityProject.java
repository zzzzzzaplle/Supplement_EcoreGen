public class CommunityProject extends Project {
    private FundingGroup fundingGroup;

    public CommunityProject() {
    }

    public FundingGroup getFundingGroup() {
        return fundingGroup;
    }

    public void setFundingGroup(FundingGroup group) {
        this.fundingGroup = group;
    }
}
