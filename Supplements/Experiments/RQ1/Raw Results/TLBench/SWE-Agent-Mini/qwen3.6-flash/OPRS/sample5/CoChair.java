public class CoChair extends UserRole {

    public CoChair() {
    }

    public int makeFinalDecision(Paper paper, Grade decision) {
        int countAccepted = 0;
        int countRejected = 0;
        
        for (ReviewAssignment review : paper.getReviews()) {
            if (review.getGrade() == Grade.ACCEPT) {
                countAccepted++;
            } else if (review.getGrade() == Grade.REJECT) {
                countRejected++;
            }
        }
        
        paper.setDecision(decision);
        
        // Return 1 if accepted, 0 otherwise (or simply count based on decision)
        // The requirement says "make final decision", so we set it and return a meaningful int.
        // Let's return 1 if accepted, 0 if rejected to be consistent with boolean-like outcome encoded as int.
        if (decision == Grade.ACCEPT) {
            return 1;
        } else {
            return 0;
        }
    }
}
