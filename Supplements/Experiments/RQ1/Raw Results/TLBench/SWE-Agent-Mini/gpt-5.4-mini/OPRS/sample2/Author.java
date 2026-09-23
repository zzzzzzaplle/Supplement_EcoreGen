import java.util.ArrayList;
import java.util.List;

public class Author extends UserRole {
    private List<Paper> papers;

    public Author() {
        this.papers = new ArrayList<Paper>();
    }

    public void submitPaper(Paper paper) {
        getPapers().add(paper);
    }

    public int countSubmittedPapers() {
        return getPapers().size();
    }

    public double calculateAcceptanceRate() {
        List<Paper> list = getPapers();
        if (list.isEmpty()) {
            return 0.0;
        }
        int accepted = 0;
        for (Paper paper : list) {
            if (paper != null && paper.getDecision() == Grade.ACCEPT) {
                accepted++;
            }
        }
        return ((double) accepted) / list.size();
    }

    public List<Paper> getPapers() {
        if (papers == null) {
            papers = new ArrayList<Paper>();
        }
        return papers;
    }

    public void setPapers(List<Paper> papers) {
        this.papers = papers;
    }
}
