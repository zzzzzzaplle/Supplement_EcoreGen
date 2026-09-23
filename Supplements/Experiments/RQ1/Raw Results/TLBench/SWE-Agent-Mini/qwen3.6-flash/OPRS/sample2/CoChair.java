public class CoChair extends UserRole {
    public CoChair() {
    }

    public int makeFinalDecision(Paper paper, Grade decision) {
        if (paper == null || decision == null) {
            return -1;
        }
        
        // Ensure all reviews are submitted and consistent before making a decision
        if (!paper.isAllReviewsConsistent()) {
            return -1;
        }
        
        paper.setDecision(decision);
        
        if (decision == Grade.ACCEPT) {
            return 1;
        } else if (decision == Grade.REJECT) {
            return 0;
        }
        
        return -1;
    }
}
