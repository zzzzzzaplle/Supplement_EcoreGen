public class CoChair extends UserRole {
    public CoChair() {
    }

    public int makeFinalDecision(Paper paper, Grade decision) {
        paper.setDecision(decision);
        return 0; // Return value not explicitly defined, so return 0 as dummy
    }
}
