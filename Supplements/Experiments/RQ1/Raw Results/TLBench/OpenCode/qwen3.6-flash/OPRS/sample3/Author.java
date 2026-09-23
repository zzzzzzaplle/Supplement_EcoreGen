package com.paperboard.user;

import com.paperboard.paper.Paper;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Author extends UserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Paper> papers;

    public Author() {
        this.papers = new ArrayList<>();
    }

    public void submitPaper(Paper paper) {
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
        int acceptedCount = 0;
        for (Paper paper : papers) {
            if (paper.getDecision() != null && paper.getDecision() == com.paperboard.review.Grade.ACCEPT) {
                acceptedCount++;
            }
        }
        return (double) acceptedCount / papers.size();
    }

    public List<Paper> getPapers() {
        return papers;
    }

    public void setPapers(List<Paper> papers) {
        this.papers = papers;
    }
}
