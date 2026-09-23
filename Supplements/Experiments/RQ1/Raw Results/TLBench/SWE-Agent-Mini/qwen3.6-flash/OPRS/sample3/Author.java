import java.util.ArrayList;
import java.util.List;

public class Author extends UserRole {
    private List<Paper> papers;

    public Author() {
        this.papers = new ArrayList<>();
    }

    public List<Paper> getPapers() {
        return papers;
    }

    public void submitPaper(Paper paper) {
        this.papers.add(paper);
    }

    // Functional Requirement 3: Count total papers
    public int countSubmittedPapers() {
        return papers.size();
    }

    // Functional Requirement 4: Calculate acceptance rate
    public double calculateAcceptanceRate() {
        if (papers == null || papers.isEmpty()) {
            return 0.0;
        }
        int acceptedCount = 0;
        for (Paper p : papers) {
            if (p != null && p.getDecision() == Grade.ACCEPT) {
                acceptedCount++;
            }
        }
        return (double) acceptedCount / papers.size();
    }
}
