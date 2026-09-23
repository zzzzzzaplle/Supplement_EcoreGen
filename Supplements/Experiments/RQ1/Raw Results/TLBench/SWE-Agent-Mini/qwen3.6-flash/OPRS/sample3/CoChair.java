public class CoChair extends UserRole {
    
    public CoChair() {
    }

    // Makes a final decision for a paper
    public int makeFinalDecision(Paper paper, Grade decision) {
        if (paper != null) {
            paper.setDecision(decision);
        }
        // Return 1 to indicate success
        return 1;
    }
}
