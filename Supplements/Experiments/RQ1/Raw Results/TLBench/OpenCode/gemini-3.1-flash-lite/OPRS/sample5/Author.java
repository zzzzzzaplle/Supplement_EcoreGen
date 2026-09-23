public class Author extends UserRole {
    private java.util.List<Paper> papers = new java.util.ArrayList<>();

    public Author() {}

    public java.util.List<Paper> getPapers() { return papers; }
    public void submitPaper(Paper paper) { this.papers.add(paper); }

    public int countSubmittedPapers() {
        return papers.size();
    }

    public double calculateAcceptanceRate() {
        if (papers.isEmpty()) return 0.0;
        long acceptedCount = papers.stream()
            .filter(p -> p.getDecision() == Grade.ACCEPT)
            .count();
        return (double) acceptedCount / papers.size();
    }
}
