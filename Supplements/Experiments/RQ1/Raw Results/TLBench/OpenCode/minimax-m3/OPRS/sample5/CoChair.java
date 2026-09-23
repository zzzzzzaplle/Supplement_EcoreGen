public class CoChair extends UserRole {

    public CoChair() {
    }

    public int makeFinalDecision(Paper paper, Grade decision) {
        if (paper != null) {
            paper.setDecision(decision);
        }
        return 0;
    }
}
