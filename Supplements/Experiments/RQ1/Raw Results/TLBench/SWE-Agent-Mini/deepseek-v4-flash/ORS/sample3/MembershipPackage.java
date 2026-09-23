import java.util.Arrays;

public class MembershipPackage {
    private Award[] awards;

    public MembershipPackage() {
        this.awards = new Award[0];
    }

    public Award[] getAwards() {
        return awards;
    }

    public void setAwards(Award[] awards) {
        this.awards = awards;
    }

    public boolean hasAward(Award award) {
        if (awards == null) {
            return false;
        }
        for (Award a : awards) {
            if (a == award) {
                return true;
            }
        }
        return false;
    }
}
