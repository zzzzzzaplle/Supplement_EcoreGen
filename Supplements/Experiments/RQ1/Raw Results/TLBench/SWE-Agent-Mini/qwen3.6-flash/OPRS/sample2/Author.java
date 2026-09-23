import java.util.List;

public class Author extends UserRole {
    private List<Paper> papers;

    public Author() {
        this.papers = new java.util.ArrayList<>();
    }

    public void submitPaper(Paper paper) {
        if (paper != null) {
            this.papers.add(paper);
        }
    }

    public int countSubmittedPapers() {
        // Assuming "submitted" means papers with a non-UNDECIDED decision
        // Or it could mean all papers submitted by the author.
        // Based on req 3 "Count the total number of papers submitted by an author",
        // it usually means all papers the author has submitted to the system.
        if (this.papers == null) {
            return 0;
        }
        return this.papers.size();
    }

    public double calculateAcceptanceRate() {
        if (this.papers == null || this.papers.isEmpty()) {
            return 0.0;
        }
        
        int totalPapers = this.papers.size();
        int acceptedPapers = 0;
        
        for (Paper paper : this.papers) {
            if (paper != null && paper.getDecision() == Grade.ACCEPT) {
                acceptedPapers++;
            }
        }
        
        return (double) acceptedPapers / totalPapers;
    }

    public List<Paper> getPapers() {
        return papers;
    }
}
