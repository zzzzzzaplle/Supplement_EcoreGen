import java.util.ArrayList;
import java.util.List;

public class Author extends UserRole {
    private List<Paper> papers;

    public Author() {
        this.papers = new ArrayList<Paper>();
    }

    public void submitPaper(Paper paper) {
        if (this.papers == null) {
            this.papers = new ArrayList<Paper>();
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
        if (papers == null || papers.isEmpty()) {
            return 0.0;
        }
        int accepted = 0;
        for (Paper paper : papers) {
            if (paper != null && paper.getDecision() == Grade.ACCEPT) {
                accepted++;
            }
        }
        return ((double) accepted) / papers.size();
    }

    public List<Paper> getPapers() {
        return papers;
    }

    public void setPapers(List<Paper> papers) {
        this.papers = papers;
    }
}
