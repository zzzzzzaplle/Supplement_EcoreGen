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

    public int countSubmittedPapers() {
        return this.papers.size();
    }

    public double calculateAcceptanceRate() {
        if (papers.isEmpty()) return 0.0;
        long acceptedCount = papers.stream()
                .filter(p -> p.getDecision() == Grade.ACCEPT)
                .count();
        return (double) acceptedCount / papers.size();
    }
}
