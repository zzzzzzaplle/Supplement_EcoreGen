package com.paperboard.paper;

import com.paperboard.review.ReviewAssignment;
import com.paperboard.review.Grade;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Paper implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private PaperType type;

    private Grade decision;

    private List<ReviewAssignment> reviews;

    public Paper() {
        this.reviews = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PaperType getType() {
        return type;
    }

    public void setType(PaperType type) {
        this.type = type;
    }

    public Grade getDecision() {
        return decision;
    }

    public void setDecision(Grade decision) {
        this.decision = decision;
    }

    public void addReview(ReviewAssignment review) {
        this.reviews.add(review);
    }

    public List<ReviewAssignment> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewAssignment> reviews) {
        this.reviews = reviews;
    }

    public boolean isAllReviewsConsistent() {
        if (reviews == null || reviews.isEmpty()) {
            return false;
        }
        boolean allAccept = true;
        boolean allReject = true;
        for (ReviewAssignment review : reviews) {
            if (review.getGrade() == Grade.ACCEPT) {
                allReject = false;
            } else if (review.getGrade() == Grade.REJECT) {
                allAccept = false;
            } else {
                allAccept = false;
                allReject = false;
            }
        }
        return allAccept || allReject;
    }
}
