public class CoChair extends UserRole {
    public CoChair() {
    }

    public int makeFinalDecision(Paper paper, Grade decision) {
        if (paper == null || decision == null) {
            return -1;
        }
        paper.setDecision(decision);
        return 0;
    }
}
