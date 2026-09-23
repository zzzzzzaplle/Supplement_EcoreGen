import java.util.List;
import java.util.ArrayList;

public class Author extends UserRole {
    private List<Paper> papers;
    
    public Author() {
        this.papers = new ArrayList<>();
    }
    
    public List<Paper> getPapers() {
        return papers;
    }
    
    public void setPapers(List<Paper> papers) {
        this.papers = papers;
    }
    
    public void submitPaper(Paper paper) {
        if (papers == null) {
            papers = new ArrayList<>();
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
        if (papers == null || papers.isEmpty()) {
            return 0.0;
        }
        int acceptedCount = 0;
        for (Paper paper : papers) {
            Grade decision = paper.getDecision();
            if (decision == Grade.ACCEPT) {
                acceptedCount++;
            }
        }
        return (double) acceptedCount / papers.size();
    }
}
