public class CoChair extends UserRole {
    
    public CoChair() {}

    public int makeFinalDecision(Paper paper, Grade decision) {
        paper.setDecision(decision);
        return 0; // Keeping return type as implied by diagram logic, though it's effectively void
    }
}
