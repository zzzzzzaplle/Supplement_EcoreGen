import java.util.ArrayList;
import java.util.List;

public class Author extends UserRole {
    private List<Paper> papers;

    public Author() {
        this.papers = new ArrayList<Paper>();
    }

    public void submitPaper(Paper paper) {
        if (papers == null) {
            papers = new ArrayList<Paper>();
        }
        papers.add(paper);
    }

    public int countSubmittedPapers() {
        if (papers == null) {
            return 0;
        }
        return papers.size();
    }

    public double calculateAcceptanceRate() {
        if (papers == null || papers.size() == 0) {
            return 0.0;
        }
        int accepted = 0;
        for (Paper paper : papers) {
            if (paper != null && paper.getDecision() == Grade.ACCEPT) {
                accepted++;
            }
        }
        return (double) accepted / (double) papers.size();
    }

    public List<Paper> getPapers() {
        return papers;
    }

    public void setPapers(List<Paper> papers) {
        this.papers = papers;
    }
}
