public class CoChair extends UserRole {

    public CoChair() {
        super();
    }

    public int makeFinalDecision(Paper paper, Grade decision) {
        if (paper == null || decision == null) {
            return 0;
        }
        paper.setDecision(decision);
        return 1;
    }
}
