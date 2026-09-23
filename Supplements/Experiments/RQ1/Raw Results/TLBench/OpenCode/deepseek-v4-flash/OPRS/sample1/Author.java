import java.util.ArrayList;
import java.util.List;

public class Author extends UserRole {
    private List<Paper> papers;

    public Author() {
        this.papers = new ArrayList<>();
    }

    public void submitPaper(Paper paper) {
        papers.add(paper);
    }

    public int countSubmittedPapers() {
        return papers.size();
    }

    public double calculateAcceptanceRate() {
        if (papers.isEmpty()) {
            return 0.0;
        }
        int accepted = 0;
        for (Paper p : papers) {
            if (p.getDecision() == Grade.ACCEPT) {
                accepted++;
            }
        }
        return (double) accepted / papers.size();
    }

    public List<Paper> getPapers() {
        return papers;
    }
}
