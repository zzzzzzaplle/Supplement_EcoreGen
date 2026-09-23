import java.util.List;

public class Author extends UserRole {
    private List<Paper> papers;

    public Author() {
    }

    public List<Paper> getPapers() {
        return papers;
    }

    public void setPapers(List<Paper> papers) {
        this.papers = papers;
    }

    public void submitPaper(Paper paper) {
        if (this.papers == null) {
            this.papers = new java.util.ArrayList<>();
        }
        this.papers.add(paper);
    }

    public int countSubmittedPapers() {
        if (papers == null) {
            return 0;
        }
        return papers.size();
    }

    /**
     * Calculate the acceptance rate of papers for an author.
     * Example: 10 papers, 6 accepted -> 0.60
     */
    public double calculateAcceptanceRate() {
        int totalPapers = countSubmittedPapers();
        if (totalPapers == 0) {
            return 0.0;
        }
        
        int acceptedCount = 0;
        for (Paper paper : papers) {
            if (paper.getDecision() == Grade.ACCEPT) {
                acceptedCount++;
            }
        }
        
        return (double) acceptedCount / totalPapers;
    }
}
