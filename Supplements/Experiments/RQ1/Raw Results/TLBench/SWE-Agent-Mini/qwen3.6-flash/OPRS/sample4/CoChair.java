public class CoChair extends UserRole {

    public CoChair() {
    }

    /**
     * Make a final decision for a paper.
     * Returns 1 if accepted, 0 if rejected/invalid.
     */
    public int makeFinalDecision(Paper paper, Grade decision) {
        if (paper != null && decision != null) {
            paper.setDecision(decision);
            if (decision == Grade.ACCEPT) {
                return 1;
            } else if (decision == Grade.REJECT) {
                return 0;
            }
        }
        return 0;
    }
}
