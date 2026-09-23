public class CoChair extends UserRole {

    public CoChair() {
    }

    public int makeFinalDecision(Paper paper, Grade decision) {
        if (decision == Grade.ACCEPT || decision == Grade.REJECT) {
            paper.setDecision(decision);
            return 1;
        }
        return 0;
    }
}
