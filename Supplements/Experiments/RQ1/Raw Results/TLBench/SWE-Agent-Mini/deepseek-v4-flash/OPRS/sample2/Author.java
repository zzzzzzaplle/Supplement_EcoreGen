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
        return papers.size();
    }

    public double calculateAcceptanceRate() {
        if (papers.isEmpty()) {
            return 0.0;
        }
        int accepted = 0;
        for (Paper paper : papers) {
            if (paper.getDecision() == Grade.ACCEPT) {
                accepted++;
            }
        }
        return (double) accepted / papers.size();
    }
}
