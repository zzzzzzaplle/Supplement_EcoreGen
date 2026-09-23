public class CoChair extends UserRole {
    public CoChair() {
    }

    public void makeFinalDecision(Paper paper, Grade decision) {
        paper.setDecision(decision);
    }
}
