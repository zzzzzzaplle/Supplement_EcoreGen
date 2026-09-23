public class CoChair extends UserRole {
    public CoChair() {
    }

    public int makeFinalDecision(Paper paper, Grade decision) {
        paper.setDecision(decision);
        return 0; // Returning 0 as per stub, potentially returning a status code
    }
}
