import java.util.List;

public class Author extends UserRole {
    private List<Paper> papers;

    public Author() {
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

    public double calculateAcceptanceRate() {
        int total = countSubmittedPapers();
        if (total == 0) {
            return 0.0;
        }
        int accepted = 0;
        for (Paper paper : papers) {
            if (paper.getDecision() == Grade.ACCEPT) {
                accepted++;
            }
        }
        return (double) accepted / total;
    }

    public List<Paper> getPapers() {
        return papers;
    }
}
