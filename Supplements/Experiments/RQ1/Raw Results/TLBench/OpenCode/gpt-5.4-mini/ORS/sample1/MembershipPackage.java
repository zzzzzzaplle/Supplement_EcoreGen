public class MembershipPackage {
    private Award[] awards;

    public MembershipPackage() {
    }

    public Award[] getAwards() {
        return awards;
    }

    public void setAwards(Award[] awards) {
        this.awards = awards;
    }

    public boolean hasAward(Award award) {
        if (award == null || awards == null) {
            return false;
        }
        for (Award current : awards) {
            if (current == award) {
                return true;
            }
        }
        return false;
    }
}
