public class MembershipPackage {
    private Award[] awards;

    public MembershipPackage() {}

    public Award[] getAwards() { return awards; }
    public void setAwards(Award[] awards) { this.awards = awards; }
    public boolean hasAward(Award award) { return false; }
}
