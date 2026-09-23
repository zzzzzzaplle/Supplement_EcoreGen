public class FundingGroup {
    private String name;
    private FundingGroupType type;

    public FundingGroup() {
    }

    public FundingGroup(String name, FundingGroupType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FundingGroupType getType() {
        return type;
    }

    public void setType(FundingGroupType type) {
        this.type = type;
    }
}
