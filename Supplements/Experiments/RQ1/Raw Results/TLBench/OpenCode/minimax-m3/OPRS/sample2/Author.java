import java.util.ArrayList;
import java.util.List;

public class Author extends UserRole {
    private List<Paper> papers;

    public Author() {
        super();
        this.papers = new ArrayList<Paper>();
    }

    public void submitPaper(Paper paper) {
        if (paper != null) {
            this.papers.add(paper);
        }
    }

    public int countSubmittedPapers() {
        return this.papers.size();
    }

    public double calculateAcceptanceRate() {
        if (this.papers == null || this.papers.isEmpty()) {
            return 0.0;
        }
        int accepted = 0;
        for (Paper p : this.papers) {
            if (p != null && p.getDecision() == Grade.ACCEPT) {
                accepted++;
            }
        }
        return (double) accepted / (double) this.papers.size();
    }

    public List<Paper> getPapers() {
        return papers;
    }

    public void setPapers(List<Paper> papers) {
        this.papers = papers;
    }
}
