public class CoChair extends UserRole {
    public CoChair() {}

    public int makeFinalDecision(Paper paper, Grade decision) {
        paper.setDecision(decision);
        return 0; // Returning 0 as a placeholder as return type in diagram is int but no specified meaning
    }
}
